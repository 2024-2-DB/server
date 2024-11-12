package org.devoceanyoung.feedflow.domain.message.dto;

public record AIMessageRequestDTO(
        Long teamId,
        Long userId,
        boolean isFirst

) {
}
