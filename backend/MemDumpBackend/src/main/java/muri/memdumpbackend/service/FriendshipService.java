package muri.memdumpbackend.service;

import lombok.AllArgsConstructor;
import muri.memdumpbackend.dto.SimpleMessageResponse;
import muri.memdumpbackend.dto.friendship.FriendshipRequest;
import muri.memdumpbackend.dto.friendship.GetFriendshipsResponse;
import muri.memdumpbackend.dto.friendship.GetRequestsResponse;
import muri.memdumpbackend.dto.friendship.UnfriendRequest;
import muri.memdumpbackend.exception.FriendshipException;
import muri.memdumpbackend.model.Friendship;
import muri.memdumpbackend.model.User;
import muri.memdumpbackend.repo.FriendshipRepository;
import muri.memdumpbackend.repo.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;


    @Transactional
    public void request(FriendshipRequest friendshipRequest) {
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (friendshipRequest.from().equalsIgnoreCase(friendshipRequest.to())) {
            throw new FriendshipException("Cannot be friend with yourself");
        } else if (!loggedUsername.equalsIgnoreCase(friendshipRequest.from())) {
            throw new FriendshipException("You cannot make requests on behalf of another person");
        }
        User from = userRepository.findByUsername(friendshipRequest.from())
                .orElseThrow(() -> new FriendshipException("User that sent the request not found"));
        User to = userRepository.findByUsername(friendshipRequest.to())
                .orElseThrow(() -> new FriendshipException("Receiver of the request not found"));
        checkIfFriendshipExists(from, to);
        Friendship friendship = Friendship.builder()
                .user1(from)
                .user2(to)
                .status(Friendship.Status.PENDING)
                .build();
        friendshipRepository.save(friendship);
    }

    @Transactional
    public void accept(String requestId) {
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedUser = userRepository.findByUsername(loggedUsername)
                .orElseThrow(() -> new FriendshipException("User that sent the request not found"));
        Friendship friendship = friendshipRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new FriendshipException("Friendship request not found"));
        if (!friendship.getUser2().getUsername().equalsIgnoreCase(loggedUsername)) {
            throw new FriendshipException("You can only accept your friendship requests");
        }
        friendship.setStatus(Friendship.Status.APPROVED);
        friendshipRepository.save(friendship);
    }

    @Transactional
    public void unfriend(UnfriendRequest unfriendRequest) {
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loggedUsername.equalsIgnoreCase(unfriendRequest.from())) {
            throw new FriendshipException("You cannot make requests on behalf of another person");
        }
        User user1 = userRepository.findByUsername(unfriendRequest.from()).orElseThrow(() -> new FriendshipException("User that sent the request not found"));
        User user2 = userRepository.findByUsername(unfriendRequest.to()).orElseThrow(() -> new FriendshipException("User to not found"));

        friendshipRepository.findByUser1AndUser2(user1, user2)
                .ifPresent(friendshipRepository::delete);
        friendshipRepository.findByUser1AndUser2(user2, user1)
                .ifPresent(friendshipRepository::delete);
    }

    public GetFriendshipsResponse getFriendships(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new FriendshipException("User that sent the request not found"));
        List<GetFriendshipsResponse.FriendshipResponse> friendships1 =
                friendshipRepository.findByUser1(user).stream()
                        .filter(friendship -> friendship.getStatus().equals(Friendship.Status.APPROVED))
                        .map(friendship -> new GetFriendshipsResponse.FriendshipResponse(friendship.getUser2().getId().toString(), friendship.getUser2().getUsername()))
                        .toList();
        List<GetFriendshipsResponse.FriendshipResponse> friendships2 =
                friendshipRepository.findByUser2(user).stream()
                        .filter(friendship -> friendship.getStatus().equals(Friendship.Status.APPROVED))
                        .map(friendship -> new GetFriendshipsResponse.FriendshipResponse(friendship.getUser1().getId().toString(), friendship.getUser1().getUsername()))
                        .toList();;

        return GetFriendshipsResponse.builder()
                .friendships(Stream.concat(friendships1.stream(), friendships2.stream()).toList())
                .build();

    }

    public GetRequestsResponse getRequests(String username) {
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loggedUsername.equalsIgnoreCase(username)) {
            throw new FriendshipException("You can see only your friendship requests");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new FriendshipException("User that sent the request not found"));
        List<GetRequestsResponse.RequestResponse> friendshipRequests =
                friendshipRepository.findByUser2(user).stream()
                        .filter(friendship -> friendship.getStatus().equals(Friendship.Status.PENDING))
                        .map(friendship -> new GetRequestsResponse.RequestResponse(friendship.getId().toString(), friendship.getUser1().getUsername()))
                        .toList();
        return GetRequestsResponse.builder()
                .requests(friendshipRequests)
                .build();
    }

    private void checkIfFriendshipExists(User from, User to) {
        friendshipRepository.findByUser1AndUser2(from, to)
                .ifPresent((friendship) -> {
                    if (friendship.getStatus().equals(Friendship.Status.PENDING)) {
                        throw new FriendshipException("Friendship has already been sent");
                    } else if (friendship.getStatus().equals(Friendship.Status.APPROVED)) {
                        throw new FriendshipException("Friendship has already been approved");
                    }
                });
        friendshipRepository.findByUser1AndUser2(to, from)
                .ifPresent((friendship) -> {
                    if (friendship.getStatus().equals(Friendship.Status.PENDING)) {
                        throw new FriendshipException("Friendship has already been sent");
                    } else if (friendship.getStatus().equals(Friendship.Status.APPROVED)) {
                        throw new FriendshipException("Friendship has already been approved");
                    }
                });
    }

    public String getStatus(String user1, String user2) {
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loggedUsername.equals(user1)) {
            throw new SecurityException("You can only check your own friendship status");
        }
        User from = userRepository.findByUsername(user1)
                .orElseThrow(() -> new FriendshipException("User not found"));
        User to = userRepository.findByUsername(user2)
                .orElseThrow(() -> new FriendshipException("Target user not found"));
        Optional<Friendship> friendship1 = friendshipRepository.findByUser1AndUser2(from, to);
        Optional<Friendship> friendship2 = friendshipRepository.findByUser1AndUser2(to, from);

        if (friendship1.isPresent() && friendship1.get().getStatus() == Friendship.Status.APPROVED) {
            return "FRIENDS";
        } else if (friendship2.isPresent() && friendship2.get().getStatus() == Friendship.Status.APPROVED) {
            return "FRIENDS";
        } else if (friendship1.isPresent() && friendship1.get().getStatus() == Friendship.Status.PENDING) {
            return "PENDING";
        } else if (friendship2.isPresent() && friendship2.get().getStatus() == Friendship.Status.PENDING) {
            return "PENDING";
        }
        return "NOT_FRIENDS";
    }
}
