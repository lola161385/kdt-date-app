// com/example/date_app/config/FirebaseConfig.java
package com.example.date_app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            String base64 = System.getenv("FIREBASE_SERVICE_ACCOUNT_KEY");
            InputStream serviceAccount;
            if (base64 != null && !base64.isBlank()) {
                // 환경변수에서 BASE64로 읽기 (로컬/서버 모두 지원)
                serviceAccount = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            } else {
                // fallback: 클래스패스 리소스에서 읽기 (로컬 개발 편의)
                serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
                if (serviceAccount == null) {
                    throw new IllegalStateException("firebase-service-account.json not found in classpath and FIREBASE_SERVICE_ACCOUNT_KEY env not set");
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://date-app-41a1c-default-rtdb.firebaseio.com")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase 초기화 완료");
            }
        } catch (Exception e) {
            System.err.println("🔥 Firebase 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

