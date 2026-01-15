package com.cf.files.utility.model;

import java.io.InputStream;

public class MergeFilePO {
	
	private String fileName;
	private String fileType;
	private String fileExtensionType;
	private InputStream fileStream;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileExtensionType() {
		return fileExtensionType;
	}
	public void setFileExtensionType(String fileExtensionType) {
		this.fileExtensionType = fileExtensionType;
	}
	public InputStream getFileStream() {
		return fileStream;
	}
	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}
	
	

}
