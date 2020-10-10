package systems.rajshah.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.firebase.auth.FirebaseAuthException;
import com.itextpdf.text.DocumentException;

import systems.rajshah.model.FdInfo;
import systems.rajshah.model.FullInvestorInfo;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.ReportGenObject;
import systems.rajshah.model.UserInfo;

public interface IfirebaseUser {
	public UserInfo getCurrentUserDetails(String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException;

	public String createInvestorInfo(InvestorInfo investInfo, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException;

	public String createFdInfo(FdInfo fdInfo, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException;

	public FullInvestorInfo getfullInfo(String Idvar, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException;

	public List<FullInvestorInfo> getInvestInfoBtDates(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException;

	public List<FullInvestorInfo> getfullInfoByFamilyCode(String Idvar, QueryObjectDetails queyObject,
			String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException;

	public ByteArrayInputStream generateCustomerIntimationReport(String Idvar, QueryObjectDetails queyObject,
			String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException;

	public InvestorInfo getFamliyHeadForFamilyCode(String Idvar, String currentId)
			throws FirebaseAuthException, InterruptedException, ExecutionException;

	public Map<String, ReportGenObject> getReportJSONService(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException;

	public ByteArrayInputStream generateFullClientReport(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException;
}
