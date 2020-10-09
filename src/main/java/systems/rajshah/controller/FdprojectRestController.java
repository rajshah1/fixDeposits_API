package systems.rajshah.controller;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.itextpdf.text.DocumentException;

import systems.rajshah.model.FdInfo;
import systems.rajshah.model.FullInvestorInfo;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.ReportGenObject;
import systems.rajshah.model.UserInfo;
import systems.rajshah.service.IfirebaseUser;

@RestController
public class FdprojectRestController {
	@Autowired(required = false)
	IfirebaseUser ifirebaseuser;

	@GetMapping("/")
	public ResponseEntity<String> greetMessage() {
		return new ResponseEntity<String>("***** Welcome To Data CRM System .Please Authenticate *****",
				HttpStatus.UNAUTHORIZED);
	}

	@GetMapping(value = "/users/{currentUid}", produces = "application/json")
	public UserInfo getUserInfoByemail(@PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.getCurrentUserDetails(currentUid);
	}

	@PostMapping(value = "/{currentUid}/addInvestor", consumes = "application/json")
	public String creaInvestorInfo(@RequestBody InvestorInfo investorInfo,
			@PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.createInvestorInfo(investorInfo, currentUid);
	}

	@PostMapping(value = "/{currentUid}/addfdinfo", consumes = "application/json")
	public String addFdInfo(@RequestBody FdInfo fdInfo, @PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.createFdInfo(fdInfo, currentUid);
	}

	@GetMapping(value = "/{currentUid}/getCustomer/{id}", produces = "application/json")
	public FullInvestorInfo getCustomerInfo(@PathVariable("id") String idvarable,
			@PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.getfullInfo(idvarable, currentUid);
	}

	@GetMapping(value = "/{currentUid}/getClientDataByRange", produces = "application/json")
	public List<FullInvestorInfo> getAllBetweenRangesMaturaty(@RequestBody QueryObjectDetails queryFullDetails,
			@PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.getInvestInfoBtDates(queryFullDetails, currentUid);
	}

	@GetMapping(value = "/{currentUid}/getCustomersByFid/{id}", produces = "application/json")
	public List<FullInvestorInfo> getInfoByFamilyCode(@PathVariable("id") String idvarable,
			@RequestBody QueryObjectDetails queryFullDetails, @PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.getfullInfoByFamilyCode(idvarable, queryFullDetails, currentUid);
	}

	@GetMapping(value = "/{currentUid}/generateCustIntiByFid/{id}")
	public ResponseEntity<InputStreamResource> generateCustInByFid(@PathVariable("id") String idvarable,
			@RequestBody QueryObjectDetails queryFullDetails, @PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		ByteArrayInputStream bis = ifirebaseuser.generateCustomerIntimationReport(idvarable, queryFullDetails,
				currentUid);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=" + idvarable + ".pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}

	@GetMapping(value = "/{currentUid}/getFamilyHeadDetails/{id}", produces = "application/json")
	public InvestorInfo getFamilyHeadDetails(@PathVariable("id") String idvarable,
			@PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		return ifirebaseuser.getFamliyHeadForFamilyCode(idvarable, currentUid);
	}

	@GetMapping(value = "/{currentUid}/getReportJSON")
	public ResponseEntity<Map<String, ReportGenObject>> getReportJSON(@PathVariable("currentUid") String currentUid,
			@RequestBody QueryObjectDetails queryFullDetails)
			throws FirebaseAuthException, InterruptedException, ExecutionException {
		Map<String, ReportGenObject> responseMap = ifirebaseuser.getReportJSONService(queryFullDetails, currentUid);
		ResponseEntity.status(HttpStatus.ACCEPTED).build();
		return ResponseEntity.ok(responseMap);

	}

}
