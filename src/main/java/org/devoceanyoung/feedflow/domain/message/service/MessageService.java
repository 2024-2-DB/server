package org.devoceanyoung.feedflow.domain.message.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.devoceanyoung.feedflow.domain.message.dto.AIMessageRequestDTO;
import org.devoceanyoung.feedflow.domain.message.dto.AllMessageRequestDTO;
import org.devoceanyoung.feedflow.domain.message.dto.GptRequestDTO;
import org.devoceanyoung.feedflow.domain.message.dto.UserMessageDTO;
import org.devoceanyoung.feedflow.domain.message.entity.Message;
import org.devoceanyoung.feedflow.domain.message.repository.MessageRepository;
import org.devoceanyoung.feedflow.domain.team.entity.Team;
import org.devoceanyoung.feedflow.domain.team.entity.UserTeam;
import org.devoceanyoung.feedflow.domain.team.repository.TeamRepository;
import org.devoceanyoung.feedflow.domain.team.repository.UserTeamRepository;
import org.devoceanyoung.feedflow.global.config.GPTConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserTeamRepository userTeamRepository;

    @Value("${openai.api.key}")
    private String apiKey;
    private final TeamRepository teamRepository;


    public Flux<String> streamMessages(AIMessageRequestDTO aiMessageRequestDTO) {
        UserTeam userTeam = userTeamRepository.findUserTeamByUser_UserIdAndTeam_TeamId(aiMessageRequestDTO.userId(), aiMessageRequestDTO.teamId())
                .orElseThrow(() -> new RuntimeException("No userteam found"));

        List<Message> inputMessages = getLLMInputs(userTeam, aiMessageRequestDTO.isFirst());

        StringBuilder accumulatedContent = new StringBuilder();

        return Flux.create(sink -> {
            if (aiMessageRequestDTO.isFirst()) {
                String systemPrompt = GPTConfig.getSystemPrompts(true);
                accumulatedContent.append(systemPrompt);

                messageRepository.save(Message.builder()
                        .userTeam(userTeam)
                        .role(GPTConfig.ROLE_ASSISTANT)
                        .content(systemPrompt)
                        .createdAt(LocalDateTime.now())
                        .build());
                sink.next(systemPrompt);
                sink.complete();
                return;
            }

            Flux<String> eventStream = getResponse(inputMessages);

            eventStream.subscribe(
                    content -> {
                        String extractedContent = extractContent(content);
                        System.out.println("Extracted Content: " + extractedContent);
                        accumulatedContent.append(extractedContent);
                        sink.next(extractedContent);
                    },
                    error -> {
                        System.err.println("Error occurred: " + error.getMessage());
                        sink.error(error);
                    },
                    () -> {
                        System.out.println("Final accumulated content: " + accumulatedContent.toString()); // 최종 응답 확인
                        messageRepository.save(Message.builder()
                                .userTeam(userTeam)
                                .role(GPTConfig.ROLE_ASSISTANT)
                                .content(accumulatedContent.toString().replace("*", " "))
                                .createdAt(LocalDateTime.now())
                                .build());
                        sink.complete();
                    }
            );
            new SseEmitter().onTimeout(sink::complete);
        });
    }

    public List<Message> getLLMInputs(UserTeam userTeam, boolean isFirst) {
        List<Message> messages = messageRepository.findAllByUserTeamOrderByCreatedAtDesc(userTeam);
        if(messages == null) throw new RuntimeException("The Messages is Null");

        List<Message> parsedDatas = new ArrayList<>();

        parsedDatas.add(Message.builder()
                .userTeam(userTeam)
                .role(GPTConfig.ROLE_ASSISTANT)
                .content(GPTConfig.getSystemPrompts(isFirst))
                .createdAt(LocalDateTime.now())
                .build()
        );
        if(isFirst) return parsedDatas;

        for(int i=messages.size()-1; i>=0; i--) parsedDatas.add(messages.get(i));
        return parsedDatas;
    }

    public Flux<String> getResponse(List<Message> messages) {

        WebClient webClient = WebClient.builder()
                .baseUrl(GPTConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(GPTConfig.AUTHORIZATION, GPTConfig.BEARER + apiKey)
                .build();

        GptRequestDTO request = GptRequestDTO.builder()
                .model(GPTConfig.CHAT_MODEL)
                .maxTokens(GPTConfig.MAX_TOKEN)
                .temperature(GPTConfig.TEMPERATURE)
                .stream(GPTConfig.STREAM)
                .messages(messages)
                .build();

        return webClient.post()
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);
    }



    public String extractContent(String jsonEvent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonEvent);
            String extractedText = node.at("/choices/0/delta/content").asText();
            return extractedText.replace(" ", "*");
        } catch (IOException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            return "";
        }
    }



    public List<Message> getAllMessage(AllMessageRequestDTO allMessageRequestDTO) {
        UserTeam userTeam = userTeamRepository.findUserTeamByUser_UserIdAndTeam_TeamId(allMessageRequestDTO.userId(), allMessageRequestDTO.teamId())
                .orElseThrow(() -> new RuntimeException("No userteam found for userId " + allMessageRequestDTO.userId()));

        System.out.println("UserTeam: " + userTeam);
        return messageRepository.findAllByUserTeamOrderByCreatedAtAsc(userTeam);
    }



    public void saveMessage(UserMessageDTO userMessageDTO) {
        UserTeam userTeam = userTeamRepository.findUserTeamByUser_UserIdAndTeam_TeamId(userMessageDTO.userId(), userMessageDTO.teamId())
                .orElseThrow(() -> new RuntimeException("No userteam found for userId " + userMessageDTO.userId()));

        Message newMessage = Message.builder()
                .userTeam(userTeam)
                .role(GPTConfig.ROLE_USER)
                .content(userMessageDTO.content())
                .createdAt(LocalDateTime.now())
                .build();

        messageRepository.save(newMessage);
    }
}

