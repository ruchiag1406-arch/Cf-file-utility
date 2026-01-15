package com.cf.files.utility.pdf.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.jsoup.Jsoup;

import com.cf.files.utility.helper.MimeMessageParser;
import com.cf.files.utility.helper.MimeObjectEntry;
import com.cf.files.utility.helper.StringReplacer;
import com.cf.files.utility.helper.StringReplacerCallback;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.html.HtmlEscapers;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class EmlConvertor extends PDFConverter {

	private File emlFile;
	private boolean showHeader;

	// html wrapper template for text/plain messages
	private static final String HTML_WRAPPER_TEMPLATE = "<!DOCTYPE html><html><head><style>body{font-size: 0.5cm;}</style><meta charset=\"%s\"><title>title</title></head><body>%s</body></html>";
	private static final String ADD_HEADER_IFRAME_JS_TAG_TEMPLATE = "<script id=\"header-v6a8oxpf48xfzy0rhjra\" data-file=\"%s\" type=\"text/javascript\">%s</script>";
	private static final String HEADER_FIELD_TEMPLATE = "<tr><td class=\"header-name\">%s</td><td class=\"header-value\">%s</td></tr>";

	private static final Pattern IMG_CID_REGEX = Pattern.compile("cid:(.*?)\"", Pattern.DOTALL);
	private static final Pattern IMG_CID_PLAIN_REGEX = Pattern.compile("\\[cid:(.*?)\\]", Pattern.DOTALL);

	public EmlConvertor(File emlFile, File pdfFile) {
		super(pdfFile);
		this.emlFile = emlFile;
		this.showHeader = false;

		System.setProperty("mail.mime.address.strict", "false");
		System.setProperty("mail.mime.decodetext.strict", "false");
		System.setProperty("mail.mime.decodefilename", "true");
		System.setProperty("mail.mime.decodeparameters", "true");
		System.setProperty("mail.mime.multipart.ignoremissingendboundary", "true");
		System.setProperty("mail.mime.multipart.ignoremissingboundaryparameter", "true");

		System.setProperty("mail.mime.parameters.strict", "false");
		System.setProperty("mail.mime.applefilenames", "true");
		System.setProperty("mail.mime.ignoreunknownencoding", "true");
		System.setProperty("mail.mime.uudecode.ignoremissingbeginend", "true");
		System.setProperty("mail.mime.multipart.allowempty", "true");
		System.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");

		System.setProperty("mail.mime.base64.ignoreerrors", "true");

		// set own cleaner class to handle broken contentTypes
		System.setProperty("mail.mime.contenttypehandler", "mimeparser.ContentTypeCleaner");
	}

	@Override
	public void convertToPdf() {
		
		Document document = null;
		FileInputStream fosTarg = null;
		PdfWriter writer = null;

		try {
			
			
		    
		    fosTarg = new FileInputStream(emlFile);
		    
			final MimeMessage message = new MimeMessage(null, fosTarg);

			String subject = message.getSubject();

			String from = message.getHeader("From", null);
			if (from == null) {
				from = message.getHeader("Sender", null);
			}

			from = MimeUtility.decodeText(MimeUtility.unfold(from));

			String[] recipients = new String[0];
			String recipientsRaw = message.getHeader("To", null);
			if (!Strings.isNullOrEmpty(recipientsRaw)) {
				try {
					recipientsRaw = MimeUtility.unfold(recipientsRaw);
					recipients = recipientsRaw.split(",");
					for (int i = 0; i < recipients.length; i++) {
						recipients[i] = MimeUtility.decodeText(recipients[i]);
					}
				} catch (Exception e) {
					// ignore this error
				}
			}

			String sentDateStr = message.getHeader("date", null);

			MimeObjectEntry<String> bodyEntry = MimeMessageParser.findBodyPart(message);
			String charsetName = bodyEntry.getContentType().getParameter("charset");

//			System.out.println("Extract the inline images");
			final HashMap<String, MimeObjectEntry<String>> inlineImageMap = MimeMessageParser
					.getInlineImageMap(message);

			/* ######### Embed images in the html ######### */
			String htmlBody = bodyEntry.getEntry();
			if (bodyEntry.getContentType().match("text/html")) {
				if (inlineImageMap.size() > 0) {
//					System.out.println("Embed the referenced images (cid) using <img src=\"data:image ...> syntax");

					// find embedded images and embed them in html using <img
					// src="data:image ...> syntax
					htmlBody = StringReplacer.replace(htmlBody, IMG_CID_REGEX, new StringReplacerCallback() {
						public String replace(Matcher m) throws Exception {
							MimeObjectEntry<String> base64Entry = inlineImageMap.get("<" + m.group(1) + ">");

							// found no image for this cid, just return the
							// matches string as it is
							if (base64Entry == null) {
								return m.group();
							}

							return "data:" + base64Entry.getContentType().getBaseType() + ";base64,"
									+ base64Entry.getEntry() + "\"";
						}
					});
				}
			} else {
//				System.out.println("No html message body could be found, fall back to text/plain and embed it into a html document");

				htmlBody = "<div style=\"white-space: pre-wrap\">" + htmlBody.replace("\n", "<br>").replace("\r", "")
						+ "</div>";

				htmlBody = String.format(HTML_WRAPPER_TEMPLATE, charsetName, htmlBody);
				if (inlineImageMap.size() > 0) {
//					System.out.println("Embed the referenced images (cid) using <img src=\"data:image ...> syntax");

					// find embedded images and embed them in html using <img
					// src="data:image ...> syntax
					htmlBody = StringReplacer.replace(htmlBody, IMG_CID_PLAIN_REGEX, new StringReplacerCallback() {
						public String replace(Matcher m) throws Exception {
							MimeObjectEntry<String> base64Entry = inlineImageMap.get("<" + m.group(1) + ">");

							// found no image for this cid, just return the
							// matches string
							if (base64Entry == null) {
								return m.group();
							}

							return "<img src=\"data:" + base64Entry.getContentType().getBaseType() + ";base64,"
									+ base64Entry.getEntry() + "\" />";
						}
					});
				}
			}

			System.out.println("Successfully parsed the .eml and converted it into html:");

//			System.out.println("---------------Result-------------");
//			System.out.println("Subject: " + subject);
//			System.out.println("From: " + from);
			if (recipients.length > 0) {
//				System.out.println("To: " + Joiner.on(", ").join(recipients));
			}
//			System.out.println("Date: " + sentDateStr);
			String bodyExcerpt = htmlBody.replace("\n", "").replace("\r", "");
			if (bodyExcerpt.length() >= 60) {
				bodyExcerpt = bodyExcerpt.substring(0, 40) + " [...] "
						+ bodyExcerpt.substring(bodyExcerpt.length() - 20, bodyExcerpt.length());
			}
//			System.out.println("Body (excerpt): " + bodyExcerpt);
//			System.out.println("----------------------------------");

			System.out.println("Start conversion to pdf");

			File tmpHtmlHeader = null;
			if (showHeader) {
				tmpHtmlHeader = File.createTempFile("emailtopdf", ".html");
				String tmpHtmlHeaderStr = Resources.toString(Resources.getResource("header.html"),
						StandardCharsets.UTF_8);
				String headers = "";

				if (!Strings.isNullOrEmpty(from)) {
					headers += String.format(HEADER_FIELD_TEMPLATE, "From", HtmlEscapers.htmlEscaper().escape(from));
				}

				if (!Strings.isNullOrEmpty(subject)) {
					headers += String.format(HEADER_FIELD_TEMPLATE, "Subject",
							"<b>" + HtmlEscapers.htmlEscaper().escape(subject) + "<b>");
				}

				if (recipients.length > 0) {
					headers += String.format(HEADER_FIELD_TEMPLATE, "To",
							HtmlEscapers.htmlEscaper().escape(Joiner.on(", ").join(recipients)));
				}

				if (!Strings.isNullOrEmpty(sentDateStr)) {
					headers += String.format(HEADER_FIELD_TEMPLATE, "Date",
							HtmlEscapers.htmlEscaper().escape(sentDateStr));
				}

				Files.write(String.format(tmpHtmlHeaderStr, headers), tmpHtmlHeader, StandardCharsets.UTF_8);

				// Append this script tag dirty to the bottom
				htmlBody += String.format(ADD_HEADER_IFRAME_JS_TAG_TEMPLATE, tmpHtmlHeader.toURI(),
						Resources.toString(Resources.getResource("contentScript.js"), StandardCharsets.UTF_8));
			}

			// File tmpHtml = File.createTempFile("emailtopdf", ".html");
			/* File tmpHtml = new File("emailtopdf.html");
			System.out.println("Write html to temporary file " + tmpHtml.getAbsolutePath());
			Files.write(htmlBody, tmpHtml, Charset.forName(charsetName)); */
			
			document = new Document();
			FileOutputStream fosK = new FileOutputStream(pdfFile);
			writer = PdfWriter.getInstance(document, fosK);
			writer.open();
		    document.open();

			String htmlData = htmlToXhtml(htmlBody);
//			System.out.println(htmlData);

			// htmlWorker.parse(new StringReader(sb.toString()));
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(htmlData));
			
			System.out.println("End conversion to pdf");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			try {
				fosTarg.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				 document.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				 writer.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}

	}
	
	private static String htmlToXhtml(final String html) {
	    final org.jsoup.nodes.Document document = Jsoup.parse(html);
	    document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
	    document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
	    return document.html();
	}

}
