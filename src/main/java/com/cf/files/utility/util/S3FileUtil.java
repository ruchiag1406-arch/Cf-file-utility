package com.cf.files.utility.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cf.files.utility.amazon.AmazonClient;
import com.cf.files.utility.amazon.AmazonUtil;

public class S3FileUtil {
	
	public static void main(String args[] ) {
		try {
			System.out.println("Start");
			copyOldFilesSept22();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readFile() throws Exception {

		// obtaining input bytes from a file
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\ragha\\Downloads\\devlatestAmazonFilespart21.xlsx"));
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		// creating a Sheet object to retrieve the object
		XSSFSheet  sheet = wb.getSheetAt(0);
		// evaluating cell type
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		for (Row row : sheet) // iteration over row using for each loop
		{
			for (Cell cell : row) // iteration over cell using for each loop
			{
				switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
				case Cell.CELL_TYPE_NUMERIC: // field that represents numeric cell type
					// getting the value of the cell as a number
					System.out.print(cell.getNumericCellValue() + "\t\t");
					break;
				case Cell.CELL_TYPE_STRING: // field that represents string cell type
					// getting the value of the cell as a string
					System.out.print(cell.getStringCellValue() + "\t\t");
					break;
				}
			}
			System.out.println();
		}
	}
	
	public static void readFile2() throws Exception {

		// obtaining input bytes from a file
//		String inPath = "C:\\Users\\ragha\\Downloads\\devlatestAmazonFilespart21.xlsx";
		String inPath = "C:\\Users\\ragha\\Downloads\\devlatestAmazonFilesExtract-part1.xlsx";
//		String inPath = "C:\\Users\\ragha\\Downloads\\uatAmazonFileExtract1.xlsx";
		
//		String outPath = "C:\\Users\\ragha\\Downloads\\devlatestAmazonFilespart212.xlsx";
		String outPath = "C:\\Users\\ragha\\Downloads\\devlatestAmazonFilesExtract-part212.xlsx";
//		String outPath = "C:\\Users\\ragha\\Downloads\\uatAmazonFileExtract12.xlsx";
		
		FileInputStream fis = new FileInputStream(new File(inPath));
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		// creating a Sheet object to retrieve the object
		XSSFSheet  sheet = wb.getSheetAt(0);
		// evaluating cell type
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		boolean isFirstRow = true;
//		int colNum = 0;
		for (Row row : sheet) // iteration over row using for each loop
		{
			// skip first row
			if ( isFirstRow ) {
				isFirstRow = false;
				continue;
			}
			
			// start column number
//			colNum=0;
			
			String fileName = null;
			String oldPath=null;
			String oldBuck = null;
			String newPath=null;
			String newBuck = "horsepowerelectric-dev";
//			String newBuck = "horsepowerelectric-uat";
			
			for (Cell cell : row) // iteration over cell using for each loop
			{
				String value = "";
				switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
				case Cell.CELL_TYPE_NUMERIC: // field that represents numeric cell type
					// getting the value of the cell as a number
					//System.out.print(cell.getNumericCellValue() + "\t\t");
					value = "" + cell.getNumericCellValue();
					break;
				case Cell.CELL_TYPE_STRING: // field that represents string cell type
					// getting the value of the cell as a string
					//System.out.print(cell.getStringCellValue() + "\t\t");
					value = cell.getStringCellValue();
					break;
				}
				
				//check column number
				
				//System.out.print(cell.getColumnIndex() + "\t\t");
//				//System.out.print(cell.getRowIndex() + "\t\t");
				
				if ( cell.getColumnIndex() == 2 ) {
					// fileurl
					// file name
					if ( !value.isEmpty() ) {
						int lastDotIdx = value.lastIndexOf('/') + "/".length();
						fileName = value.substring(lastDotIdx, value.length());
						
						int comIdx = value.indexOf(".com/") + ".com/".length();
						oldPath = value.substring(comIdx, value.length());
						
						int s3Idx = value.indexOf(".s3");
						int httpIdx = value.indexOf("https://") + "https://".length() ;
						if ( s3Idx > 0 ) {
							oldBuck = value.substring(httpIdx, s3Idx);
						} else {
							s3Idx = value.indexOf(".s3");
							httpIdx = value.indexOf('/', comIdx+2);
							if ( httpIdx > 0 ) {
								oldBuck = value.substring(comIdx, httpIdx);
								if ( oldBuck.contains("/") ) {
									oldBuck = oldBuck.substring(1);
									oldPath = oldPath.substring(oldBuck.length()+2);
								}
							}
						}
						
						System.out.println("FileName is " + fileName + "\t\t");
						System.out.println("OldPath is " + oldPath + "\t\t");
						System.out.println("OldBuck is " + oldBuck + "\t\t");
					} 
				} else if ( cell.getColumnIndex() == 5 ) {
					// path
					if ( !value.isEmpty() && fileName!=null ) {
						newPath = value + "/" + fileName;
						System.out.println("Newpath is " + newPath + "\t\t");
						System.out.println("NewBuck is " + newBuck + "\t\t");
					}
				}
				
				// increment column number
//				colNum++;
			}
			if ( oldPath!=null && newPath!=null && !oldBuck.equals(newBuck) ) {
				
				AmazonClient amClient = new AmazonClient();
				amClient.copyFile(oldBuck, oldPath, newBuck, newPath);
				amClient.delFile(oldBuck, oldPath);
				System.out.println("Yes");
				
				Cell cell = row.createCell(7);
				cell.setCellValue(newBuck);
				cell = row.createCell(8);
				cell.setCellValue(newPath);
				cell = row.createCell(9);
				String newFileUrl = "https://" + newBuck + ".s3.amazonaws.com/" + newPath;
				cell.setCellValue(newFileUrl);
				System.out.println("New Url " + newFileUrl);
//				https://s3connector-feb2019.s3.amazonaws.com/root/Fitout/Payable/17119-PO-0067-P-1/circle_(1)-1628510678292.png
				FileOutputStream os = new FileOutputStream(new File(outPath));
				wb.write(os);
				
				
			}
			System.out.println();
			
		}
	}
	
	public static void readFileForProd() throws Exception {

		// obtaining input bytes from a file
//		String inPath = "C:\\Users\\ragha\\Downloads\\devlatestAmazonFilespart21.xlsx";
		String inPath = "C:\\Users\\ragha\\Downloads\\S3Export4.xlsx";
//		String inPath = "C:\\Users\\ragha\\Downloads\\uatAmazonFileExtract1.xlsx";
		
//		String outPath = "C:\\Users\\ragha\\Downloads\\devlatestAmazonFilespart212.xlsx";
		String outPath = "C:\\Users\\ragha\\Downloads\\S3Export5.xlsx";
//		String outPath = "C:\\Users\\ragha\\Downloads\\uatAmazonFileExtract12.xlsx";
		
		FileInputStream fis = new FileInputStream(new File(inPath));
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		// creating a Sheet object to retrieve the object
		XSSFSheet  sheet = wb.getSheetAt(0);
		// evaluating cell type
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		boolean isFirstRow = true;
//		int colNum = 0;
		for (Row row : sheet) // iteration over row using for each loop
		{
			// skip first row
			if ( isFirstRow ) {
				isFirstRow = false;
				continue;
			}
			
			// start column number
//			colNum=0;
			
			String fileName = null;
			String oldPath=null;
			String oldBuck = null;
			String newPath=null;
			String newBuck = "horsepowerelectric";
//			String newBuck = "horsepowerelectric-uat";
			
			boolean flag = false;
			String url = null;
			
			for (Cell cell : row) // iteration over cell using for each loop
			{
				String value = "";
				switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
				case Cell.CELL_TYPE_NUMERIC: // field that represents numeric cell type
					// getting the value of the cell as a number
					//System.out.print(cell.getNumericCellValue() + "\t\t");
					value = "" + cell.getNumericCellValue();
					break;
				case Cell.CELL_TYPE_STRING: // field that represents string cell type
					// getting the value of the cell as a string
					//System.out.print(cell.getStringCellValue() + "\t\t");
					value = cell.getStringCellValue();
					break;
				}
				
				
				
				//check column number
				
				//System.out.print(cell.getColumnIndex() + "\t\t");
				
//				//System.out.print(cell.getRowIndex() + "\t\t");
				
				if ( cell.getColumnIndex() == 1 ) {
					// fileurl
					// file name
					if ( !value.isEmpty() ) {
						int lastDotIdx = value.lastIndexOf('/') + "/".length();
						fileName = value.substring(lastDotIdx, value.length());
						
						int comIdx = value.indexOf(".com/") + ".com/".length();
						oldPath = value.substring(comIdx, value.length());
						
						int s3Idx = value.indexOf(".s3");
						int httpIdx = value.indexOf("https://") + "https://".length() ;
						if ( s3Idx > 0 ) {
							oldBuck = value.substring(httpIdx, s3Idx);
						} else {
							s3Idx = value.indexOf(".s3");
							httpIdx = value.indexOf('/', comIdx+2);
							if ( httpIdx > 0 ) {
								oldBuck = value.substring(comIdx, httpIdx);
								if ( oldBuck.contains("/") ) {
									oldBuck = oldBuck.substring(1);
									oldPath = oldPath.substring(oldBuck.length()+2);
								}
							}
						}
						
					    int accIdx = oldPath.indexOf("?AWSAccessKeyId");
					    int xIdx = oldPath.indexOf("?X-Amz-Algorithm");
					    int exIdx = oldPath.indexOf("?Expires");
					    if ( accIdx > 0 ) {
					    	oldPath = oldPath.substring(0, accIdx);
					    }
					    if ( xIdx > 0 ) {
					    	oldPath = oldPath.substring(0, xIdx);
					    }
					    if ( exIdx > 0 ) {
					    	oldPath = oldPath.substring(0, exIdx);
					    }
						
						System.out.println("OldPath is " + oldPath + "\t\t");
						oldPath = EncodingUtil.getDecodedStr(oldPath);
						System.out.println("FileName is " + fileName + "\t\t");
						System.out.println("OldPath is " + oldPath + "\t\t");
						System.out.println("OldBuck is " + oldBuck + "\t\t");
						
						url = AmazonUtil.generateFileUrl(oldBuck, oldPath);
						InputStream in = null;
						try {
							in = FetchingUtil.getDirectPDFFile(url);
						} catch (Exception e) {
							System.out.println("Exception");
						}
						if ( in != null ) {
							flag = true;
							System.out.println("Yes Yes");
							in.close();
						}
						
						System.out.println("url is " + url + "\t\t");
					} 
				} else if ( cell.getColumnIndex() == 5 ) {
					// path
					if ( !value.isEmpty() && flag ) {
						flag =  false;
					}
				}
				
				// increment column number
//				colNum++;
			}
			if ( flag ) {
				
				System.out.println("Writing");
				flag = false;
				int idx = url.indexOf("?X-");
				String furl1 = url.substring(0, idx);
				String furl2 = url.substring(idx);
				
				Cell cell = row.createCell(5);
				cell.setCellValue(furl1);
				cell = row.createCell(6);
				cell.setCellValue(furl2);
				
//				https://s3connector-feb2019.s3.amazonaws.com/root/Fitout/Payable/17119-PO-0067-P-1/circle_(1)-1628510678292.png
				FileOutputStream os = new FileOutputStream(new File(outPath));
				wb.write(os);
				
				
			}
			System.out.println();
			
		}
	}
	
	
	public static void copyProdFiles() throws Exception {

		
		
		String userDir = System.getProperty("user.dir");
		System.out.println("Current Dir " + System.getProperty("user.dir"));
		String inPath = userDir + File.separator + "result1.xlsx";
		String outPath = userDir + File.separator + "result2.xlsx";
		
		FileInputStream fis = new FileInputStream(new File(inPath));
		FileOutputStream fos = new FileOutputStream(new File(outPath));
		
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		//XSSFSheet  sheet = wb.getSheetAt(wb.getActiveSheetIndex());
		XSSFSheet  sheet = wb.getSheetAt(1);
		
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		boolean isFirstRow = true;
		
		Row finalRow = null;
		String errorMsg = null;
		
		try {
			
			for (Row row : sheet) // iteration over row using for each loop
			{
				// skip first row
				if ( isFirstRow ) {
					isFirstRow = false;
					continue;
				}
				finalRow = row;
				errorMsg = null;
				
				// start column number colNum=0;
				String oldFileName = null;
				String oldPath=null;
				String oldBuck = null;
				String tstamp = null;
				String fileExt = null;
				
				String newFileName = null;
				String newPath=null;
				String newBuck = "horsepowerelectric";
				
				String fileNameCol = null;
				String fileExtCol = null;
				
				String recCount = null;
				String finalName = null;
				String finalPath = null;
				
				boolean breakCopy = false;
				
				for (Cell cell : row)
				{
					
					// check
					/* if ( cell.getRowIndex() == 10) {
						wb.close();
						System.exit(0);
					}*/
					
					
					
					if ( cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() == 2 || cell.getColumnIndex()>6 ) {
						continue;
					}
					System.out.println("Cell Index is " + cell.getColumnIndex());
					
					String value = "";
					try {
						switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
						case Cell.CELL_TYPE_NUMERIC: 
							//System.out.print(cell.getNumericCellValue() + "\t\t");
							value = "" + cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING: 
							//System.out.print(cell.getStringCellValue() + "\t\t");
							value = cell.getStringCellValue();
							break;
						}
					} catch (Throwable e) {
						e.printStackTrace();
						System.out.println("Type Exception is " + e);
						errorMsg = e.getMessage();
						continue;
					}
					
					//System.out.print(cell.getColumnIndex() + "\t\t");
					//System.out.print(cell.getRowIndex() + "\t\t");
					if ( cell.getColumnIndex() == 3 ) {
						if ( !value.isEmpty() ) {
							
							// 17120-C-0263-1622464876798.pdf 30 
							fileNameCol = value;
							
							// .jpeg .pdf .docx
							int idx = fileNameCol.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = fileNameCol.length()-idx;
								if ( loc==4 || loc==5 ) {
									fileExtCol = fileNameCol.substring(idx);
									fileNameCol = fileNameCol.substring(0, idx);	
								}
							}
							
							System.out.println("FileNameCol is " + fileNameCol + "\t\t");
							System.out.println("FileExtCol is " + fileExtCol + "\t\t");
						}
						
					} else if ( cell.getColumnIndex() == 4 ) {
						
						// get old file path
						// get old file name
						// get timestamp
						// get file extension
						
						if ( !value.isEmpty() ) {
							
							int slashIdx = value.lastIndexOf('/') + "/".length();
							oldFileName = value.substring(slashIdx, value.length());
							
							int comIdx = value.indexOf(".com/") + ".com/".length();
							oldPath = value.substring(comIdx, value.length());
							
							int s3Idx = value.indexOf(".s3");
							int httpIdx = value.indexOf("https://") + "https://".length() ;
							if ( s3Idx > 0 ) {
								oldBuck = value.substring(httpIdx, s3Idx);
							} else {
								s3Idx = value.indexOf(".s3");
								httpIdx = value.indexOf('/', comIdx+2);
								if ( httpIdx > 0 ) {
									oldBuck = value.substring(comIdx, httpIdx);
									if ( oldBuck.contains("/") ) {
										oldBuck = oldBuck.substring(1);
										oldPath = oldPath.substring(oldBuck.length()+2);
									}
								}
							}
							
							// extension of file
							int idx = oldPath.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = oldPath.length()-idx;
								if ( loc==4 || loc==5 ) {
									fileExt = oldPath.substring(idx);
									oldPath = oldPath.substring(0, idx);
								}
							}
							
							oldPath=oldPath.replaceAll("â€“", "–");
							oldPath=oldPath.replaceAll("â€™", "’");
							oldPath=oldPath.replaceAll("Â", "");
							try {
								oldPath = EncodingUtil.getDecodedUrl(oldPath);
							} catch (IllegalArgumentException ile) {
								 if (oldPath.endsWith("%")) {
									 oldPath = oldPath.substring(0, oldPath.lastIndexOf("%"));
									 oldPath = EncodingUtil.getDecodedUrl(oldPath);
									 oldPath = oldPath + "%";
								 }
							}
							
							oldPath = oldPath + fileExt;
							
							/*
							// timestamp of file
							String find = null;
							if ( oldPath.length()>30 ) {
								Pattern p = Pattern.compile("-?\\d+");
								Matcher m = p.matcher(oldPath.substring(oldPath.length()-30, oldPath.length()));
								// if an occurrence if a pattern was found in a given string...
						        
						        while (m.find()) {
						        	find = m.group();
						            System.out.println(m.group()); // second matched  "-?\\d+"
						        }
								
							}
							
						
							if ( find!=null && ( find.length()>10 && find.length()<13 ) ) {
								tstamp = find;
								System.out.println("Find TS " +  find);
							}
							
							*/
							System.out.println("Old File Ext is " + fileExt + "\t\t");
							//System.out.println("Old File Timestamp  is " + tstamp + "\t\t");
							System.out.println("Old File Name is " + oldFileName + "\t\t");
							System.out.println("Old Path is " + oldPath + "\t\t");
							System.out.println("Old Bucket is " + oldBuck + "\t\t");
						} 
					} else if ( cell.getColumnIndex() == 5 ) {
						// get new file path
						// get new file name
						// check file name has extension or not
						
						if ( !value.isEmpty() && oldFileName!=null ) {
							
							// remove protocol
							newPath=value.replaceAll("https://horsepowerelectric.s3.amazonaws.com/", "");
							
							
							// remove extension .jpeg .pdf .docx
							int idx = newPath.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = newPath.length()-idx;
								if ( loc==4 || loc==5 ) {
									newPath = newPath.substring(0, idx);
								}
							}
							
							newPath=newPath.replaceAll("â€“", "–");
							newPath=newPath.replaceAll("â€™", "’");
							newPath=newPath.replaceAll("Â", "");
							
							
							try {
								newPath = EncodingUtil.getDecodedUrl(newPath);
							} catch (IllegalArgumentException ile) {
								 if (newPath.endsWith("%")) {
									 newPath = newPath.substring(0, newPath.lastIndexOf("%"));
									 newPath = EncodingUtil.getDecodedUrl(newPath);
									 newPath = newPath + "%";
								 }
								 
							}
							
							//newPath=newPath.replaceAll("%20", " ");
							//newPath=newPath.replaceAll("%2B", "+");
							
							
							int slashIdx = newPath.lastIndexOf('/');
							if ( slashIdx>0 && slashIdx!=newPath.length() ) {
								newFileName = newPath.substring(slashIdx+1);
								newPath = newPath.substring(0, slashIdx+1);
							}
							
							
							fileExt = fileExt == null ? fileExtCol : fileExt;
							finalName = newFileName + fileExt;
							finalPath = newPath + finalName;
							
							/*
							//Pattern p = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)");
							Pattern p = Pattern.compile("-?\\d+");
							// create matcher for pattern p and given string
					        Matcher m = p.matcher(newFileName);

					        // if an occurrence if a pattern was found in a given string...
					        String find = null;
					        while (m.find()) {
					        	find = m.group();
					            System.out.println(m.group()); // second matched  "-?\\d+"
					        }
							if ( find!=null && ( find.length()>=3 && find.length()<=4 )  ) {
								recCount = find;
								System.out.println("Find RC " +  find);
							}
							
							if ( tstamp!=null && newFileName.contains(tstamp) ) {
								finalName = newFileName.substring(0, newFileName.indexOf(tstamp));
							}
							else if ( recCount!=null && newFileName.contains(recCount)  ) {
								finalName = newFileName.substring(0, newFileName.indexOf(recCount) );
							}
							
							if ( tstamp!=null ) {
								if ( tstamp.contains("-") ) {
									finalName = finalName + tstamp;
								} else {
									finalName = finalName + "-" + tstamp;
								}
							}
							
							if ( recCount!=null ) {
								if ( recCount.contains("-") ) {
									finalName = finalName + recCount;
								} else {
									finalName = finalName + "-" + recCount;
								}
							}
							
							if ( fileExt!=null ) {
								finalName = finalName + fileExt;
							}
							*/
							
							System.out.println("New File Name is " + newFileName + "\t\t");
							System.out.println("New File Ext is " + fileExt + "\t\t");
							//System.out.println("New File Timestamp  is " + tstamp + "\t\t");
							//System.out.println("New Record Count  is " + recCount + "\t\t");
							System.out.println("Final File Name is " + finalName + "\t\t");
							
						}
					} else if ( cell.getColumnIndex() == 6 ) {
						if ( "true".equals(value) ) {
							breakCopy = true;
						}
					}
					
					// increment column number
//					colNum++;
				}
				
				if ( oldPath!=null && newPath!=null && !oldBuck.equals(newBuck) ) {
					
					AmazonClient amClient = new AmazonClient();
					
					boolean copied = false;
					
					/* int dSlashIdx = newPath.indexOf("//");
					if ( dSlashIdx>0 ) {
						System.out.println("Double Slash");
						Thread.sleep(1000 * 60 * 1);
					} */
					
					if ( breakCopy ) {
						System.out.println("File Copy Skipped");
						copied = true;
					} else {
						System.out.println("File Copy Started");
						try {
							copied = amClient.copyFileNoACL(oldBuck, oldPath, newBuck, finalPath);
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Copy Exception is " + e);
							errorMsg = e.getMessage();
						}
					}
					//amClient.delFile(oldBuck, oldPath);
					//System.out.println("File Copied end - " + copied);
					
					Cell cell = row.createCell(6);
					cell.setCellValue(""+copied);
					cell = row.createCell(7);
					cell.setCellValue(oldBuck);
					cell = row.createCell(8);
					cell.setCellValue(oldPath);
					cell = row.createCell(9);
					cell.setCellValue(newBuck);
					cell = row.createCell(10);
					cell.setCellValue(newPath);
					cell = row.createCell(11);
					cell.setCellValue(fileExt);
					cell = row.createCell(12);
					cell.setCellValue(fileExtCol);
					cell = row.createCell(13);
					cell.setCellValue(newFileName);
					
					//cell = row.createCell(10);
					//cell.setCellValue(tstamp);
					//cell = row.createCell(11);
					//cell.setCellValue(recCount);
					
					
					
					cell = row.createCell(14);
					cell.setCellValue(finalName);
					cell = row.createCell(15);
					cell.setCellValue(finalPath);
					
					cell = row.createCell(16);
					String newFileUrl = "https://" + newBuck + ".s3.amazonaws.com/" + finalPath;
					cell.setCellValue(newFileUrl);
					System.out.println("Final File Url is  " + newFileUrl);
					
					if ( errorMsg!=null ) {
						cell = row.createCell(17);
						cell.setCellValue(errorMsg);
						System.out.println("Error is " + errorMsg);
					}
		
					fos = new FileOutputStream(new File(outPath));
					wb.write(fos);
					fos.close();
					System.out.println("Write Complete");
					
					
				}
				System.out.println();
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("Write Exception is " + e);
			Cell eCell = finalRow.createCell(17);
			eCell.setCellValue(e.getMessage());
			fos = new FileOutputStream(new File(outPath));
			wb.write(fos);
		} finally {
			try {
				System.out.println("Closing Wb");
				wb.close();
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Close WB Exception is " + e);
			}
		}
		
	}
	
	public static void copyOldFilesSept22() throws Exception {

		
		String userDir = System.getProperty("user.dir");
		System.out.println("Current Dir " + System.getProperty("user.dir"));
		String inPath = userDir + File.separator + "Amazon-Check Extension.xlsx";
		String outPath = userDir + File.separator + "Amazon-Check Extension-2.xlsx";
		
		FileInputStream fis = new FileInputStream(new File(inPath));
		FileOutputStream fos = new FileOutputStream(new File(outPath));
		
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		//XSSFSheet  sheet = wb.getSheetAt(wb.getActiveSheetIndex());
		XSSFSheet  sheet = wb.getSheetAt(0);
		
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		
		Row finalRow = null;
		String errorMsg = null;
		
		try {
			
			for (Row row : sheet) // iteration over row using for each loop
			{
				// skip first row
				if ( row.getRowNum() == 0 ) {
					continue;
				}
				
				finalRow = row;
				errorMsg = null;
				
				// start column number colNum=0;
				String oldFileName = null;
				String oldPath=null;
				String oldBuck = null;
				String tstamp = null;
				String fileExt = null;
				
				String newFileName = null;
				String newPath=null;
				//String newBuck = "horsepowerelectric";
				String newBuck = "horsepowernyc"; 
				
				String fileNameCol = null;
				String fileExtCol = null;
				
				String recCount = null;
				String finalName = null;
				String finalPath = null;
				
				boolean breakCopy = false;
				boolean breakAnother = false;
				
				for (Cell cell : row)
				{
					
					// check
					/* if ( cell.getRowIndex() == 10) {
						wb.close();
						System.exit(0);
					}*/
					
					
					System.out.println("Cell Index is " + cell.getColumnIndex());
					
					if ( cell.getColumnIndex() <=2 || cell.getColumnIndex()>=5 ) {
						continue;
					}
					
					
					
					String value = "";
					try {
						switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
						case Cell.CELL_TYPE_NUMERIC: 
							//System.out.print(cell.getNumericCellValue() + "\t\t");
							value = "" + cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING: 
							//System.out.print(cell.getStringCellValue() + "\t\t");
							value = cell.getStringCellValue();
							break;
						}
					} catch (Throwable e) {
						e.printStackTrace();
						System.out.println("Type Exception is " + e);
						errorMsg = e.getMessage();
						continue;
					}
					
					//System.out.print(cell.getColumnIndex() + "\t\t");
					//System.out.print(cell.getRowIndex() + "\t\t");
					if ( cell.getColumnIndex() == 2 ) {
						if ( !value.isEmpty() ) {
							
							// 17120-C-0263-1622464876798.pdf 30 
							fileNameCol = value;
							
							// .jpeg .pdf .docx
							int idx = fileNameCol.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = fileNameCol.length()-idx;
								if ( loc==4 || loc==5 ) {
									fileExtCol = fileNameCol.substring(idx);
									fileNameCol = fileNameCol.substring(0, idx);	
								}
							}
							
							System.out.println("FileNameCol is " + fileNameCol + "\t\t");
							System.out.println("FileExtCol is " + fileExtCol + "\t\t");
						}
						
					} else if ( cell.getColumnIndex() == 3 ) {
						
						// get old file path
						// get old file name
						// get timestamp
						// get file extension
						
						if ( !value.isEmpty() ) {
							
							int slashIdx = value.lastIndexOf('/') + "/".length();
							oldFileName = value.substring(slashIdx, value.length());
							
							int comIdx = value.indexOf(".com/") + ".com/".length();
							oldPath = value.substring(comIdx, value.length());
							
							int s3Idx = value.indexOf(".s3");
							int httpIdx = value.indexOf("https://") + "https://".length() ;
							if ( s3Idx > 0 ) {
								oldBuck = value.substring(httpIdx, s3Idx);
							} else {
								s3Idx = value.indexOf(".s3");
								httpIdx = value.indexOf('/', comIdx+2);
								if ( httpIdx > 0 ) {
									oldBuck = value.substring(comIdx, httpIdx);
									if ( oldBuck.contains("/") ) {
										oldBuck = oldBuck.substring(1);
										oldPath = oldPath.substring(oldBuck.length()+2);
									}
								}
							}
							
							// extension of file
							int idx = oldPath.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = oldPath.length()-idx;
								if ( loc==4 || loc==5 ) {
									fileExt = oldPath.substring(idx);
									oldPath = oldPath.substring(0, idx);
								}
							}
							
							oldPath=oldPath.replaceAll("â€“", "–");
							oldPath=oldPath.replaceAll("â€™", "’");
							oldPath=oldPath.replaceAll("Â", "");
							try {
								oldPath = EncodingUtil.getDecodedUrl(oldPath);
							} catch (IllegalArgumentException ile) {
								 if (oldPath.endsWith("%")) {
									 oldPath = oldPath.substring(0, oldPath.lastIndexOf("%"));
									 oldPath = EncodingUtil.getDecodedUrl(oldPath);
									 oldPath = oldPath + "%";
								 }
							}
							
							if ( fileExt!=null ) {
								oldPath = oldPath + fileExt;
							}
							
							
							/*
							// timestamp of file
							String find = null;
							if ( oldPath.length()>30 ) {
								Pattern p = Pattern.compile("-?\\d+");
								Matcher m = p.matcher(oldPath.substring(oldPath.length()-30, oldPath.length()));
								// if an occurrence if a pattern was found in a given string...
						        
						        while (m.find()) {
						        	find = m.group();
						            System.out.println(m.group()); // second matched  "-?\\d+"
						        }
								
							}
							
						
							if ( find!=null && ( find.length()>10 && find.length()<13 ) ) {
								tstamp = find;
								System.out.println("Find TS " +  find);
							}
							
							*/
							System.out.println("Old File Ext is " + fileExt + "\t\t");
							//System.out.println("Old File Timestamp  is " + tstamp + "\t\t");
							System.out.println("Old File Name is " + oldFileName + "\t\t");
							System.out.println("Old Path is " + oldPath + "\t\t");
							System.out.println("Old Bucket is " + oldBuck + "\t\t");
						} 
					} 
					/* else if ( cell.getColumnIndex() == 4 ) {
						
						if ( oldPath!=null && oldBuck!=null ) {
							newBuck = oldBuck;
							newPath = "/oldSept2022/" + fileNameCol + System.currentTimeMillis() + fileExtCol;
							System.out.println("Brand New Path is " + newPath);
						}
						
					}*/
					/* else if ( cell.getColumnIndex() == 4 ) {
						// get new file path
						// get new file name
						// check file name has extension or not
						
						if ( !value.isEmpty() && oldFileName!=null ) {
							
							// remove protocol
							newPath=value.replaceAll("https://horsepowerelectric.s3.amazonaws.com/", "");
							
							
							// remove extension .jpeg .pdf .docx
							int idx = newPath.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = newPath.length()-idx;
								if ( loc==4 || loc==5 ) {
									newPath = newPath.substring(0, idx);
								}
							}
							
							newPath=newPath.replaceAll("â€“", "–");
							newPath=newPath.replaceAll("â€™", "’");
							newPath=newPath.replaceAll("Â", "");
							
							
							try {
								newPath = EncodingUtil.getDecodedUrl(newPath);
							} catch (IllegalArgumentException ile) {
								 if (newPath.endsWith("%")) {
									 newPath = newPath.substring(0, newPath.lastIndexOf("%"));
									 newPath = EncodingUtil.getDecodedUrl(newPath);
									 newPath = newPath + "%";
								 }
								 
							}
							
							//newPath=newPath.replaceAll("%20", " ");
							//newPath=newPath.replaceAll("%2B", "+");
							
							
							int slashIdx = newPath.lastIndexOf('/');
							if ( slashIdx>0 && slashIdx!=newPath.length() ) {
								newFileName = newPath.substring(slashIdx+1);
								newPath = newPath.substring(0, slashIdx+1);
							}
							
							
							fileExt = fileExt == null ? fileExtCol : fileExt;
							finalName = newFileName + fileExt;
							finalPath = newPath + finalName;
							*/
							
							/*
							//Pattern p = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)");
							Pattern p = Pattern.compile("-?\\d+");
							// create matcher for pattern p and given string
					        Matcher m = p.matcher(newFileName);

					        // if an occurrence if a pattern was found in a given string...
					        String find = null;
					        while (m.find()) {
					        	find = m.group();
					            System.out.println(m.group()); // second matched  "-?\\d+"
					        }
							if ( find!=null && ( find.length()>=3 && find.length()<=4 )  ) {
								recCount = find;
								System.out.println("Find RC " +  find);
							}
							
							if ( tstamp!=null && newFileName.contains(tstamp) ) {
								finalName = newFileName.substring(0, newFileName.indexOf(tstamp));
							}
							else if ( recCount!=null && newFileName.contains(recCount)  ) {
								finalName = newFileName.substring(0, newFileName.indexOf(recCount) );
							}
							
							if ( tstamp!=null ) {
								if ( tstamp.contains("-") ) {
									finalName = finalName + tstamp;
								} else {
									finalName = finalName + "-" + tstamp;
								}
							}
							
							if ( recCount!=null ) {
								if ( recCount.contains("-") ) {
									finalName = finalName + recCount;
								} else {
									finalName = finalName + "-" + recCount;
								}
							}
							
							if ( fileExt!=null ) {
								finalName = finalName + fileExt;
							}
							*/
					
							/*
							
							System.out.println("New File Name is " + newFileName + "\t\t");
							System.out.println("New File Ext is " + fileExt + "\t\t");
							//System.out.println("New File Timestamp  is " + tstamp + "\t\t");
							//System.out.println("New Record Count  is " + recCount + "\t\t");
							System.out.println("Final File Name is " + finalName + "\t\t");
							
						}
					}*/ 
					else if ( cell.getColumnIndex() == 4 ) {
						if ( "true".equals(value) ) {
							breakCopy = true;
						}
					}
					
					// increment column number
//					colNum++;
				}
				
				if ( oldPath!=null && oldBuck!=null) { // && newPath!=null && !oldBuck.equals(newBuck) ) {
					
					//newBuck = oldBuck;
					//if ( fileNameCol.length() < 12 ) {
					//	fileNameCol = fileNameCol + "-" + System.currentTimeMillis();
					//}
					//newPath = "oldSept2022/" + fileNameCol + fileExt;
					System.out.println("Brand New Path is " + newPath);
					
					AmazonClient amClient = new AmazonClient();
					
					boolean copied = false;
					
					/* int dSlashIdx = newPath.indexOf("//");
					if ( dSlashIdx>0 ) {
						System.out.println("Double Slash");
						Thread.sleep(1000 * 60 * 1);
					} */
					
					if ( breakCopy ) {
						System.out.println("File Copy Skipped");
						copied = true;
					} else {
						System.out.println("File Copy Started");
						try {
//							copied=true;
							copied = amClient.objKeyExists(oldBuck, oldPath);
							if ( !copied ) {
								oldPath = oldPath + ".pdf";
								breakAnother = amClient.objKeyExists(oldBuck, oldPath);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Copy Exception is " + e);
							errorMsg = e.getMessage();
						}
					}
					//amClient.delFile(oldBuck, oldPath);
					//System.out.println("File Copied end - " + copied);
					
					Cell cell = row.createCell(5);
					cell.setCellValue(""+copied);
					cell = row.createCell(6);
					cell.setCellValue(""+breakAnother);
					/*
					cell = row.createCell(7);
					cell.setCellValue(oldBuck);
					cell = row.createCell(8);
					cell.setCellValue(oldPath);
					cell = row.createCell(9);
					cell.setCellValue(newBuck);
					cell = row.createCell(10);
					cell.setCellValue(newPath);
					cell = row.createCell(11);
					cell.setCellValue(fileExt);
					cell = row.createCell(12);
					cell.setCellValue(fileExtCol);
					cell = row.createCell(13);
					cell.setCellValue(newFileName);
					*/
					
					//cell = row.createCell(10);
					//cell.setCellValue(tstamp);
					//cell = row.createCell(11);
					//cell.setCellValue(recCount);
					
					
					/*
					cell = row.createCell(14);
					cell.setCellValue(finalName);
					cell = row.createCell(15);
					cell.setCellValue(finalPath);
					*/
					cell = row.createCell(7);
					String newFileUrl = "https://" + oldBuck + ".s3.amazonaws.com/" + oldPath;
					cell.setCellValue(newFileUrl);
					System.out.println("Final File Url is  " + newFileUrl);
					
					if ( errorMsg!=null ) {
						cell = row.createCell(8);
						cell.setCellValue(errorMsg);
						System.out.println("Error is " + errorMsg);
					}
		
					if ( row.getRowNum()%200==0 ) {
						fos = new FileOutputStream(new File(outPath));
						wb.write(fos);
						fos.close();
						System.out.println("Write Complete");
						//System.exit(0);
					}
					
					
				}
				System.out.println();
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("Write Exception is " + e);
			Cell eCell = finalRow.createCell(8);
			eCell.setCellValue(e.getMessage());
			fos = new FileOutputStream(new File(outPath));
			wb.write(fos);
		} finally {
			try {
				System.out.println("Finally Closing Wb");
				fos = new FileOutputStream(new File(outPath));
				wb.write(fos);
				fos.close();
				wb.close();
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Close WB Exception is " + e);
			}
		}
		
	}
	
	public static void deleteProdFiles() throws Exception {

		String userDir = System.getProperty("user.dir");
		System.out.println("Current Dir " + System.getProperty("user.dir"));
		String inPath = userDir + File.separator + "result2.xlsx";
		String outPath = userDir + File.separator + "result3.xlsx";
		
		FileInputStream fis = new FileInputStream(new File(inPath));
		FileOutputStream fos = new FileOutputStream(new File(outPath));
		
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		//XSSFSheet  sheet = wb.getSheetAt(wb.getActiveSheetIndex());
		XSSFSheet  sheet = wb.getSheetAt(0);
		
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		
		Row finalRow = null;
		int rowNum = 0;
		
		try {
			
			for (Row row : sheet) // iteration over row using for each loop
			{
				
				rowNum = row.getRowNum();
				
				// skip first row
				if ( rowNum == 0 ) {
					continue;
				}
				
				// initializing the row
				finalRow = row;
				
				
				// start column number colNum=0;
				String oldFileName = null;
				String oldPath=null;
				String oldBuck = null;
				String fileExt = null;
				
				String fileNameCol = null;
				String fileExtCol = null;
				
				boolean copied = false;
				boolean deleted = false;
				String errorMsg = null;
				
				for (Cell cell : row)
				{
					
					// continue for next cell if not required
					if ( cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() == 2 || ( cell.getColumnIndex()>6 && cell.getColumnIndex()<18  ) ) {
						continue;
					}
					
					System.out.print("Column index is " + cell.getColumnIndex() + "\t\t");
					//System.out.print("Row index is " + cell.getRowIndex() + "\t\t");
					
					String value = "";
					try {
						switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
						case Cell.CELL_TYPE_NUMERIC: 
							//System.out.print(cell.getNumericCellValue() + "\t\t");
							value = "" + cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING: 
							//System.out.print(cell.getStringCellValue() + "\t\t");
							value = cell.getStringCellValue();
							break;
						}
					} catch (Throwable e) {
						//e.printStackTrace();
						System.out.println("Cell Type Exception is " + e);
						errorMsg = e.getMessage();
						continue;
					}
					
					
					if ( cell.getColumnIndex() == 3 ) {
						
						if ( !value.isEmpty() ) {
							
							// 17120-C-0263-1622464876798.pdf 30 
							fileNameCol = value;
							
							// .jpeg .pdf .docx
							int idx = fileNameCol.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = fileNameCol.length()-idx;
								if ( loc==4 || loc==5 ) {
									fileExtCol = fileNameCol.substring(idx);
									fileNameCol = fileNameCol.substring(0, idx);	
								}
							}
							
							System.out.println("FileNameCol is " + fileNameCol + "\t\t");
							System.out.println("FileExtCol is " + fileExtCol + "\t\t");
						}
						
					} else if ( cell.getColumnIndex() == 4 ) {
						
						// get old file path
						// get old file name
						// get file extension
						
						if ( !value.isEmpty() ) {
							
							int slashIdx = value.lastIndexOf('/') + "/".length();
							oldFileName = value.substring(slashIdx, value.length());
							
							int comIdx = value.indexOf(".com/") + ".com/".length();
							oldPath = value.substring(comIdx, value.length());
							
							
							//removing protocol
							int s3Idx = value.indexOf(".s3");
							int httpIdx = value.indexOf("https://") + "https://".length() ;
							if ( s3Idx > 0 ) {
								oldBuck = value.substring(httpIdx, s3Idx);
							} else {
								s3Idx = value.indexOf(".s3");
								httpIdx = value.indexOf('/', comIdx+2);
								if ( httpIdx > 0 ) {
									oldBuck = value.substring(comIdx, httpIdx);
									if ( oldBuck.contains("/") ) {
										oldBuck = oldBuck.substring(1);
										oldPath = oldPath.substring(oldBuck.length()+2);
									}
								}
							}
							
							// extension of file
							int idx = oldPath.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = oldPath.length()-idx;
								if ( loc==4 || loc==5 ) {
									fileExt = oldPath.substring(idx);
									oldPath = oldPath.substring(0, idx);
								}
							}
							
							// decoding special characters
							oldPath=oldPath.replaceAll("â€“", "–");
							oldPath=oldPath.replaceAll("â€™", "’");
							oldPath=oldPath.replaceAll("Â", "");
							try {
								oldPath = EncodingUtil.getDecodedUrl(oldPath);
							} catch (IllegalArgumentException ile) {
								 if (oldPath.endsWith("%")) {
									 oldPath = oldPath.substring(0, oldPath.lastIndexOf("%"));
									 oldPath = EncodingUtil.getDecodedUrl(oldPath);
									 oldPath = oldPath + "%";
								 }
							}
							oldPath = oldPath + fileExt;
							
						
							System.out.println("Old File Ext is " + fileExt + "\t\t");
							System.out.println("Old File Name is " + oldFileName + "\t\t");
							System.out.println("Old Path is " + oldPath + "\t\t");
							System.out.println("Old Bucket is " + oldBuck + "\t\t");
						} 
					} else if ( cell.getColumnIndex() == 6 ) {
						if ( "true".equals(value) ) {
							copied = true;
							System.out.println("Yes File is copied");
						}
					} else if ( cell.getColumnIndex() == 18 ) {
						if ( "true".equals(value) ) {
							deleted = true;
							System.out.println("Yes File is deleted");
						}
					}
					
				}
				
				if ( oldPath!=null && oldBuck!=null ) {
					
					AmazonClient amClient = new AmazonClient();
					
					if ( copied && !deleted ) {
						deleted  = amClient.delFile(oldBuck, oldPath);
						System.out.println("File Deleted - " + deleted);
						if ( !deleted ) {
							errorMsg = "Internet or Key Issue";
						}
					} else {
						System.out.println("File Deleted Skipped - " + deleted);
					}
			
					Cell cell = row.createCell(18);
					cell.setCellValue(""+deleted);
					
					if ( errorMsg!=null ) {
						cell = row.createCell(19);
						cell.setCellValue(errorMsg);
						System.out.println("Error is " + errorMsg);
					}
		
					if ( rowNum%200==0) {
						fos = new FileOutputStream(new File(outPath));
						wb.write(fos);
						fos.close();
						System.out.println("Write Complete");
					}
					
				}
				
				System.out.println();
			}
			
		} catch (Throwable e) {
			try {
				e.printStackTrace();
				System.out.println("Catch Exception is " + e);
				Cell eCell = finalRow.createCell(19);
				eCell.setCellValue(e.getMessage());
				fos = new FileOutputStream(new File(outPath));
				wb.write(fos);
				fos.close();
			}catch (Exception e1) {
				e.printStackTrace();
				System.out.println("Exception during catch is " + e1);
			}
			
		} finally {
			try {
				System.out.println("Finally Closing WB");
				fos = new FileOutputStream(new File(outPath));
				wb.write(fos);
				fos.close();
				wb.close();
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception during finally is " + e);
			}
		}
		
	}
	
	
	public static void deleteFilesOther() throws Exception {

		String userDir = System.getProperty("user.dir");
		System.out.println("Current Dir " + System.getProperty("user.dir"));
		String inPath = userDir + File.separator + "final1.xlsx";
		String outPath = userDir + File.separator + "final2.xlsx";
		
		FileInputStream fis = new FileInputStream(new File(inPath));
		FileOutputStream fos = new FileOutputStream(new File(outPath));
		
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		//XSSFSheet  sheet = wb.getSheetAt(wb.getActiveSheetIndex());
		XSSFSheet  sheet = wb.getSheetAt(4);
		
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		boolean isFirstRow = true;
		int rowNum = 1;
		
		Row finalRow = null;
		
		
		try {
			
			for (Row row : sheet) // iteration over row using for each loop
			{
				
				// skip first row
				if ( isFirstRow ) {
					isFirstRow = false;
					continue;
				}
				
				// initializing the row
				finalRow = row;
				rowNum = row.getRowNum();
				
				// start column number colNum=0;
				String oldFileName = null;
				String oldPath=null;
				String oldBuck = null;
				String fileExt = null;
				
				
				boolean deleted = false;
				String errorMsg = null;
				
				for (Cell cell : row)
				{
					
					// continue for next cell if not required
					if ( cell.getColumnIndex() <6 || cell.getColumnIndex()>8 ) {
						continue;
					}
					
					System.out.print("Column index is " + cell.getColumnIndex() + "\t\t");
					//System.out.print("Row index is " + cell.getRowIndex() + "\t\t");
					
					String value = "";
					try {
						switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
						case Cell.CELL_TYPE_NUMERIC: 
							//System.out.print(cell.getNumericCellValue() + "\t\t");
							value = "" + cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING: 
							//System.out.print(cell.getStringCellValue() + "\t\t");
							value = cell.getStringCellValue();
							break;
						}
					} catch (Throwable e) {
						//e.printStackTrace();
						System.out.println("Cell Type Exception is " + e);
						errorMsg = e.getMessage();
						continue;
					}
					
					
					if ( cell.getColumnIndex() == 6 ) {
						
						// get old file path
						// get old file name
						// get file extension
						
						if ( !value.isEmpty() ) {
							
							int slashIdx = value.lastIndexOf('/') + "/".length();
							oldFileName = value.substring(slashIdx, value.length());
							
							int comIdx = value.indexOf(".com/") + ".com/".length();
							oldPath = value.substring(comIdx, value.length());
							
							
							//removing protocol
							int s3Idx = value.indexOf(".s3");
							int httpIdx = value.indexOf("https://") + "https://".length() ;
							if ( s3Idx > 0 ) {
								oldBuck = value.substring(httpIdx, s3Idx);
							} else {
								s3Idx = value.indexOf(".s3");
								httpIdx = value.indexOf('/', comIdx+2);
								if ( httpIdx > 0 ) {
									oldBuck = value.substring(comIdx, httpIdx);
									if ( oldBuck.contains("/") ) {
										oldBuck = oldBuck.substring(1);
										oldPath = oldPath.substring(oldBuck.length()+2);
									}
								}
							}
							
							// extension of file
							int idx = oldPath.lastIndexOf('.');
							if ( idx>0 ) {
								int loc = oldPath.length()-idx;
								if ( loc==4 || loc==5 ) {
									fileExt = oldPath.substring(idx);
									oldPath = oldPath.substring(0, idx);
								}
							}
							
							// decoding special characters
							oldPath=oldPath.replaceAll("â€“", "–");
							oldPath=oldPath.replaceAll("â€™", "’");
							oldPath=oldPath.replaceAll("Â", "");
							try {
								oldPath = EncodingUtil.getDecodedUrl(oldPath);
							} catch (IllegalArgumentException ile) {
								 if (oldPath.endsWith("%")) {
									 oldPath = oldPath.substring(0, oldPath.lastIndexOf("%"));
									 oldPath = EncodingUtil.getDecodedUrl(oldPath);
									 oldPath = oldPath + "%";
								 }
							}
							oldPath = oldPath + fileExt;
							
						
							System.out.println("Old File Ext is " + fileExt + "\t\t");
							System.out.println("Old File Name is " + oldFileName + "\t\t");
							System.out.println("Old Path is " + oldPath + "\t\t");
							System.out.println("Old Bucket is " + oldBuck + "\t\t");
						} 
					} else if ( cell.getColumnIndex() == 7 ) {
						if ( "true".equals(value) ) {
							deleted = true;
							System.out.println("Yes File is deleted");
						}
					}
					
				}
				
				if ( oldPath!=null && oldBuck!=null ) {
					
					AmazonClient amClient = new AmazonClient();
					
					if ( !deleted ) {
						deleted  = amClient.delFile(oldBuck, oldPath);
						System.out.println("File Deleted - " + deleted);
						if ( !deleted ) {
							errorMsg = "Internet or Key Issue";
						}
					} else {
						System.out.println("File Deleted Skipped - " + deleted);
					}
			
					Cell cell = row.createCell(7);
					cell.setCellValue(""+deleted);
					
					if ( errorMsg!=null ) {
						cell = row.createCell(8);
						cell.setCellValue(errorMsg);
						System.out.println("Error is " + errorMsg);
					}
					
					if ( rowNum % 200 == 0 ) {
						fos = new FileOutputStream(new File(outPath));
						wb.write(fos);
						fos.close();
						System.out.println("Write Complete");
					}
					
					
				}
				
				System.out.println();
			}
			
		} catch (Throwable e) {
			try {
				e.printStackTrace();
				System.out.println("Catch Exception is " + e);
				Cell eCell = finalRow.createCell(8);
				eCell.setCellValue(e.getMessage());
				
				fos = new FileOutputStream(new File(outPath));
				wb.write(fos);
				fos.close();
				
			}catch (Exception e1) {
				e.printStackTrace();
				System.out.println("Exception during catch is " + e1);
			}
			
		} finally {
			try {
				System.out.println("Finally Closing WB");
				fos = new FileOutputStream(new File(outPath));
				wb.write(fos);
				fos.close();
				wb.close();
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception during finally is " + e);
			}
		}
		
	}
	
	
				
}
