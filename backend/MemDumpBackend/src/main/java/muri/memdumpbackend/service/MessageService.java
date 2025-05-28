package muri.memdumpbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import muri.memdumpbackend.dto.message.MessageDTO;
import muri.memdumpbackend.dto.message.SendMessageDTO;
import muri.memdumpbackend.exception.CreationException;
import muri.memdumpbackend.model.Friendship;
import muri.memdumpbackend.model.Message;
import muri.memdumpbackend.model.User;
import muri.memdumpbackend.repo.FriendshipRepository;
import muri.memdumpbackend.repo.MessageRepository;
import muri.memdumpbackend.repo.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageDTO sendMessage(SendMessageDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new CreationException("Sender not found"));
        User recipient = userRepository.findByUsername(dto.recipientUsername())
                .orElseThrow(() -> new CreationException("Recipient not found"));
        if (username.equals(recipient.getUsername())) {
            throw new CreationException("Cannot send message to yourself");
        }

        if (!areFriends(sender.getId(), recipient.getId())) {
            throw new CreationException("You can only message friends");
        }

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(dto.content())
                .timestamp(LocalDateTime.now())
                .delivered(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        MessageDTO messageDTO = toMessageDTO(savedMessage);

        messagingTemplate.convertAndSendToUser(
                dto.recipientUsername(), "/queue/messages", messageDTO);

        log.info("Sent message from {} to {}", username, dto.recipientUsername());
        return messageDTO;
    }

    @Transactional
    public List<MessageDTO> getAllMessagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CreationException("User not found"));
        List<Message> messages = messageRepository.findByRecipientIdOrSenderId(user.getId(), user.getId());

        messages.stream()
                .filter(m -> !m.isDelivered() && m.getRecipient().getId().equals(user.getId()))
                .forEach(m -> m.setDelivered(true));

        messageRepository.saveAll(messages);

        return messages.stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getChatHistory(String friendUsername) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CreationException("User not found"));
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new CreationException("Friend not found"));

        if (areFriends(user.getId(), friend.getId())) {
            throw new CreationException("You can only view chat history with friends");
        }

        return messageRepository.findBySenderIdAndRecipientIdOrRecipientIdAndSenderId(
                        user.getId(), friend.getId(), friend.getId(), user.getId())
                .stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    private boolean areFriends(UUID userId, UUID friendId) {
        Friendship friendship = friendshipRepository.findByUser1IdAndUser2IdAndStatusOrUser2IdAndUser1IdAndStatus(
                userId, friendId, Friendship.Status.APPROVED,
                userId, friendId, Friendship.Status.APPROVED).orElse(null);
        return friendship != null;
    }

    private MessageDTO toMessageDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getUsername(),
                message.getRecipient().getUsername(),
                message.getContent(),
                message.getTimestamp(),
                message.isDelivered()
        );
    }
}
