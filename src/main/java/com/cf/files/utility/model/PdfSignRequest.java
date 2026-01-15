package com.cf.files.utility.model;

public class PdfSignRequest {
	
	private String status;
	private String recordId;
	private String bucketName;
	private String amazonS3Url;
	private String sigName;
	private String comments;
	private String sigDate;
	private String org;
	private String instanceUrl;
	private String accessToken;
	private String expDate;
	private String userId;
	private String orgId;
	
	public PdfSignRequest() {
	}
	
	public PdfSignRequest(String status, String recordId, String amazonS3Url, String bucketName, String sigName) {
		this.status = status;
		this.amazonS3Url = amazonS3Url;
		this.bucketName = bucketName;
		this.recordId = recordId;
		this.sigName = sigName;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getAmazonS3Url() {
		return amazonS3Url;
	}
	public void setAmazonS3Url(String amazonS3Url) {
		this.amazonS3Url = amazonS3Url;
	}
	public String getSigName() {
		return sigName;
	}
	public void setSigName(String sigName) {
		this.sigName = sigName;
	}
	public String getSigDate() {
		return sigDate;
	}
	public void setSigDate(String sigDate) {
		this.sigDate = sigDate;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getInstanceUrl() {
		return instanceUrl;
	}
	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	
	
}