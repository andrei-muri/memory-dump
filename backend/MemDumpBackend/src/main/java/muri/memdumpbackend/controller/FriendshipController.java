package muri.memdumpbackend.controller;

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
import muri.memdumpbackend.service.FriendshipService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/friendship")
@AllArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;


    @PostMapping("/request")
    public SimpleMessageResponse request(@RequestBody FriendshipRequest friendshipRequest) {
        this.friendshipService.request(friendshipRequest);
        return new SimpleMessageResponse("Request sent successfully");
    }

    @PostMapping("/accept/{requestId}")
    public SimpleMessageResponse accept(@PathVariable String requestId) {
        this.friendshipService.accept(requestId);
        return new SimpleMessageResponse("Request accepted");
    }

    @PostMapping("/unfriend")
    public SimpleMessageResponse unfriend(@RequestBody UnfriendRequest unfriendRequest) {
        this.friendshipService.unfriend(unfriendRequest);
        return new SimpleMessageResponse("Request unfriend successfully done");
    }

    @GetMapping("/get/{username}")
    public GetFriendshipsResponse get(@PathVariable String username) {
        return this.friendshipService.getFriendships(username);
    }

    @GetMapping("/get/requests/{username}")
    public GetRequestsResponse getRequests(@PathVariable String username) {
        return this.friendshipService.getRequests(username);
    }

    @GetMapping("/status")
    public String getFriendshipStatus(@RequestParam String user1, @RequestParam String user2) {
        return friendshipService.getStatus(user1, user2);
    }

}
