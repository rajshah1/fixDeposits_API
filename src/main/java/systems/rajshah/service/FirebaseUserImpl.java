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
	 String CurrentLoggedInUserUID="";
	
	@Override
	public UserInfo getCurrentUserDetails(String emailID) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Firestore dbFirestore = FirestoreClient.getFirestore();
		UserRecord a=FirebaseAuth.getInstance().getUserByEmail(emailID);
		CurrentLoggedInUserUID=a.getUid();
		//System.out.println("this is UID:"+CurrentLoggedInUserUID);

		/*
		 *                      ONLY RUN <------ONCE------>
		 * This code will create alphacounter document in {/UID} collection :{a:0,b:0
		 * .........z:0} Map if(CurrentLoggedInUserUID!= null) { Map<String,Integer>
		 * counts = new HashMap<>(); for(int i=0;i<26;i++) {
		 * counts.put(String.valueOf((char)(i+97)),0); } System.out.println(counts);
		 * ApiFuture<WriteResult>
		 * future=dbFirestore.collection(CurrentLoggedInUserUID).document("alphaCounter"
		 * ).set(counts);
		 * System.out.println("I have seen this"+future.get().getUpdateTime()); }
		 */
		 
		
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
	public String createInvestorInfo(InvestorInfo investInfo)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Firestore dbFirestore = FirestoreClient.getFirestore();
		CollectionReference collRef=dbFirestore.collection(CurrentLoggedInUserUID);
		DocumentSnapshot fullCountMap=collRef.document("alphaCounter").get().get();
		Long currentNumber=(Long)fullCountMap.getData().get(investInfo.getLastName().substring(0, 1).toLowerCase());
		String idString=investInfo.getFirstName().substring(0,1).toUpperCase()+investInfo.getLastName().substring(0,1).toUpperCase()+(currentNumber+1);
		investInfo.setId(idString);
		ApiFuture<WriteResult> future=dbFirestore.collection(CurrentLoggedInUserUID).document(idString).set(investInfo);
		future.get();
		//System.out.println("Added on: "+future.get().getUpdateTime());
		Map<String, Object> updates = new HashMap<>();
		updates.put(investInfo.getLastName().substring(0, 1).toLowerCase(), currentNumber+1);
		ApiFuture<WriteResult> increment=collRef.document("alphaCounter").update(updates);
		//System.out.println(increment.get().getUpdateTime());
		
		
		return "Successfully Added : "+idString;
		
	}

	
}
