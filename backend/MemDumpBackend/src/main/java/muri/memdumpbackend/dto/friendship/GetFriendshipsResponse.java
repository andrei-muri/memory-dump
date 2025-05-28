package muri.memdumpbackend.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetFriendshipsResponse {
    public record FriendshipResponse(String userId, String username) {}
    private List<FriendshipResponse> friendships;
}
