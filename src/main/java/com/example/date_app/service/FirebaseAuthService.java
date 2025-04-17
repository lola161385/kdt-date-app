// com/example/date_app/service/FirebaseAuthService.java

package com.example.date_app.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

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

}
