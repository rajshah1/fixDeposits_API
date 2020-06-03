package systems.rajshah.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;

import systems.rajshah.repository.InvestorInfo;
import systems.rajshah.repository.UserInfo;
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
	
	@PostMapping(value="/addInvestor",consumes = "application/json")
	public InvestorInfo setInvestorInfo(@RequestBody InvestorInfo investorInfo) throws FirebaseAuthException, InterruptedException, ExecutionException {
		
		return ifirebaseuser.postInvestorInfo(investorInfo);
		
	}
	
}
