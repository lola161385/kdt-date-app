package com.example.date_app.controller;

import com.example.date_app.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate-introduction")
    public ResponseEntity<Map<String, String>> generateIntroduction(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> keywords = (List<String>) request.get("keywords");

            @SuppressWarnings("unchecked")
            List<String> mbti = (List<String>) request.get("mbti");

            @SuppressWarnings("unchecked")
            List<String> likeKeyWords = (List<String>) request.get("likeKeyWords");

            // 스타일 옵션 추가 (선택사항)
            String style = (String) request.get("style");

            String introduction;
            if (style != null && !style.trim().isEmpty()) {
                introduction = aiService.generateIntroduction(keywords, mbti, likeKeyWords, style);
            } else {
                introduction = aiService.generateIntroduction(keywords, mbti, likeKeyWords);
            }

            Map<String, String> response = new HashMap<>();
            response.put("introduction", introduction);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/generate-multiple-introductions")
    public ResponseEntity<Map<String, Object>> generateMultipleIntroductions(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> keywords = (List<String>) request.get("keywords");

            @SuppressWarnings("unchecked")
            List<String> mbti = (List<String>) request.get("mbti");

            @SuppressWarnings("unchecked")
            List<String> likeKeyWords = (List<String>) request.get("likeKeyWords");

            List<String> introductions = aiService.generateMultipleIntroductions(keywords, mbti, likeKeyWords);

            Map<String, Object> response = new HashMap<>();
            response.put("introductions", introductions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/generate-chat-starters")
    public ResponseEntity<Map<String, Object>> generateChatStarters(@RequestBody Map<String, Object> request) {
        try {
            String targetName = (String) request.get("targetName");
            String targetMbti = (String) request.get("targetMbti");
            
            @SuppressWarnings("unchecked")
            List<String> targetTags = (List<String>) request.get("targetTags");

            List<String> chatStarters = aiService.generateChatStarters(targetName, targetMbti, targetTags);
            
            Map<String, Object> response = new HashMap<>();
            response.put("chatStarters", chatStarters);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
