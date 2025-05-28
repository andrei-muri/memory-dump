package muri.memdumpbackend.dto.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDTO(
        UUID id,
        String senderUsername,
        String recipientUsername,
        String content,
        LocalDateTime timestamp,
        boolean delivered
) {}
