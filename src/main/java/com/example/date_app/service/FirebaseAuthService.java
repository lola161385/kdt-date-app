// com/example/date_app/service/FirebaseAuthService.java

package com.example.date_app.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class FirebaseAuthService {

    public String registerUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        return userRecord.getUid();
    }

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUserByEmail(email);
    }

    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public void deleteUserByEmail(String email) throws FirebaseAuthException {
        UserRecord user = getUserByEmail(email);
        FirebaseAuth.getInstance().deleteUser(user.getUid());
    }

    public void updateUserProfile(String email, String name, String birthdate, String gender, String bio, String mbti, List<String> tags)
            throws FirebaseAuthException {

        if (tags.size() > 5) throw new IllegalArgumentException("태그는 최대 5개까지 선택 가능합니다.");

        UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("email", email);
        profile.put("birthdate", birthdate);
        profile.put("bio", bio);
        profile.put("gender", gender);
        profile.put("personality", Map.of("mbti", mbti, "tags", tags));

        ref.setValueAsync(profile);
    }

    public Map<String, Object> getUserProfile(String email) throws FirebaseAuthException {
        try {
            UserRecord user = getUserByEmail(email);
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid());

            final Map<String, Object>[] result = new Map[]{null};
            CountDownLatch latch = new CountDownLatch(1);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        result[0] = (Map<String, Object>) dataSnapshot.getValue();
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    latch.countDown();
                }
            });

            latch.await();
            return result[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("프로필 조회 중 인터럽트 발생", e);
        }
    }

    public void createInitialUserProfile(String email) throws FirebaseAuthException {
        UserRecord user = getUserByEmail(email);
        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        Map<String, Object> profile = new HashMap<>();
        profile.put("email", email);
        profile.put("name", "");
        profile.put("birthdate", "");
        profile.put("bio", "");
        profile.put("gender", "");
        profile.put("personality", Map.of("mbti", "", "tags", List.of()));

        ref.setValueAsync(profile);
    }

    public boolean userProfileExists(String email) throws FirebaseAuthException {
        try {
            UserRecord user = getUserByEmail(email);
            String uid = user.getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid);

            final boolean[] exists = {false};
            CountDownLatch latch = new CountDownLatch(1);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    exists[0] = snapshot.exists();
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    latch.countDown();
                }
            });

            latch.await();
            return exists[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("프로필 존재 여부 확인 중 인터럽트 발생", e);
        }
    }

    public List<Map<String, Object>> getAllUserProfiles() {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

            List<Map<String, Object>> resultList = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(1);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Object value = child.getValue();
                        if (value instanceof Map) {
                            resultList.add((Map<String, Object>) value);
                        }
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    latch.countDown();
                }
            });

            latch.await();
            return resultList;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("전체 프로필 조회 중 인터럽트 발생", e);
        }
    }
}
