package com.example.date_app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateIntroduction(List<String> keywords, List<String> mbti, List<String> likeKeyWords) {
        String prompt = String.format(
                "당신은 소개팅 앱을 위한 자기소개글 생성 AI입니다. 다음 정보를 바탕으로 매력적인 자기소개글을 작성해주세요:\n" +
                        "- 성격 키워드: %s\n" +
                        "- MBTI: %s\n" +
                        "- 선호하는 상대방 키워드: %s\n\n" +
                        "300자 내외로 작성해주세요. 자연스럽고 개성있는 글을 작성해주세요.",
                String.join(", ", keywords),
                String.join(", ", mbti),
                String.join(", ", likeKeyWords)
        );

        // API 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // API 요청 본문 설정
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "당신은 소개팅 앱을 위한 자기소개글 생성 AI입니다. 매력적이고 개성있는 자기소개글을 작성해주세요.");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini-2024-07-18");
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // API 호출
        Map<String, Object> response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                request,
                Map.class
        );

        // 응답에서 텍스트 추출
        if (response != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                return (String) message.get("content");
            }
        }

        return "자기소개글 생성에 실패했습니다.";
    }
}