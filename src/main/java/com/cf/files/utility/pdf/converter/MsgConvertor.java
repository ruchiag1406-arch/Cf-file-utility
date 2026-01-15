package com.cf.files.utility.pdf.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.jsoup.Jsoup;

import com.auxilii.msgparser.Message;
import com.auxilii.msgparser.MsgParser;
import com.auxilii.msgparser.attachment.Attachment;
import com.auxilii.msgparser.attachment.FileAttachment;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class MsgConvertor extends PDFConverter {

	private File msgFile;

	public MsgConvertor(File msgFile, File pdfFile) {
		super(pdfFile);
		this.msgFile = msgFile;

	}

	private MsgParser msgp = new MsgParser();
	String EMPTY_STRING = "";
	private String fromEmail = EMPTY_STRING;
	private String fromName = EMPTY_STRING;
	private String subject = EMPTY_STRING;
	private String bodyHTML = EMPTY_STRING;
	private String bodyText = EMPTY_STRING;
	private String toEmail = EMPTY_STRING;
	private String toName = EMPTY_STRING;
	private List<Attachment> atts = null;
	private Document document = new Document();

	private void getData(String fileSrc) throws IOException {
		Message msg = msgp.parseMsg(fileSrc);
		fromEmail = msg.getFromEmail();
		fromEmail = fromEmail.contains("Content_Types") ? EMPTY_STRING : fromEmail;
		toEmail = msg.getToEmail();
		toName = msg.getToName();
		bodyText = msg.getBodyText();
		fromName = msg.getFromName();
		subject = msg.getSubject();
		bodyHTML = msg.getConvertedBodyHTML();
		atts = msg.getAttachments();
	}

	private void createPdf(String fileSrc) {

        fileSrc = fileSrc.substring(0, fileSrc.lastIndexOf('.')) + ".pdf";

		try {
			PdfWriter.getInstance(document, new FileOutputStream(fileSrc));
			document.open();
			document.add(new Paragraph(String.format("From: %s [%s]", fromEmail, fromName == null ? "N/A" : fromName)));
			document.add(new Paragraph(MessageFormat.format("To: {0}", toEmail == null ? toName : toEmail)));
			document.add(new Paragraph("Subject: " + subject));
			if (!parseElements(document)) {
				document.add(new Paragraph("Body text: "));
				document.add(new Paragraph(bodyText));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			document.close();
		}
	}

	private boolean parseElements(Document document) {
		boolean ret = true;
		try {
			bodyHTML = htmlToXhtml(bodyHTML);
			ElementList list = XMLWorkerHelper.parseToElementList(bodyHTML, null);
			document.add(new Paragraph("Body HTML: "));
			for (Element element : list) {
				document.add(element);
			}
		} catch (Exception ex) {
			ret = false;
			ex.printStackTrace();

		}

		return ret;
	}

	private void saveAttachments(String fileSrc) throws IOException {
		fileSrc = fileSrc.substring(0, fileSrc.lastIndexOf('.')) + "-att";
		for (int i = 0; i < atts.size(); i++) {
			if (atts.get(i) instanceof FileAttachment) {
				FileAttachment file = (FileAttachment) atts.get(i);
				fileSrc += i + file.getExtension();

				FileOutputStream out = new FileOutputStream(fileSrc);
				out.write(file.getData());
				out.close();
			}
		}
	}
	
	private static String htmlToXhtml(final String html) {
	    final org.jsoup.nodes.Document document = Jsoup.parse(html);
	    document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
	    document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
	    return document.html();
	}

	@Override
	public void convertToPdf() {
		// TODO Auto-generated method stub

//		System.out.println("Path P is " + msgFile.toPath().toString());
		System.out.println("Path Src is " + msgFile.getAbsoluteFile());
		System.out.println("Path Pdf is " + pdfFile.getAbsoluteFile());
		
		/* calling methods of main functions to convert to pdf*/
		try {
			getData(msgFile.getAbsolutePath());
			createPdf(pdfFile.getAbsolutePath());
			saveAttachments(pdfFile.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
