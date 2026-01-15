package com.cf.files.utility.amazon;

/**
 * @author raghav.simlote
 *
 */
public class AmazonObj {
	
	private String fileUrl;
	private String signedPath;
	private String version;
	private long contentSize;
	private String attachmentId;
	
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public long getContentSize() {
		return contentSize;
	}
	public void setContentSize(long contentSize) {
		this.contentSize = contentSize;
	}
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getSignedPath() {
		return signedPath;
	}
	public void setSignedPath(String signedPath) {
		this.signedPath = signedPath;
	}
	
	
	

}
