package com.cf.files.utility.pdf.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author raghav.simlote
 *
 */
public class ImageConverter extends PDFConverter {
	
	private File imageFile;
	
	public ImageConverter(File imageFile, File pdfFile) {
		super(pdfFile);
		this.imageFile = imageFile;
	}

	@Override
	public void convertToPdf() {

		Document document = null;
		FileInputStream fosTarg = null;
		PdfWriter writer = null;
		
		try {
			
			document = new Document();
			FileOutputStream fosK = new FileOutputStream(pdfFile);
			writer = PdfWriter.getInstance(document, fosK);
			writer.open();
		    document.open();
		    
		    fosTarg = new FileInputStream(imageFile);
			byte[] bytes = IOUtils.toByteArray(fosTarg);
		    Image imageF = Image.getInstance(bytes);
			
//			Image Scaling to fit the size of Paper.
			imageF.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			imageF.setAbsolutePosition(0, imageF.getAbsoluteY());
			imageF.setBorderWidth(0);
			
		    document.add( imageF );
		    
		   
		} catch (FileNotFoundException e) {

			System.err.println("Image File (or) PDF File is already opened. Please close the file");

			System.exit(1);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			
			try {
				fosTarg.close();
			}catch (Exception e) {
				
			}
			
			try {
				 document.close();
			}catch (Exception e) {
				
			}
			
			try {
				 writer.close();
			}catch (Exception e) {
				
			}
			
			
		}
		
		
	}

}