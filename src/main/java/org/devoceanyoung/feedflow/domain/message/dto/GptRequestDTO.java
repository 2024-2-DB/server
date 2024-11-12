package org.devoceanyoung.feedflow.domain.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GptRequestDTO(
        String model,
        @JsonProperty("max_tokens") Integer maxTokens,
        Double temperature,
        Boolean stream,
        List<Map<String, String>> messages
) {
}
