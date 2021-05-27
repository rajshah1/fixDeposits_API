/**
 * 
 */
package systems.rajshah.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import HeaderFooterPageEvent.HeaderFooterUtils;
import systems.rajshah.model.InvestorInfo;
import systems.rajshah.model.QueryObjectDetails;
import systems.rajshah.model.ReportGenObject;
import systems.rajshah.model.UserInfo;

/**
 * @author RAJ SHAH
 *
 */
@Service
public class FirebaseUserOperation implements IFirebaseUserOperartions {
	@Autowired
	private Firestore firestore;

	@Autowired
	private FirebaseAuth firebaseAuth;

	@Autowired
	IfirebaseUser firebaseUserImpl;

	// Create a Class Object
	// FirebaseUserImpl firebaseUserImplObject =new FirebaseUserImpl();

	private static final Logger logger = LoggerFactory.getLogger(FirebaseUserOperation.class);

	@Override
	public ByteArrayInputStream generateManagementReport(QueryObjectDetails queyObject, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		// TODO Auto-generated method stub
		logger.debug("In Generate Managment Report Method");
		// Create New Document
		Document doc = new Document(PageSize.A4, 10, 10, 5, 5);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		UserInfo uInfo = firebaseUserImpl.getCurrentUserDetails(currentUid);
		PdfWriter pdfWrite = PdfWriter.getInstance(doc, bout);

		doc.open();

		HeaderFooterUtils headerEvent = new HeaderFooterUtils();
		Calendar cal = Calendar.getInstance();
		cal.setTime(queyObject.getInitialDate());
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(queyObject.getLastDate());

		String dateformatorfirst = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.YEAR);
		String dateformatorlast = cal1.get(Calendar.DAY_OF_MONTH) + "/" + (cal1.get(Calendar.MONTH) + 1) + "/"
				+ cal1.get(Calendar.YEAR);
		headerEvent.onStartPage(pdfWrite, doc, dateformatorfirst, dateformatorlast);
		pdfWrite.setPageEvent(headerEvent);
		FirebaseUserImpl.addMetadata(doc, uInfo.getName());

		// Generate the Data to add to table
		Map<String, ReportGenObject> reportMetaData = firebaseUserImpl.getReportJSONService(queyObject, currentUid);
		// Add Data to tables
		if (reportMetaData.size() == 1 && reportMetaData.containsKey("NO Inputs")) {
			doc.add(new Chunk(" \n \n No Data Found For Given Dates . Please Try other Dates"));
			doc.close();
			return new ByteArrayInputStream(bout.toByteArray());
		} else {
			reportMetaData.values().stream().forEach(e -> {

				try {

					DottedLineSeparator line = new DottedLineSeparator();

					Paragraph p1 = new Paragraph();
					p1.add(new Chunk(
							"        Family Code : " + e.getFamilyCode() + "------ Family Head : " + e.getFamilyHeadName()));

					doc.add(line);
					doc.add(p1);
					PdfPTable table = new PdfPTable(6);
					table.setWidthPercentage(90);
					table.setSpacingBefore(8f);
					table.setHeaderRows(1);

					float[] columnWidths = { 1f, 3f, 4f, 3f, 3f, 4.5f };
					table.setWidths(columnWidths);
					PdfPTable table1 = FirebaseUserImpl.tablegenerator(table);
					table1.setHeaderRows(1);
					FirebaseUserImpl.persistJSONMetadataToTable(doc, table1, e.getFdInfo());

					doc.add(line);
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					logger.error("DocumentException Occurred : {}", e1);
				}

			});

			doc.close();
		}
		return new ByteArrayInputStream(bout.toByteArray());
	}

	@Override
	public ByteArrayInputStream generateFullReportByFamilyCode(String familyCode, String currentUid)
			throws FirebaseAuthException, InterruptedException, ExecutionException, DocumentException {
		// TODO Auto-generated method stub

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, 50);

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(new Date());
		cal1.add(Calendar.YEAR, -50);

		QueryObjectDetails queryObj = new QueryObjectDetails();
		queryObj.setInitialDate(cal1.getTime());
		queryObj.setLastDate(cal.getTime());
		queryObj.setSearchField("startDate");

		// logger.debug("In Geerate fullstatByClientID "+queryObj.toString());
		ByteArrayInputStream fullInfoResp = firebaseUserImpl.generateCustomerIntimationReport(familyCode, queryObj,
				currentUid);

		return fullInfoResp;
	}

	@Override
	public List<InvestorInfo> getUserInfoFromFamilyCode(String currentUid, String id) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return firestore.collection(currentUid).whereEqualTo("familyCode", id).get().get().toObjects(InvestorInfo.class);
	}

}
