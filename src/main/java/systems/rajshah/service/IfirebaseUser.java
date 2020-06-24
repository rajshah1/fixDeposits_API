package systems.rajshah.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.firebase.auth.FirebaseAuthException;

import systems.rajshah.model.FdInfo;
import systems.rajshah.model.FullInvestorInfo;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.UserInfo;

public interface IfirebaseUser {
public UserInfo getCurrentUserDetails(String emailId) throws FirebaseAuthException, InterruptedException, ExecutionException;
public String createInvestorInfo(InvestorInfo investInfo,String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException;
public String createFdInfo(FdInfo fdInfo,String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException;
public FullInvestorInfo getfullInfo(String Idvar,String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException;
public List<FullInvestorInfo> getInvestInfoBtDates(QueryObjectDetails queyObject,String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException;
}
