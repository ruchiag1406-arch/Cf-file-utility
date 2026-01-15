package com.cf.files.utility.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.WordUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import com.cf.files.utility.model.PdfCord;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class PdfUtility {
	
	public static int x=0;
	public static int y=0;
	public static int page=1;
	
	public static void createSignedPdf(String src, String dest, String signData, String signName, String signDate, String comments) throws Exception {
		
		System.out.println("Signing the pdf");
		
		final PdfCord pdfCord = new PdfCord();
		
		//System.out.println("Src is " + src);
		PdfReader reader = new PdfReader(src);
		
		int pNumbers = reader.getNumberOfPages();
		
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		//System.out.println("PArset is " + parser);
		final List <TextRenderInfo> textRenderInfos = new ArrayList<TextRenderInfo>();
		
		for (int i=1; i<=pNumbers; i++) {
			//System.out.println("i is " + i);
			final int other = i;
			parser.processContent(i, new TextMarginFinder() {
				
				@Override
			     public void renderText(TextRenderInfo renderInfo) {
					
			       
			         super.renderText(renderInfo);
			         
			         String text = renderInfo.getText();
			         //System.out.println("Text - " +other + " - " + renderInfo.getText());
			         
			         if ( text!=null ) {
			        	 
			        	 if ( text.contains("Authorized Signature") ) {
			        		 
			        		 textRenderInfos.add(renderInfo);
			                 System.out.println("createSignedPdf "
			                 		+ "Text " + renderInfo.getText());
//			                 System.out.println(renderInfo.getBaseline().getStartPoint().get(Vector.I1));
//			                 System.out.println(renderInfo.getBaseline().getEndPoint().get(Vector.I2));
//			                 System.out.println(renderInfo.getBaseline().getEndPoint().get(Vector.I3));
			                 
			                 PdfUtility.page = other;
			        		 
			        	 } else if (text.contains("\\n1\\")) {
				        	 
				        	 System.out.println("Yes N" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setxName((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setyName((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 
				         } else if (text.contains("\\d1\\")) {
				        	 
				        	 System.out.println("Yes D" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setxDate((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setyDate((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 
				         } else if (text.contains("\\s1\\")) {
				        	 
				        	 System.out.println("Yes S" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setxSign((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setySign((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 PdfUtility.page = other;
				         } else if (text.contains("\\c1\\")) {
				        	 
				        	 System.out.println("Yes C" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setxComm((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setyComm((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 PdfUtility.page = other;
				         }
			        	
			         } 
			         
			     }
			}); 
			//parser.processContent(i, new CustomTextMarginFinder(textRenderInfos, i) ); 
		}
		
		Rectangle mediabox = reader.getPageSize(page);
		
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
		/* PdfContentByte content = stamper.getOverContent(reader.getNumberOfPages()); */
		
		if ( signData!=null ) {
			
			// data:[<MIME-type>][;charset=<encoding>][;base64],<data>
	        String theSource = signData.replace("data:image/png;base64,", "");
	        
	        Image image = Image.getInstance(Base64.decode(theSource));
			
	        //Image image = Image.getInstance(IMG_SRC);
	        System.out.println("Y33 is " +  image.getHeight() );
	        System.out.println("Y33 is " +  image.getScaledHeight() );
	        image.scalePercent(12);
	        System.out.println("Y11 is " + pdfCord.getySign());
	        System.out.println("Y22 is " +  image.getScaledHeight());
	        
	        image.setAbsolutePosition(pdfCord.getxSign(), ( pdfCord.getySign()-(image.getScaledHeight()/2) ) + ( (int)(0.08*image.getScaledHeight())));
	        //writer.getDirectContent().addImage(image);
	        
			/*AcroFields fields = reader.getAcroFields();
			System.out.println("Fields " + fields.getSignatureNames().size());
		    for(String signame : fields.getBlankSignatureNames()) {
		      List<AcroFields.FieldPosition> positions = fields.getFieldPositions(signame);
		      Rectangle rect = positions.get(0).position; // In points:
		      float left   = rect.getLeft();
		      float bTop   = rect.getTop();
		      float width  = rect.getWidth();
		      float height = rect.getHeight();

		      int page = positions.get(0).page;
		      Rectangle pageSize = reader.getPageSize(page);
		      float pageHeight = pageSize.getTop();
		      float top = pageHeight - bTop;

		      System.out.print(signame + "::" + page + "::" + left + "::" + top + "::" + width + "::" + height + "\n");
		    }*/
			
			
			/* PRStream stream;
			for (int i = 1; i <= pNumbers; i++) {
				PdfDictionary dict = reader.getPageN(i);
				PdfObject object = dict.getDirectObject(PdfName.CONTENTS);
				if (object instanceof PRStream) {
					stream = (PRStream) object;
					byte[] data = PdfReader.getStreamBytes(stream);
					String dd = new String(data);
					dd = dd.replaceAll("Autorized Signature", "old_text");
					stream.setData(dd.getBytes());
				}
			}*/
	        
	        PdfContentByte over = stamper.getOverContent(page);
	        over.addImage(image);
			
		}
		
		
		
        
//        PdfImage stream = new PdfImage(image, "", null);
//        stream.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
//        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
//        image.setDirectReference(ref.getIndirectReference());
		
       
        
        
        PdfContentByte cb = stamper.getOverContent(page);
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.saveState();
        cb.beginText();
        cb.moveText(pdfCord.getxName(), ( pdfCord.getyName()-((int)(0.01*mediabox.getHeight()))) );
        cb.setFontAndSize(bf, 12);
        cb.showText(signName);
        cb.endText();
        cb.restoreState();
		
        
        cb.saveState();
        cb.beginText();
        cb.moveText(pdfCord.getxDate(), (pdfCord.getyDate()-((int)(0.01*mediabox.getHeight()))) );
        cb.setFontAndSize(bf, 12);
        cb.showText(signDate);
        cb.endText();
        cb.restoreState();
        
        if ( comments.length() <= 100 ) {
        	System.out.println("Comments " + pdfCord.getxComm());
        	cb.saveState();
            cb.beginText();
            cb.moveText(pdfCord.getxComm(), (pdfCord.getyComm()-((int)(0.01*mediabox.getHeight()))) );
            cb.setFontAndSize(bf, 12);
            cb.showText(comments);
            cb.endText();
            cb.restoreState();
        	
        } else {
        	
        	int y = (pdfCord.getyComm()-((int)(0.01*mediabox.getHeight())));
//        	int y = (pdfCord.getyComm()+((int)(0.02*mediabox.getHeight())));
//        	int y = pdfCord.getyComm();
        	
        	comments = WordUtils.wrap(comments, 90);
        	String [] comms = comments.split(System.lineSeparator());
        	y = y+(10*(comms.length-1));
        	for ( int i=0; i<comms.length; i++) {
        		System.out.println("Comments " +  i + " is " + comms[i]);
        		cb.saveState();
                cb.beginText();
                cb.moveText(pdfCord.getxComm(),  y - (i*10) );
                cb.setFontAndSize(bf, 10);
                cb.showText(comms[i]);
                cb.endText();
                cb.restoreState();
        	}
        	
        }
        
        
		
		stamper.close();
		reader.close();
		
	}
	
	public static PdfCord applyCoord(String src) throws Exception {
		
		final PdfCord pdfCord = new PdfCord();
		
		//System.out.println("Src is " + src);
		PdfReader reader = new PdfReader(src);
		
		int pNumbers = reader.getNumberOfPages();
		System.out.println("pNumbers is " + pNumbers);
        
		
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		
		
		
		
		final List <TextRenderInfo> textRenderInfos = new ArrayList<TextRenderInfo>();
       
		for (int i=1; i<=pNumbers; i++) {
			//System.out.println("i is " + i);
			final int other = i;
			parser.processContent(i, new TextMarginFinder() {
				
				@Override
			     public void renderText(TextRenderInfo renderInfo) {
					
			         super.renderText(renderInfo);
			         String text = renderInfo.getText();
			         //System.out.println("Text - " +other + " - " + renderInfo.getText());
			         
			         if ( text!=null ) {
			        	 
			        	 if ( text.contains("Authorized Signature") ) {
			        		 
			        		 textRenderInfos.add(renderInfo);
			                 System.out.println("createSignedPdf "
			                 		+ "Text " + renderInfo.getText());
//			                 System.out.println(renderInfo.getBaseline().getStartPoint().get(Vector.I1));
//			                 System.out.println(renderInfo.getBaseline().getEndPoint().get(Vector.I2));
//			                 System.out.println(renderInfo.getBaseline().getEndPoint().get(Vector.I3));
			                 
			                 PdfUtility.page = other;
			        		 
			        	 } else if (text.contains("\\n1\\")) {
				        	 
				        	 System.out.println("Yes N" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setpName(other-1);
				        	 pdfCord.setxName((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setyName((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 
				         } else if (text.contains("\\d1\\")) {
				        	 
				        	 System.out.println("Yes D" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setpDate(other-1);
				        	 pdfCord.setxDate((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setyDate((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 
				         } else if (text.contains("\\s1\\")) {
				        	 
				        	 System.out.println("Yes S" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setpSign(other-1);
				        	 pdfCord.setxSign((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setySign((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 PdfUtility.page = other;
				         } else if (text.contains("\\c1\\")) {
				        	 
				        	 System.out.println("Yes C" + text);
				        	 pdfCord.setPage(other);
				        	 pdfCord.setpComm(other-1);
				        	 pdfCord.setxComm((int)renderInfo.getBaseline().getBoundingRectange().x);
				        	 pdfCord.setyComm((int)renderInfo.getBaseline().getBoundingRectange().y);
				        	 PdfUtility.page = other;
				         }
			        	
			         } 
			         
			     }
			}); 
			//parser.processContent(i, new CustomTextMarginFinder(textRenderInfos, i) ); 
		}
		
		
		Rectangle mediabox = reader.getPageSize(page);
		x = (int)((pdfCord.getxName()/mediabox.getWidth()) *100)+1;																																											
	    y = (int)(((mediabox.getHeight()-pdfCord.getyName())/mediabox.getHeight()) *100)+2;
	    pdfCord.setxName( (int)((pdfCord.getxName()/mediabox.getWidth()) *100)+2 );																																											
	    pdfCord.setyName( (int)(((mediabox.getHeight()-pdfCord.getyName()-5)/mediabox.getHeight()) *100)-0 );
	    pdfCord.setxDate( (int)((pdfCord.getxDate()/mediabox.getWidth()) *100)+2 );																																											
	    pdfCord.setyDate( (int)(((mediabox.getHeight()-pdfCord.getyDate()-5)/mediabox.getHeight()) *100)-0 );
	    pdfCord.setxSign( (int)((pdfCord.getxSign()/mediabox.getWidth()) *100)+2 );																																											
	    pdfCord.setySign( (int)(((mediabox.getHeight()-pdfCord.getySign()-4)/mediabox.getHeight()) *100)-0 );
	    pdfCord.setxComm( (int)((pdfCord.getxComm()/mediabox.getWidth()) *100)+2 );																																											
	    pdfCord.setyComm( (int)(((mediabox.getHeight()-pdfCord.getyComm()-4)/mediabox.getHeight()) *100)-0 );
	    
	        System.out.println("Coordinates " + x + " - " + y );
		/* System.out.println("Paset is " + mediabox.getWidth() + " - " + mediabox.getHeight() );
		
        System.out.println("Text " + textRenderInfos.get(0).getText());
        
        System.out.println("X Rect is " + textRenderInfos.get(0).getBaseline().getBoundingRectange().x);
        System.out.println("Y Rect is " + textRenderInfos.get(0).getBaseline().getBoundingRectange().y);
        System.out.println("X Vec is " + textRenderInfos.get(0).getBaseline().getStartPoint().get(Vector.I1));
        System.out.println("Y Vec is " + textRenderInfos.get(0).getBaseline().getEndPoint().get(Vector.I2));
        
        System.out.println("Y Vec is " + textRenderInfos.get(0).getDescentLine().getStartPoint().I2);
        
        
        System.out.println("Total X " + mediabox.getWidth());
        System.out.println("Total Y " + mediabox.getHeight());
        x = (int)((textRenderInfos.get(0).getBaseline().getBoundingRectange().x/mediabox.getWidth()) *100)+1;																																											
        y = (int)(((mediabox.getHeight()-textRenderInfos.get(0).getBaseline().getBoundingRectange().y)/mediabox.getHeight()) *100)+2;
        System.out.println("Coordinates " + x + " - " + y ); */
		
		reader.close();
		
		return pdfCord;
		
	}
	
	public static void saveFileonSF(String instanceUrl, String sessId, String contId, String fileStr) throws Exception {
		
		File body = new File(fileStr);
		String restURI = instanceUrl + "/services/data/v43.0/sobjects/ContentVersion/";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(restURI);
		uploadFile.setHeader("Authorization", "Bearer " + sessId);
		

		if ( body.length() < (20 * 1000000 ) ) {

			
			// uploadFile.addHeader("content-type", "multipart/form-data");

			JSONObject attachment = new JSONObject();
			attachment.put("ContentDocumentId", contId);
			attachment.put("PathOnClient", body.getName()); // .pdf
			//attachment.put("ContentType", "application/pdf"); // application/

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			// builder.setBoundary("19dj0d239d23d");
			builder.addTextBody("entity_content", attachment.toString(), ContentType.APPLICATION_JSON);

			

//          This attaches the file to the POST:
			byte[] array = Files.readAllBytes(body.toPath());
			builder.addBinaryBody("VersionData", array, ContentType.MULTIPART_FORM_DATA, body.getName());

			HttpEntity multipart = builder.build();

			uploadFile.setEntity(multipart);

			CloseableHttpResponse response = httpClient.execute(uploadFile);
			StringWriter writer = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), writer, StandardCharsets.UTF_8);
			
			
			//JSONObject jsonObject = new JSONObject(writer.toString());
			System.out.println("Upload code 1" + writer.toString() );


			System.out.println("Upload code " + response.getStatusLine().getStatusCode()  );

		} 
		
	}

}