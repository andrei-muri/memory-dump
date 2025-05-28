package muri.memdumpbackend.dto.message;

public record SendMessageDTO(
   String recipientUsername,
   String content
) {}
