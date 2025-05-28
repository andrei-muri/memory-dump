package muri.memdumpbackend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import muri.memdumpbackend.model.VoteType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoteRequest {
    private VoteType voteType;
    private String username;
    private String postId;
}
