package com.branding.branding_backend.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiClientImpl implements AiClient {

    private final WebClient webClient;

    @Value("${ai.server.base-url}")
    private String aiServerBaseUrl;

    @Override
    public Map<String, Object> requestInterviewReport(
            Map<String, Object> interviewInput
    ) {
        return webClient
                .post()
                .uri(aiServerBaseUrl + "/brands/interview")
                .bodyValue(interviewInput)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    @Override
    public Map<String, Object> requestNaming(
            Map<String, Object> namingInput) {
        return webClient
                .post()
                .uri(aiServerBaseUrl + "/brands/naming")
                .bodyValue(namingInput)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    @Override
    public Map<String, Object> requestConcept(Map<String, Object> payload) {
        return Map.of(
                "concept1", "spring",
                "concept2", "summer",
                "concept3", "fall"
        );
    }

    @Override
    public Map<String, Object> requestStory(Map<String, Object> payload) {
        return Map.of(
                "story1", "this is story1",
                "story2", "this is story2",
                "story3", "this is story3"
        );
    }

    @Override
    public Map<String, Object> requestLogo(Map<String, Object> payload) {
        return Map.of(
                "logo1", "https://placehold.co/512x512?text=LOGO+1",
                "logo2", "https://placehold.co/512x512?text=LOGO+2",
                "logo3", "https://placehold.co/512x512?text=LOGO+3"
        );
    }
}
