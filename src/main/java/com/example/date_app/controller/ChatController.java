// com/example/date_app/controller/ChatController.java

package com.example.date_app.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    @PostMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            if (idToken == null || idToken.isEmpty()) {
                return ResponseEntity.badRequest().body("토큰이 없습니다.");
            }

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            log.info("✅ Firebase 토큰 검증 성공: {}", uid);
            return ResponseEntity.ok(uid);
        } catch (Exception e) {
            log.error("❌ 토큰 검증 실패", e);
            return ResponseEntity.badRequest().body("서버 에러: " + e.getMessage());
        }
    }
}
