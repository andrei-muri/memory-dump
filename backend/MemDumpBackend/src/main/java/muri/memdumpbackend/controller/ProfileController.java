package muri.memdumpbackend.controller;

import lombok.AllArgsConstructor;
import muri.memdumpbackend.dto.SimpleMessageResponse;
import muri.memdumpbackend.dto.profile.GetProfileDTO;
import muri.memdumpbackend.dto.profile.ProfileCreateDTO;
import muri.memdumpbackend.service.PostService;
import muri.memdumpbackend.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
@CrossOrigin
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SimpleMessageResponse> createProfile(@ModelAttribute ProfileCreateDTO dto) {
        profileService.createProfile(dto);
        return ResponseEntity.status(201).body(new SimpleMessageResponse("Profile created successfully"));
    }

    @GetMapping("/picture/{username}")
    public ResponseEntity<byte[]>  profilePicture(@PathVariable String username) {
        return this.profileService.getProfilePicture(username).
                map(profilePicture -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE)
                        .body(profilePicture)
                )
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get/{username}")
    public GetProfileDTO getProfile(@PathVariable String username) {
        return profileService.getProfile(username);
    }

    @GetMapping("exists/{username}")
    public ResponseEntity<SimpleMessageResponse> exists(@PathVariable String username) {
        return profileService.exists(username);
    }

}
