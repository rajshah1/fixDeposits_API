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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import systems.rajshah.model.FdInfo;
import systems.rajshah.model.FullInvestorInfo;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.UserInfo;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;


@Service
public class FirebaseUserImpl implements IfirebaseUser{
	@Autowired
	private Firestore firestore;
	
	@Autowired
	private FirebaseAuth firebaseAuth;
	
	private static final Logger logger=LoggerFactory.getLogger(FirebaseUserImpl.class);
	
	private String fdInfoVar="fdInfo";
	@Override
	public UserInfo getCurrentUserDetails(String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		UserRecord uInfo=firebaseAuth.getUser(currentUid);
		/*
		 * ONLY RUN <------ONCE------> This code will create alphacounter document in
		 * {/UID} collection :{a:0,b:0 .........z:0} Map
		 */ 
			/*
			 * if(CurrentLoggedInUserUID!= null) { Map<String,Integer> counts = new
			 * HashMap<>(); for(int i=0;i<26;i++) {
			 * counts.put(String.valueOf((char)(i+97)),0); } System.out.println(counts);
			 * ApiFuture<WriteResult>
			 * future=this.firestore.collection(CurrentLoggedInUserUID).document("alphaCounter"
			 * ).set(counts);
			 * System.out.println("I have seen this"+future.get().getUpdateTime()); }
			 */
		
		ApiFuture<DocumentSnapshot> future=this.firestore.collection("users").document(uInfo.getEmail()).get();
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
		CollectionReference collRef=this.firestore.collection(currentUid);
		DocumentSnapshot fullCountMap=collRef.document("alphaCounter").get().get();
		Long currentNumber=(Long)fullCountMap.getData().get(investInfo.getLastName().substring(0, 1).toLowerCase());
		String idString=investInfo.getFirstName().substring(0,1).toUpperCase()+investInfo.getLastName()
		.substring(0,1).toUpperCase()+(currentNumber+1);
		investInfo.setId(idString);
		ApiFuture<WriteResult> future=this.firestore.collection(currentUid).document(idString).set(investInfo);
		future.get();
		logger.info("TargetOperation:documentwithsameF TargetModule:FirebaseUserImpl "
				+ "TargetComponent:Service :: UserCreatedOn",future.get().getUpdateTime());
		Map<String, Object> updates = new HashMap<>();
		updates.put(investInfo.getLastName().substring(0, 1).toLowerCase(), currentNumber+1);
		collRef.document("alphaCounter").update(updates);
		//System.out.println(increment.get().getUpdateTime());

		return idString;
	}

	@Override
	public String createFdInfo(FdInfo fdInfo,String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		CollectionReference collRef=this.firestore.collection(currentUid);
		ApiFuture<DocumentReference> future=collRef.document(fdInfo.getId()).collection(fdInfoVar).add(fdInfo);
		future.get();
		return "";
	}

	
	@Override
	public FullInvestorInfo getfullInfo(String idVar,String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		FullInvestorInfo fullData=new FullInvestorInfo();
		InvestorInfo investInfo=null;
		FdInfo fdInformation=null;
		List<FdInfo> t=new ArrayList<>();
		CollectionReference collRef=this.firestore.collection(currentUid);
		DocumentSnapshot documentInfo=collRef.document(idVar).get().get();
		ApiFuture<QuerySnapshot> fdInfos=collRef.document(idVar).collection(fdInfoVar).get();
		
		  if(documentInfo.exists()) {
			  investInfo=documentInfo.toObject(InvestorInfo.class);
			  fullData.setInvestor(investInfo);
		  }
		for (DocumentSnapshot documentData : fdInfos.get().getDocuments()) {
			fdInformation = documentData.toObject(FdInfo.class);
			  t.add(fdInformation);
		}
		fullData.setFdInfo(t);
		return fullData;
	}
	
	
	@Override
	public List<FullInvestorInfo> getInvestInfoBtDates(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		List<String> test=new ArrayList<>();
		List<FullInvestorInfo> listfull= new ArrayList<>();
		CollectionReference collRef=this.firestore.collection(currentUid);
		String searchFieldValue=queyObject.getSearchField();
		
		List<QueryDocumentSnapshot> startDateResults=this.firestore.collectionGroup(fdInfoVar)
		.whereGreaterThanOrEqualTo(searchFieldValue,queyObject.getInitialDate())
		.whereLessThanOrEqualTo(searchFieldValue,queyObject.getLastDate())
		.whereEqualTo("uid", currentUid).get().get().getDocuments();
		
		for (DocumentSnapshot documentData : startDateResults) {
				FullInvestorInfo oneInstance=new FullInvestorInfo();	
				List<FdInfo> fdInformation=new ArrayList<>();
				String currentFdId=documentData.getString("id");
				
				if(test.contains(currentFdId)) {
						  List<InvestorInfo> resultListOfInvestors=listfull.stream()
						  .map(e->e.getInvestor()).collect(Collectors.toList());
						  if(!resultListOfInvestors.isEmpty()) {
							  Set<InvestorInfo> resultWhereIdCompareTrue=resultListOfInvestors.stream()
									     .filter(e->e.getId().equals(currentFdId)).collect(Collectors.toSet());
							  
											  resultWhereIdCompareTrue.forEach(obj->{ 
											  int indexValue=resultListOfInvestors.indexOf(obj); 
											  FullInvestorInfo oneInstance1=listfull.get(indexValue); 
											  List<FdInfo> fdInformation1 =oneInstance1.getFdInfo();
											  fdInformation1.add(documentData.toObject(FdInfo.class));
											  oneInstance1.setFdInfo(fdInformation1);
											  
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
				}
		}
		return listfull;
	}

	
	
	@Override
	public List<FullInvestorInfo> getfullInfoByFamilyCode(String idVar,QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		List<FullInvestorInfo> fullData = new ArrayList<>();
		String Tp=queyObject.getSearchField();
		CollectionReference collRef=this.firestore.collection(currentUid);
		List<QueryDocumentSnapshot> documentInfo=collRef.whereEqualTo("familyCode",idVar).get().get().getDocuments();
	if(!documentInfo.isEmpty()) {	
		for(DocumentSnapshot documentwithsameF:documentInfo) {
			logger.debug("TargetOperation:documentwithsameF TargetModule:FirebaseUserImpl "
					+ "TargetComponent:Service :: DocumentWithSameFamily", documentwithsameF.toString());
			List<QueryDocumentSnapshot> tet=collRef.document(documentwithsameF.getString("id")).collection(fdInfoVar)
			.whereGreaterThanOrEqualTo(Tp,queyObject.getInitialDate()).whereLessThanOrEqualTo(Tp,queyObject.getLastDate())
			.get().get().getDocuments();
			
			if(!tet.isEmpty()) {
				List<FdInfo> t=new ArrayList<>();
				FullInvestorInfo te=new FullInvestorInfo();
				InvestorInfo investInfo=documentwithsameF.toObject(InvestorInfo.class);				
				logger.debug("TargetOperation:documentwithsameF TargetModule:FirebaseUserImpl "
						+ "TargetComponent:Service :: investInfo",investInfo);
				tet.stream().forEach(e->{
					FdInfo fdInformation=e.toObject(FdInfo.class);
					t.add(fdInformation);

				});
				te.setInvestor(investInfo);
				te.setFdInfo(t);
				fullData.add(te);

			}
		
		}
	}
	return fullData;
	
}

	@Override
	public ByteArrayInputStream generateCustomerIntimationReport(String idVar,QueryObjectDetails queyObject, String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		//from currentID find User Details
		UserInfo uInfo=this.getCurrentUserDetails(currentUid);
		InvestorInfo familyheadaddressName=this.getFamliyHeadForFamilyCode(idVar, currentUid);
		Document doc=new Document();
    	doc.setMargins(10,10,0,0);
    	PdfPTable table=new PdfPTable(6);
    	table.setWidthPercentage(90);
    	table.setSpacingBefore(5f);
    	float[] columnWidths = {1f,3f,4f,3f,3f,4f};
    	table.setWidths(columnWidths);
    	List<FullInvestorInfo> genListForFamilyCode=this.getfullInfoByFamilyCode(idVar, queyObject, currentUid);
    	ByteArrayOutputStream bout=new ByteArrayOutputStream();
    	try {

    				PdfWriter.getInstance(doc, bout);
    				doc.open();
    				addMetadata(doc,uInfo.getName());
    				myInfoHeader(doc,uInfo);
    				reciverInfoAddress(doc,familyheadaddressName);
    				generalInfoWidrowal(doc,queyObject);
    				PdfPTable table1=tablegenerator(table);
    				addlistToPdf(doc,table1,genListForFamilyCode);
    				footerCustInmp(doc);
    				doc.close();
    		
    			} catch (DocumentException e) {
    				logger.error("Error Occurred --> Context ", e);
    			}

		
		
		return new ByteArrayInputStream(bout.toByteArray());
		
	}
	
	private static void addMetadata(Document doc,String currentUserName){
    	doc.addCreationDate();
		doc.addCreator(currentUserName);
		doc.addAuthor("rajshah.systems");
		doc.addLanguage("English");

    }
    private static void myInfoHeader(Document doc,UserInfo uInfoVal) throws DocumentException{
    	String contactInfo="";
    	if(uInfoVal.getMobileNo().length()>12) {
    		String [] splitval=uInfoVal.getMobileNo().split(",");
			  for(int i=0;i<splitval.length;i++) { if(splitval[i].length()<10) {
			  contactInfo=contactInfo.concat(" (O) "+splitval[i]); } else {
			  contactInfo=contactInfo.concat(" (M) "+splitval[i]); } }
			  }
    	else
    		contactInfo=contactInfo.concat(uInfoVal.getMobileNo());

    	Chunk ch1=new Chunk(" "+uInfoVal.getName().toUpperCase());
    	Chunk ch2=new Chunk("\n "+uInfoVal.getaddressOne());
    	Chunk ch3=new Chunk("\n "+uInfoVal.getaddressTwo());
    	Chunk ch5=new Chunk();
    	if(uInfoVal.getaddressThree().length()>1) 
    		ch5.append("\n"+uInfoVal.getaddressThree());
    	
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
    private static void reciverInfoAddress(Document doc, InvestorInfo toWhom) throws DocumentException{
    	logger.trace("Inside reciverInfoAddress"+toWhom.getFirstName());
    	if(toWhom.getAddress().length()>0) {
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
    }
    private static void generalInfoWidrowal(Document doc,QueryObjectDetails queyObject) throws DocumentException {
		SimpleDateFormat sm = new SimpleDateFormat("dd-mm-yyyy");
    	Chunk ch10=new Chunk("\n \n Dear Sir/Madam,");
    	Chunk ch11=new Chunk("\n \t \t \t \t \t \t \t \t \t \t \t \t Following FDR are matured on below mentioned dates .So, Kindly Contact us.");
    	Chunk ch12=new Chunk("\n \n \t \t \t \t \t \t \t \t \t \t \t \t MATURITY FOR THE PERIOD : ");
    	Chunk ch13=new Chunk("\t \t"+sm.format(queyObject.getInitialDate())+" TO "+sm.format(queyObject.getLastDate()));
   
    	Paragraph p1=new Paragraph();
    	p1.add(ch10);p1.add(ch11);p1.add(ch12);p1.add(ch13);
    	p1.setSpacingAfter(10F); 
    	DottedLineSeparator line =new DottedLineSeparator();
    	doc.add(p1);
    	doc.add(line);
	}
    private static PdfPTable tablegenerator(PdfPTable table) {

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
			logger.error("Context", e);
		}

    }

	@Override
	public InvestorInfo getFamliyHeadForFamilyCode(String idVar, String currentId)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		
		CollectionReference collRef=this.firestore.collection(currentId);
		List<String> selfValues=new ArrayList<>();
		selfValues.add("Self");selfValues.add("self");selfValues.add("SELF");
		List<QueryDocumentSnapshot> documentInfo=collRef.whereEqualTo("familyCode",idVar)
		.whereIn("familyHead",selfValues).get().get().getDocuments();
			//System.out.println(documentInfo.get(0).toObject(InvestorInfo.class));z
		if(documentInfo.size()==1)
			return documentInfo.get(0).toObject(InvestorInfo.class);
		else
			return null;		
	}

	@Override
	public List<ByteArrayInputStream> getCustReportByDates(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		String searchFieldValue=queyObject.getSearchField();
		List<ByteArrayInputStream> response=new ArrayList<>(); 
		List<QueryDocumentSnapshot> startDateResults=this.firestore.collectionGroup(fdInfoVar)
		.whereGreaterThanOrEqualTo(searchFieldValue,queyObject.getInitialDate())
		.whereLessThanOrEqualTo(searchFieldValue,queyObject.getLastDate())
		.whereEqualTo("uid", currentUid).get().get().getDocuments();
		
		startDateResults.parallelStream().forEach(e->{
				try {
					String id=this.firestore.collection(currentUid).document(e.getString("id")).get().get().getString("familyCode");
					logger.info("falmily Code for which generate Report "+id);
					ByteArrayInputStream bis = this.generateCustomerIntimationReport(id,queyObject,currentUid);
					response.add(bis);
				} catch (FirebaseAuthException | ExecutionException | DocumentException e1) {
						logger.error("Error Occurred --> Context", e1);
				} catch (InterruptedException e2) {
					logger.error("InterruptedException Occurred For Operation: "
							+ "generateCustomerIntimationReport in Method: getCustReportByDates :: Error Trace : "+e2);
					Thread.currentThread().interrupt();
				}
		});
		return response;
	}



	
	
	
}
