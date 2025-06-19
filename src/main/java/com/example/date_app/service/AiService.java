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
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public List<String> generateChatStarters(String targetName, String targetMbti, List<String> targetTags) {
        String prompt = String.format(
            "당신은 소개팅 앱에서 대화를 시작하는 문구를 추천하는 AI입니다. 다음 정보를 바탕으로 상대방과 대화를 시작할 수 있는 자연스럽고 매력적인 첫 메시지 3개를 추천해주세요:\n" +
            "- 상대방 이름: %s\n" +
            "- 상대방 MBTI: %s\n" +
            "- 상대방 성격 태그: %s\n\n" +
            "각 메시지는 80자에서 100자 사이로 작성해주세요. 반드시 상대방 이름을 포함해서 작성해주세요. 번호를 붙이지 말고 메시지만 작성해주세요. 메시지는 줄바꿈으로 구분해주세요. 상대방 이름은 targetName입니다.",
            targetName,
            targetMbti,
            String.join(", ", targetTags)
        );

        // API 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // API 요청 본문 설정
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "당신은 소개팅 앱에서 대화를 시작하는 문구를 추천하는 AI입니다. 자연스럽고 매력적인 첫 메시지를 추천해주세요. 반드시 상대방 이름을 포함해서 작성해주세요.");

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

        // 응답에서 텍스트 추출 및 메시지 분리
        if (response != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                String content = (String) message.get("content");
                
                // 줄바꿈으로 구분된 메시지를 리스트로 변환
                return Arrays.stream(content.split("\n"))
                        .filter(line -> !line.isBlank())
                        .limit(3)
                        .collect(Collectors.toList());
            }
        }

        // 기본 메시지 반환 (상대방 이름 포함)
        return List.of(
            targetName + "님, 안녕하세요! 프로필을 보고 정말 매력적이라고 느껴 대화를 나눠보고 싶어 연락드립니다. 오늘 하루는 어떻게 보내셨나요?",
            targetName + "님, 반갑습니다! 프로필에서 느껴지는 분위기가 정말 좋아 보여요. 서로 알아가면 즐거운 시간이 될 것 같아요. 주말 계획이 있으신가요?",
            targetName + "님, 안녕하세요! 취미나 관심사가 비슷한 것 같아 메시지 드립니다. 특별히 좋아하시는 활동이나 취미가 있으신가요? 저도 공유하고 싶어요."
        );
    }
}
