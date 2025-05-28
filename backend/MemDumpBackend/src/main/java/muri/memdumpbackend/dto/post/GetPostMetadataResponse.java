package muri.memdumpbackend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetPostMetadataResponse {
    private String id;
    private String author;
    private String title;
    private LocalDateTime created;
    private Integer score;
    private List<String> tags;
}
