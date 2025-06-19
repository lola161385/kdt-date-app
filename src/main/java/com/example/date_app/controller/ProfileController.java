package com.example.date_app.controller;

import com.example.date_app.dto.ProfileUpdateRequest;
import com.example.date_app.service.FirebaseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final FirebaseAuthService firebaseAuthService;

    // [1] 프로필 조회 페이지 (기존 home.html → profile.html로 사용)
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> profile = firebaseAuthService.getUserProfile(userEmail);
            if (profile != null) {
                model.addAttribute("name", profile.getOrDefault("name", ""));
                model.addAttribute("birthdate", profile.getOrDefault("birthdate", ""));
                model.addAttribute("bio", profile.getOrDefault("bio", ""));
                model.addAttribute("gender", profile.getOrDefault("gender", ""));

                Map<String, Object> personality = (Map<String, Object>) profile.getOrDefault("personality", Map.of());
                model.addAttribute("mbti", personality.getOrDefault("mbti", ""));
                model.addAttribute("tags", personality.getOrDefault("tags", List.of()));
            }
        } catch (Exception e) {
            model.addAttribute("error", "프로필 조회 실패: " + e.getMessage());
        }

        return "profile"; // ✅ 여기서 이제 profile.html을 렌더링
    }

    // [2] 프로필 수정 페이지 (기존 profile.html → profileEdit.html로 이동)
    @GetMapping("/profile/edit")
    public String editProfilePage() {
        return "profileEdit"; // ✅ 수정용 html
    }

    // [3] 프로필 수정 API
    @PostMapping("/api/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateFullProfile(@RequestBody ProfileUpdateRequest request) {
        String email = getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(401).body("Unauthorized");

        if (!request.isValid()) {
            return ResponseEntity.badRequest().body("❌ 태그와 선호 키워드는 각각 최대 5개 이하로 입력 가능합니다.");
        }

        try {
            firebaseAuthService.updateUserProfile(
                    email,
                    request.getName(),
                    request.getBirthdate(),
                    request.getGender(),
                    request.getBio(),
                    request.getMbti(),
                    request.getTags(),
                    request.getLikeTags() // ✅ 선호 키워드 추가 전달
            );
            return ResponseEntity.ok("✅ 프로필 전체 저장 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 저장 실패: " + e.getMessage());
        }
    }


    // [4] API: 프로필 데이터 조회 (React 등에서 사용 가능)
    @GetMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiProfile() {
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            Map<String, Object> profile = firebaseAuthService.getUserProfile(userEmail);
            return ResponseEntity.ok(Map.of("userEmail", userEmail, "profile", profile));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Profile fetch failed"));
        }
    }

    // 다른 사용자의 프로필 정보 가져오기
    @GetMapping("/api/profile/get")
    @ResponseBody
    public ResponseEntity<?> getUserProfile(@RequestParam String email) {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) return ResponseEntity.status(401).body("Unauthorized");

        try {
            Map<String, Object> profile = firebaseAuthService.getUserProfile(email);
            if (profile == null) {
                return ResponseEntity.status(404).body("프로필을 찾을 수 없습니다.");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("userEmail", email);
            response.put("profile", profile);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("프로필 조회 실패: " + e.getMessage());
        }
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? (String) auth.getPrincipal() : null;
    }
}
