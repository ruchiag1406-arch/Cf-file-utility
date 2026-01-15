package com.cf.files.utility.pdf.converter;

import java.io.File;

/**
 * @author raghav.simlote
 * 
 * Base Class for All Converters. Each File converter needs to extends this class.
 *
 */
public abstract class PDFConverter {
	
	/**
	 * Converted PDF file reference.
	 */
	protected File pdfFile;
	
	protected PDFConverter(File pdfFile) {
		super();
		this.pdfFile = pdfFile;
	}

	/**
	 * Method to be implemented by each Converter.
	 */
	public abstract void convertToPdf();
	
}
