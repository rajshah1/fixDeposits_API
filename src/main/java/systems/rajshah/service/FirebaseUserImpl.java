package systems.rajshah.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import systems.rajshah.repository.UserInfo;

@Service
public class FirebaseUserImpl implements IfirebaseUser{
	Firestore dbFirestore = FirestoreClient.getFirestore();

	@Override
	public UserInfo getCurrentUserDetails(String emailID) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		
		UserRecord a=FirebaseAuth.getInstance().getUserByEmail(emailID);
		
		System.out.println("this is UID:"+a.getUid());
		ApiFuture<DocumentSnapshot> future=dbFirestore.collection("users").document(a.getEmail()).get();
		DocumentSnapshot docsnap=future.get();
		UserInfo userInfo;
		if(docsnap.exists()) {
			userInfo=docsnap.toObject(UserInfo.class);
			System.out.println(userInfo.toString());
			return userInfo;
		}
		return null;
		
	}
	
}
