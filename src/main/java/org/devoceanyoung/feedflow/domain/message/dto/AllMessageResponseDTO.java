package org.devoceanyoung.feedflow.domain.message.dto;

import java.time.LocalDateTime;

public record AllMessageResponseDTO(
        String role,
        String content,
        LocalDateTime createdAt
) {
}
