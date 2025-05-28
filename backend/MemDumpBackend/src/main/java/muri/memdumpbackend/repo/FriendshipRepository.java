package muri.memdumpbackend.repo;

import muri.memdumpbackend.model.Friendship;
import muri.memdumpbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    Optional<Friendship> findByUser1AndUser2(User user1, User user2);
    List<Friendship> findByUser1(User user1);
    List<Friendship> findByUser2(User user2);
    Optional<Friendship> findByUser1IdAndUser2IdAndStatusOrUser2IdAndUser1IdAndStatus(
            UUID user1_id, UUID user2_id, Friendship.Status status, UUID user2_id2, UUID user1_id2, Friendship.Status status2
    );
}
