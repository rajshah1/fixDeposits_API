package systems.rajshah.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import systems.rajshah.model.FdInfo;
import systems.rajshah.model.FullInvestorInfo;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.UserInfo;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;


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
						  if(!resultListOfInvestors.isEmpty()) {
							  Set<InvestorInfo> resultWhereIdCompareTrue=resultListOfInvestors.stream()
									     .filter(e->e.getId().equals(currentFdId)==true).collect(Collectors.toSet());
							  
											  resultWhereIdCompareTrue.forEach(obj->{ 
											  int indexValue=resultListOfInvestors.indexOf(obj); 
											  FullInvestorInfo oneInstance1=listfull.get(indexValue); 
											  List<FdInfo> fdInformation1 =oneInstance1.getFdInfo();
											  fdInformation1.add(documentData.toObject(FdInfo.class));
											  oneInstance1.setFdInfo(fdInformation1); //
								//			  System.out.println("One Instance"+oneInstance);
											  listfull.set(indexValue,oneInstance1);	  
									});
				
						  }	 
				}
				else {
					 DocumentSnapshot docfuture=collRef.document(currentFdId).get().get();
					 oneInstance.setInvestor(docfuture.toObject(InvestorInfo.class)); 
					 fdInformation.add(documentData.toObject(FdInfo.class));
					 oneInstance.setFdInfo(fdInformation);
					 listfull.add(oneInstance);

					 test.add(currentFdId);
			//		 System.out.println(test);
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
				tet.stream().forEach(e->{
					FdInfo fdInformation=e.toObject(FdInfo.class);
					t.add(fdInformation);

				});
				
				/*
				 * for (DocumentSnapshot selectfdInfo:tet) { FdInfo
				 * fdInformation=selectfdInfo.toObject(FdInfo.class); t.add(fdInformation); }
				 */
				te.setInvestor(investInfo);
				te.setFdInfo(t);
				fullData.add(te);

			}
		
		}
	}
	else {
		return null;
	}
	return fullData;
	
}

	@Override
	public Object generateCustomerIntimationReport(String Idvar,QueryObjectDetails queyObject, String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Document doc=new Document();
    	doc.setMargins(10,10,0,0);
    	try {
    		//		Font fs15=new Font(Font.FontFamily.TIMES_ROMAN,15);
    				Font fs10=FontFactory.getFont(FontFactory.COURIER,10);
    				PdfWriter.getInstance(doc, new FileOutputStream("./pdffiles/test.pdf"));
    				doc.open();
    				addMetadata(doc);
    				myInfoHeader(doc,fs10);
    				reciverInfoAddress(doc);
    				generalInfoWidrowal(doc);
    				tablegenerator(doc);
    				doc.close();
    		
    			} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (DocumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}

		
		
		return null;
		
	}
	
	private static void addMetadata(Document doc){
    	doc.addCreationDate();
		doc.addCreator("Raj shah");
		doc.addAuthor("Raj Shah");
		doc.addLanguage("English");

    }
    private static void myInfoHeader(Document doc,Font f1) throws DocumentException{
    	//Paragraph p1=new Paragraph("Client Investment Report",f1);
    	//p1.setAlignment(Paragraph.ALIGN_CENTER);
    	//addEmptyLine(p1, 1);
    	//doc.add(p1);
    	Chunk ch1=new Chunk(" Pranav K shah");
    	Chunk ch2=new Chunk("\n 42,Shreeram cloth Market,Revdibazar,");
    	Chunk ch3=new Chunk("\n Crosslane Ahmedabad- 380002");
    	Chunk ch4=new Chunk("\n TEL :(O) 22110395 (M)9825039684");
    	
    	Phrase phrase = new Phrase();
    	phrase.add(ch1);
    	phrase.add(ch2);
    	phrase.add(ch3);
    	phrase.add(ch4);
    	
    	Paragraph p1=new Paragraph();
    	p1.add(phrase);
    	p1.setAlignment(Paragraph.ALIGN_CENTER);
    	p1.setSpacingAfter(20);
    	doc.add(p1);
    	
    }
    private static void reciverInfoAddress(Document doc) throws DocumentException{
    	Chunk ch5=new Chunk(" To,");
    	Chunk ch6=new Chunk("\n P05: Rameshbhai m patel");
    	Chunk add7=new Chunk("\n Arvind Colony,N/R,Commisioner off");
    	Chunk add8=new Chunk("\n Shaibaug road,");
    	Chunk add9=new Chunk("\n Ahmedabad-380004");
    	
    	Phrase phrase = new Phrase();
    	phrase.add(ch5);
    	phrase.add(ch6);
    	phrase.add(add7);
    	phrase.add(add8);
    	phrase.add(add9);
    	
    	Paragraph p1=new Paragraph();
    	p1.add(phrase);
    	p1.setAlignment(Paragraph.ALIGN_LEFT);
    	p1.setSpacingAfter(20F); 
    	doc.add(p1); 	
    }
    private static void generalInfoWidrowal(Document doc) throws DocumentException {
		// TODO Auto-generated method stub
    	Chunk ch10=new Chunk("\n \n Dear Sir/Madam,");
    	Chunk ch11=new Chunk("\n \t \t \t \t \t \t \t \t \t \t \t \t Following FDR are matured on below mentioned dates .So, Kindly Contact us.");
    	Chunk ch12=new Chunk("\n \n \t \t \t \t \t \t \t \t \t \t \t \t MATURITY FOR THE PERIOD : ");
    	Chunk ch13=new Chunk("\t \t	 01/02/1997 TO 03/02/1997");
    	Phrase phrase = new Phrase();
    	phrase.add(ch10);
    	phrase.add(ch11);
    	phrase.add(ch12);
    	phrase.add(ch13);
    	
    	Paragraph p1=new Paragraph();
    	p1.add(phrase);
    	p1.setSpacingAfter(10F); 
    	DottedLineSeparator line =new DottedLineSeparator();
    	doc.add(p1);
    	doc.add(line);
	}
    private static void tablegenerator(Document doc) throws DocumentException {
		// TODO Auto-generated method stub
    	PdfPTable table=new PdfPTable(6);
    	table.setWidthPercentage(90);
    	table.setSpacingBefore(5f);
    	float[] columnWidths = {1f,3f,4f,3f,3f,4f};
    	table.setWidths(columnWidths);

    	Stream.of("SR"," MATU.DATE \n DEPO.DATE","INVESTOR NAME","COMPANY NAME"," DEPO. AMT \n MATU. AMT","CERTIFICTE NO.")
    	.forEach(e->{
    		PdfPCell headerCell=new PdfPCell();
    		headerCell.setBorderWidth(1);
    		headerCell.setVerticalAlignment(Element.ALIGN_CENTER);
    		headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		headerCell.setPadding(5f);
    		headerCell.setUseBorderPadding(true);


    		headerCell.setPhrase(new Phrase(e));
    		table.addCell(headerCell);
    		
    	});
		
    	doc.add(table);
	}

	
	
	
}
