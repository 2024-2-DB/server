package org.devoceanyoung.feedflow.domain.message.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.devoceanyoung.feedflow.domain.message.dto.AIMessageRequestDTO;
import org.devoceanyoung.feedflow.domain.message.dto.AllMessageRequestDTO;
import org.devoceanyoung.feedflow.domain.message.dto.UserMessageDTO;
import org.devoceanyoung.feedflow.domain.message.entity.Message;
import org.devoceanyoung.feedflow.domain.message.service.MessageService;
import org.devoceanyoung.feedflow.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/message")
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // AI 채팅 반환(stream)
    @PostMapping("/recieve")
    public SseEmitter getAIStreamMessage(@RequestBody AIMessageRequestDTO aiMessageRequestDTO) {
        SseEmitter emitter = new SseEmitter();
        Flux<String> str = messageService.streamMessages(aiMessageRequestDTO);
        str.subscribe(
                data -> {
                    try {
                        emitter.send(data);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );
        return emitter;
    }

    // 유저 채팅 전송
    @PostMapping("/send")
    public ResponseEntity<SuccessResponse<?>> sendUserMessage(@RequestBody UserMessageDTO userMessageDTO) throws JsonProcessingException {
        messageService.saveMessage(userMessageDTO);
        return SuccessResponse.created();
    }

    // 채팅 조회
    @GetMapping()
    public ResponseEntity<SuccessResponse<?>> getChatPage(@RequestBody AllMessageRequestDTO allMessageRequestDTO) {
        List<Message> messages = messageService.getAllMessage(allMessageRequestDTO);
        return SuccessResponse.ok(messages);
    }

// AI 채팅 반환(sync)
    @PostMapping("/sync")
    public ResponseEntity<SuccessResponse<?>> getGptOutputSync(@RequestBody AIMessageRequestDTO aiMessageRequestDTO) {
        String messages =  messageService.getGptOutputSync(aiMessageRequestDTO);
        return SuccessResponse.ok(messages);
    }


}



