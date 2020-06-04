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
		
		Map<String, Object> docData = new HashMap<>();
		docData.put("name", "Los Angeles");
		docData.put("state", "CA");
		docData.put("country", "USA");
		
		
		// Add a new document (asynchronously) in collection "cities" with id "LA"
		ApiFuture<WriteResult> future = dbFirestore.collection("cities").document("LA").set(docData);
		
		//System.out.println("Update time : " + future.get().getUpdateTime());
		//ApiFuture<DocumentReference> addedDocRef = dbFirestore.collection("cities").add(docData);
		//System.out.println("Added document with ID: " + addedDocRef.get().getId());

		
		/*
		 * if(CurrentLoggedInUserUID!= null) { Map<String,Integer> docData = new
		 * HashMap<>(); for(int i=0;i<26;i++) { docData.put
		 * (String.valueOf((char)(i+97)),0); } System.out.println(docData);
		 * System.out.println("this the UID: "+CurrentLoggedInUserUID);
		 * ApiFuture<WriteResult>
		 * future=dbFirestore.collection(CurrentLoggedInUserUID).document("alphaCounter"
		 * ).set(docData);
		 * System.out.println("I have seen this"+future.get().getUpdateTime()); }
		 */return null;
		
	}

	
}
