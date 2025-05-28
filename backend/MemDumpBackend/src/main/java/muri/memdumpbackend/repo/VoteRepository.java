package muri.memdumpbackend.repo;

import muri.memdumpbackend.model.Post;
import muri.memdumpbackend.model.User;
import muri.memdumpbackend.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    Optional<Vote> findByAuthorAndPost(User user, Post post);
}
