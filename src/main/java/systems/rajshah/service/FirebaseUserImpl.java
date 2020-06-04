package systems.rajshah.service;


import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import systems.rajshah.repository.InvestorInfo;
import systems.rajshah.repository.UserInfo;

@Service
public class FirebaseUserImpl implements IfirebaseUser{
	Firestore dbFirestore = FirestoreClient.getFirestore();
	 String CurrentLoggedInUserUID="";
	
	@Override
	public UserInfo getCurrentUserDetails(String emailID) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		
		UserRecord a=FirebaseAuth.getInstance().getUserByEmail(emailID);
		CurrentLoggedInUserUID=a.getUid();
		System.out.println("this is UID:"+CurrentLoggedInUserUID);
		
		ApiFuture<DocumentSnapshot> future=dbFirestore.collection("users").document(a.getEmail()).get();
		DocumentSnapshot docsnap=future.get();
		UserInfo userInfo;
		if(docsnap.exists()) {
			userInfo=docsnap.toObject(UserInfo.class);
			return userInfo;
		}
		return null;
		
	}

	@Override
	public InvestorInfo postInvestorInfo(InvestorInfo investInfo)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		
		return null;
		
	}

	
}
