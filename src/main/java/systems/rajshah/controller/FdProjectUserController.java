package systems.rajshah.controller;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.itextpdf.text.DocumentException;

import systems.rajshah.service.IFirebaseUserOperartions;

@RestController
public class FdProjectUserController {
	@Autowired(required = false)
	IFirebaseUserOperartions ifirebaseusersOp;

	@GetMapping(value = "/{currentUid}/fullstatByClientID/{id}")
	public ResponseEntity<InputStreamResource> fullstatByClientID(@PathVariable("id") String idvarable,
			@PathVariable("currentUid") String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		//System.out.println("In fullstatByClientID"+currentUid);
		ByteArrayInputStream bis = ifirebaseusersOp.generateFullReportByFamilyCode(idvarable, currentUid);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=" + idvarable + ".pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}
}
