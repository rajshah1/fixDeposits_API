package systems.rajshah.service;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
//import com.google.protobuf.Timestamp;

import systems.rajshah.model.FdInfo;
import systems.rajshah.model.FullInvestorInfo;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.UserInfo;

@Service
public class FirebaseUserImpl implements IfirebaseUser{
	
	@Override
	public UserInfo getCurrentUserDetails(String emailID) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Firestore dbFirestore = FirestoreClient.getFirestore();
		UserRecord a=FirebaseAuth.getInstance().getUserByEmail(emailID);
		//System.out.println("this is UID:"+CurrentLoggedInUserUID);
	
		/*
		 * ONLY RUN <------ONCE------> This code will create alphacounter document in
		 * {/UID} collection :{a:0,b:0 .........z:0} Map
		 */ 
			/*
			 * if(CurrentLoggedInUserUID!= null) { Map<String,Integer> counts = new
			 * HashMap<>(); for(int i=0;i<26;i++) {
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
	public String createInvestorInfo(InvestorInfo investInfo,String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Firestore dbFirestore = FirestoreClient.getFirestore();
		CollectionReference collRef=dbFirestore.collection(currentUid);
		DocumentSnapshot fullCountMap=collRef.document("alphaCounter").get().get();
		Long currentNumber=(Long)fullCountMap.getData().get(investInfo.getLastName().substring(0, 1).toLowerCase());
		String idString=investInfo.getFirstName().substring(0,1).toUpperCase()+investInfo.getLastName()
		.substring(0,1).toUpperCase()+(currentNumber+1);
		
		investInfo.setId(idString);
		ApiFuture<WriteResult> future=dbFirestore.collection(currentUid).document(idString).set(investInfo);
		future.get();
		//System.out.println("Added on: "+future.get().getUpdateTime());
		Map<String, Object> updates = new HashMap<>();
		updates.put(investInfo.getLastName().substring(0, 1).toLowerCase(), currentNumber+1);
		collRef.document("alphaCounter").update(updates);
		//System.out.println(increment.get().getUpdateTime());

		return idString;
	}

	@Override
	public String createFdInfo(FdInfo fdInfo,String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Firestore dbFirestore = FirestoreClient.getFirestore();
		CollectionReference collRef=dbFirestore.collection(currentUid);
		
		ApiFuture<DocumentReference> future=collRef.document(fdInfo.getId()).collection("fdInfo").add(fdInfo);
		future.get();
		//System.out.println("Added");
		//System.out.println(fdInfo);
	
		return "";
	}

	
	@Override
	public FullInvestorInfo getfullInfo(String Idvar,String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		FullInvestorInfo fullData=new FullInvestorInfo();
		InvestorInfo investInfo=null;
		FdInfo fdInformation=null;
		List<FdInfo> t=new ArrayList<FdInfo>();
		Firestore dbFirestore = FirestoreClient.getFirestore();
		CollectionReference collRef=dbFirestore.collection(currentUid);
		DocumentSnapshot documentInfo=collRef.document(Idvar).get().get();
		ApiFuture<QuerySnapshot> fdInfos=collRef.document(Idvar).collection("fdInfo").get();
		
		  if(documentInfo.exists()) {
			  investInfo=documentInfo.toObject(InvestorInfo.class);
			  fullData.setInvestor(investInfo);
		  }
		for (DocumentSnapshot documentData : fdInfos.get().getDocuments()) {
			fdInformation = documentData.toObject(FdInfo.class);
			  t.add(fdInformation);
		}
		fullData.setFdInfo(t);
		//System.out.println("Full Info  ::: "+fullData);
		return fullData;
	}
	@Override
	public List<FullInvestorInfo> getInvestInfoBtDates(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
	
		Firestore dbFirestore = FirestoreClient.getFirestore();
		List<String> test=new ArrayList<String>();
		List<FullInvestorInfo> listfull= new ArrayList<FullInvestorInfo>();
		CollectionReference collRef=dbFirestore.collection(currentUid);
		String SearchFieldValue=queyObject.getSearchField();
		
		List<QueryDocumentSnapshot> startDateResults=dbFirestore.collectionGroup("fdInfo")
		.whereGreaterThanOrEqualTo(SearchFieldValue,queyObject.getInitialDate())
		.whereLessThanOrEqualTo(SearchFieldValue,queyObject.getLastDate())
		.whereEqualTo("uid", currentUid).get().get().getDocuments();
		
		for (DocumentSnapshot documentData : startDateResults) {
				FullInvestorInfo oneInstance=new FullInvestorInfo();	
				List<FdInfo> fdInformation=new ArrayList<FdInfo>();
				String currentFdId=documentData.getString("id");
				
				if(test.contains(currentFdId)) {
						  List<InvestorInfo> resultListOfInvestors=listfull.stream()
						  .map(e->e.investor).collect(Collectors.toList());
						  			//System.out.println("palo "+resultListOfInvestors);
						  
						  Set<InvestorInfo> resultWhereIdCompareTrue=resultListOfInvestors.stream()
					     .filter(e->e.getId().equals(currentFdId)==true).collect(Collectors.toSet());
							
							  resultWhereIdCompareTrue.forEach(obj->{ int
							  indexValue=resultListOfInvestors.indexOf(obj); FullInvestorInfo oneInstance1=
							  listfull.get(indexValue); List<FdInfo> fdInformation1 =
							  oneInstance1.getFdInfo();
							  fdInformation1.add(documentData.toObject(FdInfo.class));
							  oneInstance1.setFdInfo(fdInformation1); //
							  System.out.println("One Instance"+oneInstance);
							  listfull.set(indexValue,oneInstance1);	  
							  });
							 				  
						  
							/*
							 * for(InvestorInfo investInfoValue:resultWhereIdCompareTrue) { int
							 * indexValue=resultListOfInvestors.indexOf(investInfoValue); oneInstance=
							 * listfull.get(indexValue); fdInformation = oneInstance.getFdInfo();
							 * fdInformation.add(documentData.toObject(FdInfo.class));
							 * oneInstance.setFdInfo(fdInformation); //
							 * System.out.println("One Instance"+oneInstance);
							 * listfull.set(indexValue,oneInstance); }
							 */
							 
				}
				else {
					 DocumentSnapshot docfuture=collRef.document(currentFdId).get().get();
					 oneInstance.setInvestor(docfuture.toObject(InvestorInfo.class)); 
					 fdInformation.add(documentData.toObject(FdInfo.class));
					 oneInstance.setFdInfo(fdInformation);
					 listfull.add(oneInstance);

					 test.add(currentFdId);
					 System.out.println(test);
				}
		}
		//System.out.println(listfull);
		return listfull;
	}

	
	
	@Override
	public List<FullInvestorInfo> getfullInfoByFamilyCode(String Idvar,QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		List<FullInvestorInfo> fullData = new ArrayList<FullInvestorInfo>();
		String Tp=queyObject.getSearchField();
		Firestore dbFirestore = FirestoreClient.getFirestore();
		CollectionReference collRef=dbFirestore.collection(currentUid);
		List<QueryDocumentSnapshot> documentInfo=collRef.whereEqualTo("familyCode",Idvar).get().get().getDocuments();
	if(!documentInfo.isEmpty()) {	
		for(DocumentSnapshot documentwithsameF:documentInfo) {
		//	System.out.println(documentwithsameF);
			 
			List<QueryDocumentSnapshot> tet=collRef.document(documentwithsameF.getString("id")).collection("fdInfo")
			.whereGreaterThanOrEqualTo(Tp,queyObject.getInitialDate()).whereLessThanOrEqualTo(Tp,queyObject.getLastDate())
			.get().get().getDocuments();
			
			//InvestorInfo investInfo=new InvestorInfo();
			//FdInfo fdInformation=null;
			
			if(!tet.isEmpty()) {
				List<FdInfo> t=new ArrayList<FdInfo>();
				FullInvestorInfo te=new FullInvestorInfo();
				InvestorInfo investInfo=documentwithsameF.toObject(InvestorInfo.class);
				//System.out.println(investInfo);
				for (DocumentSnapshot selectfdInfo:tet) {
					FdInfo fdInformation=selectfdInfo.toObject(FdInfo.class);
					t.add(fdInformation);
				}
				te.setInvestor(investInfo);
				te.setFdInfo(t);
				fullData.add(te);
				//System.out.println(te);
			}
		
		}
	}
	else {
		return null;
	}
//	System.out.println(fullData);
	return fullData;
	
}
	
	
	
}
