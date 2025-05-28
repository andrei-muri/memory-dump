package muri.memdumpbackend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostCreateDTO {
    private String author;
    private String title;
    private String content;
    private List<String> tags;
}
