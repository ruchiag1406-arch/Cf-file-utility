package com.cf.files.utility.pdf.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author raghav.simlote
 *
 */
public class DefaultConverter extends PDFConverter {
	
	private File inputFile;

	public DefaultConverter(File inputFile, File pdfFile) {
		super(pdfFile);
		this.inputFile = inputFile;
	}

	@Override
	public void convertToPdf() {
		
		PdfWriter writer = null;
		Document document = null;
		String tempText=null; 
		
		try {

			document = new Document();
			FileOutputStream fosK = new FileOutputStream(pdfFile);
			writer = PdfWriter.getInstance(document, fosK);
			
			writer.open();
			document.open();
			
			document.add(new Paragraph(org.apache.commons.io.FileUtils.readFileToString(inputFile,tempText))); 
			
		} catch (FileNotFoundException e) {

			System.err.println("Input File (or) PDF File is already opened. Please close the file");

			System.exit(1);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			
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
