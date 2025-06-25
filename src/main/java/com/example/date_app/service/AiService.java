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
                "당신은 소개팅 앱을 위한 자기소개글 생성 AI입니다. 다음 정보를 바탕으로 소개팅 앱용 자기소개글을 작성해주세요.\n\n" +
                        "📝 정보: 성격(%s), MBTI(%s), 이상형(%s)\n\n" +
                        "✅ 필수 요구사항:\n" +
                        "• 300자 내외, 친근하고 진정성 있는 톤\n" +
                        "• 성격을 구체적 상황/에피소드로 표현 (나열 금지)\n" +
                        "• MBTI 특성을 자연스럽게 녹여내기\n" +
                        "• 취미/관심사 1-2개 포함하여 매력 어필\n" +
                        "• 이상형 언급으로 진정성 표현\n" +
                        "• 만남에 대한 긍정적 기대감 표현\n\n" +
                        "❌ 피해야 할 것: 뻔한 표현('운명', '소울메이트'), 과도한 겸손, 성격 키워드 직접 나열",
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
        systemMessage.put("content", "당신은 소개팅 앱 전문 자기소개글 작성 AI입니다. " +
                "개인의 매력을 자연스럽게 어필하고 상대방이 호감을 느낄 수 있는 진정성 있는 자기소개글을 작성합니다.");

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

    // 스타일별 자기소개글 생성 메서드 (오버로드)
    public String generateIntroduction(List<String> keywords, List<String> mbti, List<String> likeKeyWords, String style) {
        String styleGuide = getStyleGuide(style);

        String prompt = String.format(
                "소개팅 앱용 자기소개글을 %s 스타일로 작성해주세요.\n\n" +
                        "📝 정보: 성격(%s), MBTI(%s), 이상형(%s)\n\n" +
                        "✅ 요구사항:\n" +
                        "• 300자 내외, 선택된 스타일 톤앤매너 적용\n" +
                        "• 성격을 구체적 상황으로 표현, MBTI 특성 자연스럽게 포함\n" +
                        "• 취미/관심사로 매력 어필, 이상형 언급으로 진정성 표현\n" +
                        "• 만남에 대한 긍정적 기대감, 뻔한 표현 피하기",
                styleGuide,
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
        systemMessage.put("content", "소개팅 앱 전문 자기소개글 작성 AI입니다. " +
                "다양한 스타일로 개인의 매력을 어필하는 맞춤형 자기소개글을 작성합니다.");

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

    // 스타일별 가이드 제공 메서드
    private String getStyleGuide(String style) {
        switch (style != null ? style.toLowerCase() : "friendly") {
            case "cute":
                return "귀엽고 사랑스러운 스타일 - 이모티콘 적절히 사용, 애교 있는 표현, 밝고 경쾌한 톤";
            case "professional":
                return "차분하고 성숙한 스타일 - 정중하고 품격 있는 표현, 안정감 있는 톤, 진중한 매력 어필";
            case "humorous":
                return "유머러스하고 재치있는 스타일 - 위트 있는 표현, 센스 있는 농담, 친근하고 재미있는 톤";
            case "romantic":
                return "로맨틱하고 감성적인 스타일 - 따뜻하고 감성적인 표현, 설레는 분위기, 로맨틱한 매력 어필";
            case "active":
                return "활발하고 에너지 넘치는 스타일 - 역동적인 표현, 열정적인 톤, 활동적인 매력 강조";
            default: // "friendly"
                return "친근하고 자연스러운 스타일 - 편안하고 따뜻한 표현, 진정성 있는 톤, 접근하기 쉬운 매력";
        }
    }

    // 다중 자기소개글 생성 메서드 (3개 옵션 제공)
    public List<String> generateMultipleIntroductions(List<String> keywords, List<String> mbti, List<String> likeKeyWords) {
        String prompt = String.format(
                "소개팅 앱용 자기소개글 3개를 서로 다른 스타일로 작성해주세요.\n\n" +
                        "📝 정보: 성격(%s), MBTI(%s), 이상형(%s)\n\n" +
                        "📋 3가지 스타일:\n" +
                        "1️⃣ 친근하고 자연스러운 스타일\n" +
                        "2️⃣ 유머러스하고 재치있는 스타일\n" +
                        "3️⃣ 로맨틱하고 감성적인 스타일\n\n" +
                        "✅ 요구사항:\n" +
                        "• 각각 300자 내외, 스타일별 다른 톤앤매너\n" +
                        "• 성격을 구체적 상황으로 표현, MBTI 특성 자연스럽게 포함\n" +
                        "• 취미/관심사로 매력 어필, 이상형 언급, 뻔한 표현 피하기\n" +
                        "• 각 자기소개글을 '---'로 구분\n\n" +
                        "형식: [첫번째] --- [두번째] --- [세번째]",
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
        systemMessage.put("content", "소개팅 앱 전문 자기소개글 작성 AI입니다. " +
                "다양한 스타일의 매력적인 자기소개글을 작성하여 선택 옵션을 제공합니다.");

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

        // 응답에서 텍스트 추출 및 분리
        if (response != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                String content = (String) message.get("content");

                // "---"로 구분된 자기소개글들을 리스트로 변환
                return Arrays.stream(content.split("---"))
                        .map(String::trim)
                        .filter(intro -> !intro.isEmpty())
                        .limit(3)
                        .collect(Collectors.toList());
            }
        }

        // 기본 자기소개글 반환
        return List.of(
            "안녕하세요! 저는 일상 속 작은 행복을 찾는 것을 좋아하는 사람입니다. 새로운 사람들과의 만남을 통해 서로의 이야기를 나누고 함께 성장해 나가고 싶어요.",
            "반갑습니다! 저는 유머 감각이 있다고 자부하는 사람입니다. 함께 웃으며 즐거운 시간을 보낼 수 있는 분을 만나고 싶어요. 인생은 너무 짧으니까 재미있게 살아야죠!",
            "안녕하세요. 저는 진정한 연결과 깊은 대화를 소중히 여기는 사람입니다. 서로의 마음을 이해하고 함께 아름다운 추억을 만들어갈 수 있는 특별한 인연을 찾고 있어요."
        );
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
