package com.example.date_app.service;

import com.example.date_app.dto.MatchRecommendation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MatchScoringService {

    public MatchRecommendation calculateScore(
            String myEmail,
            String myGender,
            String myMbti,
            List<String> myTags,
            Map<String, Object> targetProfile
    ) {
        if (Objects.equals(myEmail, targetProfile.get("email"))) return null;
        if (Objects.equals(myGender, targetProfile.get("gender"))) return null;

        Map<String, Object> theirPersonality = (Map<String, Object>) targetProfile.getOrDefault("personality", Map.of());
        String theirMbti = (String) theirPersonality.getOrDefault("mbti", "");
        List<String> theirTags = (List<String>) theirPersonality.getOrDefault("tags", List.of());

        int score = 0;
        if (Objects.equals(theirMbti, myMbti)) score += 2;

        List<String> commonTags = myTags.stream()
                .filter(theirTags::contains)
                .collect(Collectors.toList());
        score += commonTags.size();

        return new MatchRecommendation(
                (String) targetProfile.get("email"),
                (String) targetProfile.getOrDefault("name", "익명"),
                theirMbti,
                commonTags,
                score
        );
    }
}
