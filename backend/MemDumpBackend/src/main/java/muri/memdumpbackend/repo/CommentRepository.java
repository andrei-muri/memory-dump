package muri.memdumpbackend.repo;

import muri.memdumpbackend.model.Comment;
import muri.memdumpbackend.model.Post;
import muri.memdumpbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Optional<Comment> findByAuthorAndPost(User user, Post post);
}
