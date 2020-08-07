package systems.rajshah.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
	public UserInfo getCurrentUserDetails(String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Firestore dbFirestore = FirestoreClient.getFirestore();
		UserRecord uInfo=FirebaseAuth.getInstance().getUser(currentUid);
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

		
		ApiFuture<DocumentSnapshot> future=dbFirestore.collection("users").document(uInfo.getEmail()).get();
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
	public ByteArrayInputStream generateCustomerIntimationReport(String Idvar,QueryObjectDetails queyObject, String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		// TODO Auto-generated method stub
		//from currentID find User Details
		UserInfo uInfo=this.getCurrentUserDetails(currentUid);
		InvestorInfo familyheadaddressName=this.getFamliyHeadForFamilyCode(Idvar, currentUid);
		Document doc=new Document();
    	doc.setMargins(10,10,0,0);
    	PdfPTable table=new PdfPTable(6);
    	table.setWidthPercentage(90);
    	table.setSpacingBefore(5f);
    	float[] columnWidths = {1f,3f,4f,3f,3f,4f};
    	table.setWidths(columnWidths);
    	List<FullInvestorInfo> genListForFamilyCode=this.getfullInfoByFamilyCode(Idvar, queyObject, currentUid);
    	ByteArrayOutputStream bout=new ByteArrayOutputStream();
    	try {
    		//		Font fs15=new Font(Font.FontFamily.TIMES_ROMAN,15);
    				Font fs10=FontFactory.getFont(FontFactory.COURIER,10);
    				//PdfWriter.getInstance(doc, new FileOutputStream("test.pdf"));
    				PdfWriter.getInstance(doc, bout);
    				doc.open();
    				addMetadata(doc,uInfo.getName());
    				myInfoHeader(doc,fs10,uInfo);
    				reciverInfoAddress(doc,familyheadaddressName);
    				generalInfoWidrowal(doc,queyObject);
    				PdfPTable table1=tablegenerator(doc,table);
    				addlistToPdf(doc,table1,genListForFamilyCode);
    				footerCustInmp(doc);
    				doc.close();
    		
    			} catch (DocumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}

		
		
		return new ByteArrayInputStream(bout.toByteArray());
		
	}
	
	private static void addMetadata(Document doc,String CurrentUserName){
    	doc.addCreationDate();
		doc.addCreator(CurrentUserName);
		doc.addAuthor("rajshah.systems");
		doc.addLanguage("English");

    }
    private static void myInfoHeader(Document doc,Font f1,UserInfo uInfoVal) throws DocumentException{
    	String addressLine1="";
    	String addressLine2="";
    	String addressLine3="";
    	String [] t=uInfoVal.getAddress().toUpperCase().split(",");
    	if(t.length>=5) {
    		if(((t[0]+t[1]).length()<=25)&&((t[2]).length()<=15)) {
        	 	addressLine1=(t[0]+","+t[1]+","+t[2]);
        	 	addressLine2=(t[3]+","+t[4]);
        	}
        	else {
        		addressLine1=(t[0]+","+t[1]);
        	 	addressLine2=(t[2]+","+t[3]);
        	 	addressLine3=(t[4]);
        	}
    	}
    	else {
        	 	addressLine1=(t[0]+","+t[1]);
        	 	addressLine2=(t[2]+","+t[3]);
    	}
    	//uInfoVal.setMobileNo("9825039684,26622505");
    	String contactInfo=new String();
    //	System.out.println(uInfoVal.getMobileNo());
    	if(uInfoVal.getMobileNo().length()>12) {
    		String [] splitval=uInfoVal.getMobileNo().split(",");
			
			  for(int i=0;i<splitval.length;i++) { if(splitval[i].length()<10) {
			  contactInfo=contactInfo.concat(" (O) "+splitval[i]); } else {
			  contactInfo=contactInfo.concat(" (M) "+splitval[i]); } }
			 
			  }
			  
    	else {
    	
    		contactInfo=contactInfo.concat(uInfoVal.getMobileNo());
    	}
    		
    		
    	
    	Chunk ch1=new Chunk(" "+uInfoVal.getName().toUpperCase());
    	Chunk ch2=new Chunk("\n "+addressLine1);
    	Chunk ch3=new Chunk("\n "+addressLine2);
    	Chunk ch5=new Chunk();
    	if(addressLine3.length()>1) 
    		ch5.append("\n"+addressLine3);
    	
    	Chunk ch4=new Chunk("\n TEL : "+contactInfo);
    	
    	Paragraph p1=new Paragraph();
    	p1.add(ch1);p1.add(ch2);p1.add(ch3);
    	if(!ch5.isEmpty())
    		p1.add(ch5);
    	p1.add(ch4);
    	
    	p1.setAlignment(Paragraph.ALIGN_CENTER);
    	p1.setSpacingAfter(20F);
    	doc.add(p1);
    	
    }
    private static void reciverInfoAddress(Document doc,InvestorInfo toWhom) throws DocumentException{
    	String [] addressSplit =toWhom.getAddress().toUpperCase().split(",");
    	Chunk add7=new Chunk("\n ");
    	Chunk add8=new Chunk("\n ");
    	Chunk add9=new Chunk("\n ");
    	
    	if(addressSplit[0].length()+addressSplit[1].length()+addressSplit[2].length()<=36) {
    		add7.append(addressSplit[0]+","+addressSplit[1]+","+addressSplit[2]);
    		if(addressSplit.length==4) {
    			add8.append(addressSplit[3]);}
    		else if(addressSplit.length==5)
    			add8.append(addressSplit[3]+","+addressSplit[4]);
    		else if(addressSplit.length==6) {
    			add8.append(addressSplit[3]+","+addressSplit[4]);
    			add9.append(addressSplit[5]);
    		}
    			
    	}else if(addressSplit[0].length()+addressSplit[1].length()+addressSplit[2].length()>36) {
    		add7.append(addressSplit[0]+","+addressSplit[1]);
    		add8.append(addressSplit[2]+","+addressSplit[3]);
    		if(addressSplit.length==5)
    			add9.append(addressSplit[4]);
    		else if(addressSplit.length==6)
    			add9.append(addressSplit[4]+","+addressSplit[5]);
    	}
    	Chunk ch5=new Chunk(" To,");
    	Chunk ch6=new Chunk("\n "+toWhom.getFamilyCode()+": "
    	+toWhom.getFirstName().toUpperCase()+" "+toWhom.getMiddleName().toUpperCase()
    	+" "+toWhom.getLastName().toUpperCase());
    	
    	
    	Paragraph p1=new Paragraph();
    	p1.add(ch5);p1.add(ch6);p1.add(add7);p1.add(add8);
    	if(!add9.isEmpty())
    		p1.add(add9);
    	p1.setAlignment(Paragraph.ALIGN_LEFT);
    	p1.setSpacingAfter(20F); 
    	doc.add(p1); 	
    }
    private static void generalInfoWidrowal(Document doc,QueryObjectDetails queyObject) throws DocumentException {
		// TODO Auto-generated method stub
		SimpleDateFormat sm = new SimpleDateFormat("dd-mm-yyyy");
		//System.out.println(queyObject.getInitialDate()+"\n"+queyObject.getLastDate());
    	Chunk ch10=new Chunk("\n \n Dear Sir/Madam,");
    	Chunk ch11=new Chunk("\n \t \t \t \t \t \t \t \t \t \t \t \t Following FDR are matured on below mentioned dates .So, Kindly Contact us.");
    	Chunk ch12=new Chunk("\n \n \t \t \t \t \t \t \t \t \t \t \t \t MATURITY FOR THE PERIOD : ");
    	Chunk ch13=new Chunk("\t \t	 "+sm.format(queyObject.getInitialDate())+" TO "+sm.format(queyObject.getLastDate()));
    
    	
    	Paragraph p1=new Paragraph();
    	p1.add(ch10);p1.add(ch11);p1.add(ch12);p1.add(ch13);
    	p1.setSpacingAfter(10F); 
    	DottedLineSeparator line =new DottedLineSeparator();
    	doc.add(p1);
    	doc.add(line);
	}
    private static PdfPTable tablegenerator(Document doc,PdfPTable table) throws DocumentException {
		// TODO Auto-generated method stub

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
		
    	return table;
	}
    static int fdInfoListCounter=0;
    private static void addlistToPdf(Document doc,PdfPTable table,List<FullInvestorInfo> dataList) throws DocumentException{
		//System.out.println(dataList.toString());
    	IntStream.range(0,dataList.size()).parallel().forEach(e->{
    		IntStream.range(0,dataList.get(e).getFdInfo().size()).parallel().forEach(i->{
    	    	SimpleDateFormat sm = new SimpleDateFormat("dd-mm-yyyy");
        		String matuDate = sm.format(dataList.get(e).getFdInfo().get(i).getMaturityDate());
        		String strDate = sm.format(dataList.get(e).getFdInfo().get(i).getStartDate());
        		PdfPCell matudepoCell=new PdfPCell(new Phrase(matuDate+"\n \n"+strDate));
        		PdfPCell investoreNameCell=new PdfPCell(new Phrase(dataList.get(e).getInvestor().getFirstName()+" "
        		+dataList.get(e).getInvestor().getLastName()));
    			PdfPCell fdCompanyCell=new PdfPCell(new Phrase(dataList.get(e).getFdInfo().get(i).getComapnyName()));
        		PdfPCell matudepoAmmCell=new PdfPCell(new Phrase(dataList.get(e).getFdInfo().get(i).getAmount()+"\n \n"+
        		dataList.get(e).getFdInfo().get(i).getMaturatyAmount()));
        		PdfPCell certiCell=new PdfPCell(new Phrase(dataList.get(e).getFdInfo().get(i).getCertificateNo()));	
        		
				/*
				 * System.out.println("E "+e+"I "+i+"SR NO "+fdInfoListCounter);
				 * System.out.println("Dates are "+e+" : "+dataList.get(e).getFdInfo().get(i).
				 * getStartDate()+"   "+dataList.get(e).getFdInfo().get(i).getMaturityDate());
				 * System.out.println("Names Are :"+dataList.get(e).getInvestor().getFirstName()
				 * +"   "+dataList.get(e).getInvestor().getLastName());
				 * System.out.println("fdCopanyNmae "+dataList.get(e).getFdInfo().get(i).
				 * getComapnyName());
				 * System.out.println("Amount Are :"+dataList.get(e).getFdInfo().get(i).
				 * getAmount()+"   "+dataList.get(e).getFdInfo().get(i).getMaturatyAmount());
				 * System.out.println("Certificate No"+dataList.get(e).getFdInfo().get(i).
				 * getCertificateNo());
				 */		
        		matudepoCell.setVerticalAlignment(Phrase.ALIGN_CENTER);
        		matudepoCell.setHorizontalAlignment(Phrase.ALIGN_CENTER);
        		investoreNameCell.setVerticalAlignment(Phrase.ALIGN_CENTER);
        		investoreNameCell.setHorizontalAlignment(Phrase.ALIGN_CENTER);
        		fdCompanyCell.setVerticalAlignment(Phrase.ALIGN_CENTER);
        		fdCompanyCell.setHorizontalAlignment(Phrase.ALIGN_CENTER);
        		matudepoAmmCell.setVerticalAlignment(Phrase.ALIGN_CENTER);
        		matudepoAmmCell.setHorizontalAlignment(Phrase.ALIGN_CENTER);
        		certiCell.setVerticalAlignment(Phrase.ALIGN_CENTER);
        		certiCell.setHorizontalAlignment(Phrase.ALIGN_CENTER);
        		table.addCell(""+(++fdInfoListCounter));
        		table.addCell(matudepoCell);
        		table.addCell(investoreNameCell);
        		table.addCell(fdCompanyCell);
        		table.addCell(matudepoAmmCell);
        		table.addCell(certiCell);
    		});
    	});
    	fdInfoListCounter=0;
    	table.setSpacingAfter(20F);	
    	doc.add(table); 	
    }
    
    
    private static void footerCustInmp(Document doc) {
    	DottedLineSeparator line =new DottedLineSeparator();
    	try {
			doc.add(line);
			Paragraph p1=new Paragraph("Please Ignore Entries if Any of the above FDR's were premature");
			p1.setAlignment(Paragraph.ALIGN_CENTER);
			doc.add(p1);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    
    

	@Override
	public InvestorInfo getFamliyHeadForFamilyCode(String Idvar, String currentId)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		Firestore dbFirestore = FirestoreClient.getFirestore();
		CollectionReference collRef=dbFirestore.collection(currentId);
		List<String> selfValues=new ArrayList<>();
		selfValues.add("Self");selfValues.add("self");selfValues.add("SELF");
		List<QueryDocumentSnapshot> documentInfo=collRef.whereEqualTo("familyCode",Idvar)
		.whereIn("familyHead",selfValues).get().get().getDocuments();
			//System.out.println(documentInfo.get(0).toObject(InvestorInfo.class));z
		if(documentInfo.size()==1)
			return documentInfo.get(0).toObject(InvestorInfo.class);
		else
			return null;
		
			
	}

	
	
	
}
