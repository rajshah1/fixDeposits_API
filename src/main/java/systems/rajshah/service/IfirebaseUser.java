package systems.rajshah.service;

import java.util.concurrent.ExecutionException;

import com.google.firebase.auth.FirebaseAuthException;

import systems.rajshah.repository.UserInfo;

public interface IfirebaseUser {
public UserInfo getCurrentUserDetails(String emailId,String idToken) throws FirebaseAuthException, InterruptedException, ExecutionException;
}
