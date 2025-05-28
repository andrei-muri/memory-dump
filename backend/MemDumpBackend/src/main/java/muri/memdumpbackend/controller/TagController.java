package muri.memdumpbackend.controller;

import lombok.AllArgsConstructor;
import muri.memdumpbackend.dto.SimpleMessageResponse;
import muri.memdumpbackend.dto.tag.TagCreateDTO;
import muri.memdumpbackend.model.Tag;
import muri.memdumpbackend.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
@AllArgsConstructor
@CrossOrigin
public class TagController {
    private final TagService tagService;

    @PostMapping("/create")
    public ResponseEntity<SimpleMessageResponse> createTag(@RequestBody TagCreateDTO dto) {
        tagService.createTag(dto);
        return ResponseEntity.status(201).body(new SimpleMessageResponse("Tag created successfully"));
    }

    @GetMapping("/get")
    public List<Tag> getTags() {
        return tagService.getTags();
    }
}
