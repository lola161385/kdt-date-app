package com.example.date_app.controller;

import com.example.date_app.service.FirebaseAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final FirebaseAuthService firebaseAuthService;

    @GetMapping("/profile")
    public String profileForm(Model model) {
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> profile = firebaseAuthService.getUserProfile(userEmail);
            if (profile != null) {
                // null 체크 추가
                model.addAttribute("name", profile.getOrDefault("name", ""));
                model.addAttribute("birthdate", profile.getOrDefault("birthdate", ""));
                model.addAttribute("bio", profile.getOrDefault("bio", ""));
            }
        } catch (Exception e) {
            model.addAttribute("error", "프로필 조회 실패: " + e.getMessage());
        }
        return "profile";
    }


    @PostMapping("/profile")
    public String saveProfile(
            @RequestParam String name,
            @RequestParam String birthdate,
            @RequestParam(required = false) String bio,
            RedirectAttributes redirectAttributes
    ) {
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            return "redirect:/login";
        }

        try {
            firebaseAuthService.saveUserProfile(userEmail, name, birthdate, bio);
            redirectAttributes.addFlashAttribute("message", "✅ 프로필이 성공적으로 저장되었습니다");
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ 저장 실패: " + e.getMessage());
            return "redirect:/profile";
        }
    }
    @GetMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiProfile() {
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            Map<String, Object> profile = firebaseAuthService.getUserProfile(userEmail);
            Map<String, Object> result = new HashMap<>();
            result.put("userEmail", userEmail);
            result.put("profile", profile);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Profile fetch failed"));
        }
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? (String) auth.getPrincipal() : null;
    }

}
