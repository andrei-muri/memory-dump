package muri.memdumpbackend.repo;

import muri.memdumpbackend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByRecipientIdAndDeliveredFalse(UUID recipientId);
    List<Message> findBySenderIdAndRecipientIdOrRecipientIdAndSenderId(
            UUID userId1, UUID userId2, UUID userId2Again, UUID userId1Again);
    List<Message> findByRecipientIdOrSenderId(UUID recipientId, UUID senderId);
}
