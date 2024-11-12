package org.devoceanyoung.feedflow.domain.message.dto;

public record UserMessageDTO(
        Long teamId,
        Long userId,
        String content

) {
}
