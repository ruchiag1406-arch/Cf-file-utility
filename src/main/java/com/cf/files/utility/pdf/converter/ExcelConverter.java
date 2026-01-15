package com.cf.files.utility.pdf.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


/**
 * @author raghav.simlote
 *
 */
public class ExcelConverter extends PDFConverter {

	private File xlsFile;
	private String fileExtension;
	private Workbook workbook;
	
	public ExcelConverter(File xlsFile, File pdfFile, String fileExtension) {
		super(pdfFile);
		this.xlsFile = xlsFile;
		this.fileExtension = fileExtension;
	}

	@Override
	public void convertToPdf() {

		try {

			workbook = loadSpreadSheet(xlsFile);

			readSpreadSheet(workbook);

		} catch (FileNotFoundException e) {

			System.err.println("Excel File (or) PDF File is already opened. Please close the file");

			System.exit(1);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	private Workbook loadSpreadSheet(File xlsFile) throws Exception {

		Workbook workbook = null;
		
		String ext = fileExtension!=null ? fileExtension : getFileExtension(xlsFile.getName());

		if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("excel_x")) {

			OPCPackage pkg = OPCPackage.open(xlsFile.getAbsolutePath());

			workbook = new XSSFWorkbook(pkg);

			pkg.close();

		} else if (ext.equalsIgnoreCase("xls")|| ext.equalsIgnoreCase("excel")) {

			InputStream xlsFIS = new FileInputStream(xlsFile);

			workbook = new HSSFWorkbook(xlsFIS);

			xlsFIS.close();

		} else {
			
			OPCPackage pkg = OPCPackage.open(xlsFile.getAbsolutePath());

			workbook = new XSSFWorkbook(pkg);

			pkg.close();
			
//			throw new Exception("FILE EXTENSION NOT RECOGNIZED");

		}

//		FileInputStream inputStream = new FileInputStream(xlsFile);
//		workbook = WorkbookFactory.create(inputStream);
		
		return workbook;

	}

	private String getFileExtension(String fileName) {

		String ext = "";

		int mid = fileName.lastIndexOf(".");

		ext = fileName.substring(mid + 1, fileName.length());

		System.out.println("File Extension --" + ext);

		return ext;

	}

	private void readSpreadSheet(Workbook workbook) throws IOException,

			DocumentException {

		Document document = new Document();

		PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

		document.open();

		addMetaData(document);

		addTitlePage(document);

		Font catFont = new Font();
		Anchor anchor = new Anchor("First Chapter", catFont );

		anchor.setName("First Chapter");

		// Second parameter is the number of the chapter

		Chapter catPart = new Chapter(new Paragraph(anchor), 1);

		Font subFont = new Font();
		Paragraph subPara = new Paragraph("Table", subFont);

		Section subCatPart = catPart.addSection(subPara);

		addEmptyLine(subPara, 5);

		Sheet sheet = workbook.getSheetAt(0);

		// Iterate through each rows from first sheet

		Iterator<Row> rowIterator = sheet.iterator();

		int temp = 0;

		boolean flag = true;

		PdfPTable table = null;

		while (rowIterator.hasNext()) {

			Row row = rowIterator.next();

			int cellNumber = 0;

			if (flag) {

				table = new PdfPTable(row.getLastCellNum());

				flag = false;

			}

			// For each row, iterate through each columns

			Iterator<Cell> cellIterator = row.cellIterator();

			int numberOfColumns = 0;
			while (cellIterator.hasNext()) {

				Cell cell = cellIterator.next();

				switch (cell.getCellType()) {

				case Cell.CELL_TYPE_STRING:

					if (temp == 0) {

						numberOfColumns  = row.getLastCellNum();

						PdfPCell c1 = new PdfPCell(new Phrase(

								cell.getStringCellValue()));

						c1.setHorizontalAlignment(Element.ALIGN_CENTER);

						table.addCell(c1);

						table.setHeaderRows(1);

					} else {

						cellNumber = checkEmptyCellAndAddCellContentToPDFTable(cellNumber, cell, table);

					}

					cellNumber++;

					break;

				case Cell.CELL_TYPE_NUMERIC:

					cellNumber = checkEmptyCellAndAddCellContentToPDFTable(cellNumber, cell, table);

					cellNumber++;

					break;
				
				default:
					break;

				}

			}

			temp = 1;

			if (numberOfColumns != cellNumber) {

				for (int i = 0; i < (numberOfColumns - cellNumber); i++) {

					table.addCell(" ");

				}

			}

		}

		subCatPart.add(table);

		// Now add all this to the document

		document.add(catPart);

		document.close();

	}

	private void addMetaData(Document document) {

		document.addTitle("My first PDF");

		document.addSubject("Using iText");

		document.addKeywords("Java, PDF, iText");

		document.addAuthor("Uvaraj");

		document.addCreator("Uvaraj");

	}

	private void addTitlePage(Document document)

			throws DocumentException {

		Paragraph preface = new Paragraph();

		// We add one empty line

		addEmptyLine(preface, 1);

		// Lets write a big header

		Font catFont = new Font();
		preface.add(new Paragraph("Title of the document", catFont ));

		addEmptyLine(preface, 1);

		// Will create: Report generated by: _name, _date

		Font smallBold= new Font();
		preface.add(new Paragraph("Report generated by: " + "Uvaraj" + ", "

				+ new Date(), smallBold));

		addEmptyLine(preface, 3);

		preface.add(new Paragraph(

				"This document describes something which is very important ",

				smallBold));

		addEmptyLine(preface, 8);

		Font redFont = new Font();
		preface.add(new Paragraph(

				"This document is a preliminary version  ;-).", redFont ));

		document.add(preface);

		// Start a new page

		document.newPage();

	}

	private void addEmptyLine(Paragraph paragraph, int number) {

		for (int i = 0; i < number; i++) {

			paragraph.add(new Paragraph(" "));

		}

	}
	
	private int checkEmptyCellAndAddCellContentToPDFTable(int cellNumber, Cell cell, PdfPTable table) {

        if (cellNumber == cell.getColumnIndex()) {
        	
        	switch (cell.getCellType()) {
        	
				case Cell.CELL_TYPE_NUMERIC:
					table.addCell(Double.toString(cell.getNumericCellValue()));
					break;
				case Cell.CELL_TYPE_STRING:
					table.addCell(cell.getStringCellValue());
					break;
				default:
					break;
        			
        	}


        } else {

                    while( cellNumber < cell.getColumnIndex()) {

                                 

                                            table.addCell(" ");

                                            cellNumber++;

                               

                    }

                    if (cellNumber == cell.getColumnIndex()) {
                    	
                    	switch (cell.getCellType()) {
                    	
                    		case Cell.CELL_TYPE_NUMERIC:
	        					table.addCell(Double.toString(cell.getNumericCellValue()));
	        					break;
                    		case Cell.CELL_TYPE_STRING:
	        					 table.addCell(cell.getStringCellValue());
	        					break;
	        				default:
	        					break;
                			
                    	}

                    }

                    cellNumber = cell.getColumnIndex();

        }         

       

		return cellNumber;

	}

}
