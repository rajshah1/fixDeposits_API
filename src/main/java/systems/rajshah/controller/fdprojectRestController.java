package systems.rajshah.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;

import systems.rajshah.model.FdInfo;
import systems.rajshah.model.FullInvestorInfo;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.UserInfo;
import systems.rajshah.service.IfirebaseUser;

@RestController

public class fdprojectRestController {
	@Autowired(required = false)
	IfirebaseUser ifirebaseuser;
	
	@GetMapping("/")
	public String hello() {
		return "Hello";
	}
	
	@GetMapping(value="/users/{emailID}",produces="application/json")
	public UserInfo getUserInfoByemail(@PathVariable("emailID") String emailID) throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.getCurrentUserDetails(emailID);
	}
	
	@PostMapping(value="/{currentUid}/addInvestor",consumes = "application/json")			
	public String creaInvestorInfo(@RequestBody InvestorInfo investorInfo,@PathVariable("currentUid") String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		
		return ifirebaseuser.createInvestorInfo(investorInfo,currentUid);
		
	}
	
	@PostMapping(value="/{currentUid}/addfdinfo",consumes = "application/json")
	public String addFdInfo(@RequestBody FdInfo fdInfo,@PathVariable("currentUid") String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.createFdInfo(fdInfo,currentUid);
	}
	
	@GetMapping(value="/{currentUid}/getCustomer/{id}",produces = "application/json")
	public FullInvestorInfo getCustomerInfo(@PathVariable("id") String idvarable,@PathVariable("currentUid") String currentUid ) throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.getfullInfo(idvarable,currentUid);
	}
	
	@GetMapping(value="/{currentUid}/getFdInfobyStartDate",produces = "application/json")
	public List<FullInvestorInfo> getAllBetweenRangesStart(@RequestBody QueryObjectDetails queryFullDetails,@PathVariable("currentUid") String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException{
		return ifirebaseuser.getInvestInfoBtStartDates(queryFullDetails, currentUid);
	}
	
	@GetMapping(value="/{currentUid}/getFdInfobyMaturityDate",produces = "application/json")
	public List<FullInvestorInfo> getAllBetweenRangesMaturaty(@RequestBody QueryObjectDetails queryFullDetails,@PathVariable("currentUid") String currentUid) throws FirebaseAuthException, InterruptedException, ExecutionException{
		return ifirebaseuser.getInvestInfoBtMaturityDates(queryFullDetails, currentUid);
	}
	

	
	
	
}
