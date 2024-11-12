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
import org.devoceanyoung.feedflow.domain.team.entity.UserTeam;
import org.devoceanyoung.feedflow.domain.team.repository.UserTeamRepository;
import org.devoceanyoung.feedflow.global.config.GPTConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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

    public String getGptOutputSync(AIMessageRequestDTO aiMessageRequestDTO) {
        UserTeam userTeam = userTeamRepository.findUserTeamByUser_UserIdAndTeam_TeamId(aiMessageRequestDTO.userId(), aiMessageRequestDTO.teamId())
                .orElseThrow(() -> new RuntimeException("No userteam found"));
        List<Message> inputMessages = getLLMInputs(userTeam, aiMessageRequestDTO.isFirst());
        String gptResponse = getResponseSync(inputMessages);
        return gptResponse;
    }

    public String getResponseSync(List<Message> messages) {
        WebClient webClient = WebClient.builder()
                .baseUrl(GPTConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(GPTConfig.AUTHORIZATION, GPTConfig.BEARER + apiKey)
                .build();

        // 메시지를 OpenAI가 요구하는 형식으로 변환
        List<Map<String, String>> messageList = messages.stream()
                .map(message -> Map.of(
                        "role", message.getRole(),
                        "content", message.getContent()
                ))
                .collect(Collectors.toList());

        GptRequestDTO request = GptRequestDTO.builder()
                .model(GPTConfig.CHAT_MODEL)
                .maxTokens(GPTConfig.MAX_TOKEN)
                .temperature(GPTConfig.TEMPERATURE)
                .stream(false)
                .messages(messageList)  // 올바른 메시지 리스트를 전달
                .build();

        System.out.println("GPT Request DTO: " + request);

        try {
            WebClient.ResponseSpec responseSpec = webClient.post()
                    .bodyValue(request)
                    .retrieve();

            if (responseSpec.toBodilessEntity().block().getStatusCode().isError()) {
                String errorBody = responseSpec.bodyToMono(String.class).block();
                System.out.println("Error status code: " + responseSpec.toBodilessEntity().block().getStatusCode());
                System.out.println("Error body: " + errorBody);
                throw new RuntimeException("Error: " + errorBody);
            }

            String response = responseSpec.bodyToMono(String.class).block();
            System.out.println("Response: " + response);
            return extractContent(response);

        } catch (Exception e) {
            System.err.println("Error response: " + e.getMessage());
            throw new RuntimeException("Error getting GPT response: " + e.getMessage());
        }
    }


    public List<Message> getLLMInputs(UserTeam userTeam, boolean isFirst) {
        List<Message> messages = messageRepository.findAllByUserTeamOrderByCreatedAtDesc(userTeam);
        List<Message> parsedData = new ArrayList<>();


        parsedData.add(Message.builder()
                .userTeam(userTeam)
                .role(GPTConfig.ROLE_ASSISTANT)
                .content(GPTConfig.getSystemPrompts(isFirst))
                .createdAt(LocalDateTime.now())
                .build());

        if (!isFirst && messages != null && !messages.isEmpty()) {
            parsedData.addAll(messages);
        }
        return parsedData;
    }


    private String extractContent(String jsonEvent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonEvent);
            String content = node.at("/choices/0/message/content").asText();
            System.out.println("Extracted Content: " + content);
            return content;
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


    public Flux<String> getResponse(List<Message> messages) {
        WebClient webClient = WebClient.builder()
                .baseUrl(GPTConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(GPTConfig.AUTHORIZATION, GPTConfig.BEARER + apiKey)
                .build();

        List<Map<String, String>> messageList = messages.stream()
                .map(message -> Map.of(
                        "role", message.getRole(),
                        "content", message.getContent()
                ))
                .collect(Collectors.toList());

        GptRequestDTO request = GptRequestDTO.builder()
                .model(GPTConfig.CHAT_MODEL)
                .maxTokens(GPTConfig.MAX_TOKEN)
                .temperature(GPTConfig.TEMPERATURE)
                .stream(GPTConfig.STREAM)
                .messages(messageList)  // Use converted messageList here
                .build();

        return webClient.post()
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);
    }

    public Flux<String> streamMessages(AIMessageRequestDTO aiMessageRequestDTO) {
        UserTeam userTeam = userTeamRepository.findUserTeamByUser_UserIdAndTeam_TeamId(aiMessageRequestDTO.userId(), aiMessageRequestDTO.teamId())
                .orElseThrow(() -> new RuntimeException("No userteam found for userId " + aiMessageRequestDTO.userId()));

        List<Message> inputMessages = getLLMInputs(userTeam, aiMessageRequestDTO.isFirst());

        StringBuilder accumulatedContent = new StringBuilder();

        return Flux.create(sink -> {
            if (aiMessageRequestDTO.isFirst()) {
                String systemPrompt = GPTConfig.getSystemPrompts(true);
                accumulatedContent.append(systemPrompt);

                messageRepository.save(Message.builder()
                        .userTeam(userTeamRepository.findUserTeamByUser_UserIdAndTeam_TeamId(aiMessageRequestDTO.userId(), aiMessageRequestDTO.teamId())
                                .orElseThrow(() -> new RuntimeException("No user team found for userId " + aiMessageRequestDTO.userId())))
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
                        System.out.println("Final accumulated content: " + accumulatedContent.toString());
                        messageRepository.save(Message.builder()
                                .userTeam(userTeamRepository.findUserTeamByUser_UserIdAndTeam_TeamId(aiMessageRequestDTO.userId(), aiMessageRequestDTO.teamId())
                                        .orElseThrow(() -> new RuntimeException("No user team found for userId " + aiMessageRequestDTO.userId())))
                                .role(GPTConfig.ROLE_ASSISTANT)
                                .content(accumulatedContent.toString())
                                .createdAt(LocalDateTime.now())
                                .build());
                        sink.complete();
                    }
            );
        });
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

