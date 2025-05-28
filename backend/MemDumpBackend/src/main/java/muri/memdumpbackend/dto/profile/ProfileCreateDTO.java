package muri.memdumpbackend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCreateDTO {
    private String username;
    private String fullName;
    private String description;
    private MultipartFile profilePicture;
    private List<String> tags;
}
