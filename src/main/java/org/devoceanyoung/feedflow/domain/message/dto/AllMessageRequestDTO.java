package org.devoceanyoung.feedflow.domain.message.dto;

import java.time.LocalDateTime;

public record AllMessageRequestDTO(
        Long teamId,
        Long userId

) {
}
