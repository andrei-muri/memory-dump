package muri.memdumpbackend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import muri.memdumpbackend.dto.post.GetPostMetadataResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetProfileDTO {
    private String fullName;
    private String description;
    private List<String> preferredTags;
    private List<GetPostMetadataResponse> postsMetadata;
}
