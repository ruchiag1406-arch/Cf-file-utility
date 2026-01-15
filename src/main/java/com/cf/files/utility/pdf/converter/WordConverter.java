package com.cf.files.utility.pdf.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

/**
 * @author raghav.simlote
 *
 */
public class WordConverter extends PDFConverter {

	private File docFile;
	
	public WordConverter(File docFile, File pdfFile) {
		super(pdfFile);
		this.docFile = docFile;
	}

	@Override
	public void convertToPdf() {
		
		try {

			FileInputStream fosTarg = new FileInputStream(docFile);
			XWPFDocument document= new XWPFDocument(fosTarg);
			
			FileOutputStream fosK = new FileOutputStream(pdfFile);
			PdfOptions options=null;
			PdfConverter.getInstance().convert(document,fosK,options);
			
		} catch (FileNotFoundException e) {

			System.err.println("Doc File (or) PDF File is already opened. Please close the file");

			System.exit(1);

		} catch (Exception e) {

			e.printStackTrace();

		}
		
		
		
	}

}
