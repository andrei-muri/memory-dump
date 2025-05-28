package muri.memdumpbackend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import muri.memdumpbackend.model.ProgrammingLanguagePercentage;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetPostByIdResponse {
    private String id;
    private String author;
    private String title;
    private String content;
    private LocalDateTime created;
    private List<String> tags;
    private List<ProgrammingLanguagePercentage> programmingLanguagePercentages;
    private Integer score;
}
