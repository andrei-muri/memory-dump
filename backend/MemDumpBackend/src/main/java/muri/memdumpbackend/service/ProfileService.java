package muri.memdumpbackend.service;

import lombok.AllArgsConstructor;
import muri.memdumpbackend.dto.SimpleMessageResponse;
import muri.memdumpbackend.dto.post.GetPostMetadataResponse;
import muri.memdumpbackend.dto.profile.GetProfileDTO;
import muri.memdumpbackend.dto.profile.ProfileCreateDTO;
import muri.memdumpbackend.exception.CreationException;
import muri.memdumpbackend.exception.ResourceNotFoundException;
import muri.memdumpbackend.model.Profile;
import muri.memdumpbackend.model.Tag;
import muri.memdumpbackend.model.User;
import muri.memdumpbackend.repo.ProfileRepository;
import muri.memdumpbackend.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final PostService postService;

    public void createProfile(ProfileCreateDTO profileCreateDTO) {
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepository.findByUsername(loggedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!loggedUsername.equalsIgnoreCase(profileCreateDTO.getUsername())) {
            throw new CreationException("You can create a profile only for yourself");
        }
        if (!isValidImage(profileCreateDTO.getProfilePicture()))
            throw new CreationException("Profile image is invalid");
        Optional.ofNullable(user.getProfile())
                .ifPresent((profile) -> {
                    throw new CreationException("User " + user.getUsername() + " profile already exists");
                });
        Profile profile;
        List<Tag> tags = tagService.convertToListOfTags(profileCreateDTO.getTags());
        try {
            profile = Profile.builder()
                    .fullName(profileCreateDTO.getFullName())
                    .description(profileCreateDTO.getDescription())
                    .profilePicture(profileCreateDTO.getProfilePicture().getBytes())
                    .preferredTags(tags)
                    .build();
        } catch (IOException e) {
            throw new CreationException("Profile image is invalid");
        }

        user.setProfile(profile);
        userRepository.save(user);
    }

    public Optional<byte[]> getProfilePicture(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return Optional.ofNullable(user.getProfile().getProfilePicture());
    }

    public GetProfileDTO getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Profile profile = user.getProfile();
        List<GetPostMetadataResponse> first5PostMetadata = postService.getAllPosts(user.getId().toString(), null, null, 5);
        return GetProfileDTO.builder()
                .fullName(profile.getFullName())
                .description(profile.getDescription())
                .preferredTags(tagService.convertToListOfStringTags(profile.getPreferredTags()))
                .postsMetadata(first5PostMetadata)
                .build();

    }

    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equalsIgnoreCase("image/jpg") ||
                        contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/png")
        );
    }

    public ResponseEntity<SimpleMessageResponse> exists(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getProfile() == null) {
            return ResponseEntity.ok(new SimpleMessageResponse("false"));
        }
        return ResponseEntity.ok(new SimpleMessageResponse("true"));
    }
}
