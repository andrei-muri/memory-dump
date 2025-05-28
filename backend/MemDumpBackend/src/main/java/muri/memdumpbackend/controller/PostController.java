package muri.memdumpbackend.controller;

import lombok.AllArgsConstructor;
import muri.memdumpbackend.dto.SimpleMessageResponse;
import muri.memdumpbackend.dto.post.*;
import muri.memdumpbackend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
@CrossOrigin
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<SimpleMessageResponse> createPost(@RequestBody PostCreateDTO dto) {
        postService.createPost(dto);
        return ResponseEntity.status(201).body(new SimpleMessageResponse("Post created successfully"));
    }

    @GetMapping("/get/{id}")
    public GetPostByIdResponse getPost(@PathVariable String id) {
        return postService.getPostById(id);
    }

    @GetMapping("get/all")
    public List<GetPostMetadataResponse> getAllPosts(
            @RequestParam(required = false) String id,
            @RequestParam(required = false, defaultValue = "created") String sort,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Integer limit
    ) {
        return postService.getAllPosts(id, sort, tags, limit);
    }

    @PostMapping("/vote")
    public int vote(@RequestBody VoteRequest voteRequest) {
        return postService.vote(voteRequest);

    }

    @PostMapping("/comment")
    public ResponseEntity<SimpleMessageResponse> comment(@RequestBody CommentRequest commentRequest) {
        postService.comment(commentRequest);
        return  ResponseEntity.status(201).body(new SimpleMessageResponse("Commented successfully"));
    }
}
