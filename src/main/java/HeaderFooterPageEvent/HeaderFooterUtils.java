package HeaderFooterPageEvent;

import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterUtils extends PdfPageEventHelper {

	public void onStartPage(PdfWriter writer,Document document,String dateformatorfirst, String dateformatorlast) throws DocumentException {
    	//com.itextpdf.text.Rectangle rect = writer.getBoxSize("art");
        //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Chunk(
		//		"\n MANAGMENT REPORT FOR PERIOD :  " + dateformatorfirst + " - " + dateformatorlast + "\n \n"), rect.getLeft(), rect.getTop(), 0);
        
		
		document.add(new Paragraph(new Chunk("\n MANAGMENT REPORT FOR PERIOD :  " + dateformatorfirst + " - " + dateformatorlast + "\n \n")));
		
    }

	/*
	 * public void onEndPage(PdfWriter writer, Document document) {
	 * com.itextpdf.text.Rectangle rect = writer.getBoxSize("art");
	 * ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
	 * new Phrase("Bottom Left"), rect.getLeft(), rect.getBottom(), 0);
	 * ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
	 * new Phrase("Bottom Right"), rect.getRight(), rect.getBottom(), 0); }
	 */
}
