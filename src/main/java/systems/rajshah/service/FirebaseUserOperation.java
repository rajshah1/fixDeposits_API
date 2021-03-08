/**
 * 
 */
package systems.rajshah.service;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.itextpdf.text.DocumentException;


import systems.rajshah.model.QueryObjectDetails;

/**
 * @author RAJ SHAH
 *
 */
@Service
public class FirebaseUserOperation implements IFirebaseUserOperartions{
	@Autowired
	private Firestore firestore;

	@Autowired
	private FirebaseAuth firebaseAuth;
	
	@Autowired
	IfirebaseUser firebaseUserImpl;
	
	private static final Logger logger = LoggerFactory.getLogger(FirebaseUserOperation.class);
	@Override
	public ByteArrayInputStream generateManagementReport(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteArrayInputStream generateFullReportByFamilyCode(String familyCode, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		// TODO Auto-generated method stub
		
		
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR,50);
		
		Calendar cal1=Calendar.getInstance();
		cal1.setTime(new Date());
		cal1.add(Calendar.YEAR,-50);
		
		QueryObjectDetails queryObj=new QueryObjectDetails();
		queryObj.setInitialDate(cal1.getTime());
		queryObj.setLastDate(cal.getTime());
		queryObj.setSearchField("startDate");
		
		//logger.debug("In Geerate fullstatByClientID "+queryObj.toString());
		ByteArrayInputStream fullInfoResp=firebaseUserImpl.generateCustomerIntimationReport(familyCode, queryObj, currentUid);
		
		return  fullInfoResp;
	}

	

}
