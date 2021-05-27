/**
 * 
 */
package systems.rajshah.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.firebase.auth.FirebaseAuthException;
import com.itextpdf.text.DocumentException;

import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;

/**
 * @author RAJ SHAH
 *
 */
public interface IFirebaseUserOperartions {
	
	 
	public List<InvestorInfo> getUserInfoFromFamilyCode(String currentUid, String id)
			throws InterruptedException, ExecutionException;
	public ByteArrayInputStream generateManagementReport(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException;
	
	public ByteArrayInputStream generateFullReportByFamilyCode(String familyCode, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException;
	
	

}
