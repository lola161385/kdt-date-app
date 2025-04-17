// com/example/date_app/controller/AuthController.java
package com.example.date_app.controller;

import com.example.date_app.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final FirebaseAuthService firebaseAuthService;

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html
    }

    @PostMapping(value = "/api/login", consumes = "application/json", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> loginWithToken(@RequestBody Map<String, String> body, HttpSession session) {
        System.out.println("🔥 로그인 요청 도착");

        String idToken = body.get("idToken");
        System.out.println("📦 전달받은 idToken: " + idToken);

        try {
            FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);
            session.setAttribute("userEmail", decodedToken.getEmail());

            System.out.println("✅ Firebase 인증 성공: " + decodedToken.getEmail());
            return ResponseEntity.ok("success");
        } catch (FirebaseAuthException e) {
            System.out.println("❌ Firebase 인증 실패: " + e.getMessage());
            return ResponseEntity.status(401).body("Firebase 인증 실패: " + e.getMessage());
        }
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login?error=sessionExpired";
        }
        model.addAttribute("userEmail", userEmail);
        return "home";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String email,
                                 @RequestParam String password,
                                 Model model) {
        try {
            firebaseAuthService.registerUser(email, password);
            return "redirect:/login";
        } catch (FirebaseAuthException e) {
            model.addAttribute("message", "회원가입 실패: " + e.getMessage());
            return "register";
        }
    }

    @PostMapping("/delete-account")
    public String deleteAccount(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/login";
        }

        try {
            firebaseAuthService.deleteUserByEmail(userEmail);
            session.invalidate(); // 로그아웃 처리
            return "redirect:/login";
        } catch (FirebaseAuthException e) {
            model.addAttribute("message", "회원탈퇴 실패: " + e.getMessage());
            return "home"; // 실패 시 홈으로
        }
    }

}
