package com.cf.files.utility.model;

/**
 * @author raghav.simlote
 *
 */
public class FilePO {
	
    public Integer displayOrder;
    public String recordId;
    public String fileUrl;
    public String fileName;
    public String fileType;
    public String fileExtensionType;
    
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
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

}
