package muri.memdumpbackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import muri.memdumpbackend.dto.message.MessageDTO;
import muri.memdumpbackend.dto.message.SendMessageDTO;
import muri.memdumpbackend.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody SendMessageDTO dto) {
        MessageDTO messageDTO = messageService.sendMessage(dto);
        return ResponseEntity.status(201).body(messageDTO);
    }

    @GetMapping("/{friendUsername}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageDTO>> getChatHistory(@PathVariable String friendUsername) {
        List<MessageDTO> messages = messageService.getChatHistory(friendUsername);
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/message/send")
    public void sendMessageViaWebSocket(SendMessageDTO dto) {
        messageService.sendMessage(dto);
    }

    @SubscribeMapping("/messages")
    public List<MessageDTO> getMessagesOnSubscribe() {
        log.debug("In MessageController.getMessagesOnSubscribe()");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return messageService.getAllMessagesForUser(username);
    }
}
