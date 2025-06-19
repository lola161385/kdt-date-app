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

            String introduction = aiService.generateIntroduction(keywords, mbti, likeKeyWords);
            
            Map<String, String> response = new HashMap<>();
            response.put("introduction", introduction);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}