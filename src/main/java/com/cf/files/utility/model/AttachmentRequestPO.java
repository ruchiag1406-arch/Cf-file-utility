package com.cf.files.utility.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author raghav.simlote
 *
 */
public class AttachmentRequestPO {
	
	public ArrayList<String> attachmentIds;
	public ArrayList<String> contentDocumentIds;
	public ArrayList<String> fileUrls;
	public ArrayList<FilePO> files;
	public String orgInstance;
	public String parentId;
	public String attachmentName;
	public String sessionId;
	public String uniqueId;
	public String bucketName;
	public String orgBucketName;
	public String sObjectName;
	public String baseUrl;
	public String orgId;
	
	
	public AttachmentRequestPO() {
		uniqueId = UUID.randomUUID().toString();
	}

	public ArrayList<FilePO> getFiles() {
		return files;
	}
	public void setFiles(ArrayList<FilePO> files) {
		this.files = files;
	}

	public ArrayList<String> getFileUrls() {
		return fileUrls;
	}
	public void setFileUrls(ArrayList<String> fileUrls) {
		this.fileUrls = fileUrls;
	}



	public ArrayList<String> getAttachmentIds() {
		return attachmentIds;
	}
	public void setAttachmentIds(ArrayList<String> attachmentIds) {
		this.attachmentIds = attachmentIds;
	}
	public ArrayList<String> getContentDocumentIds() {
		return contentDocumentIds;
	}
	public void setContentDocumentIds(ArrayList<String> contentDocumentIds) {
		this.contentDocumentIds = contentDocumentIds;
	}
	public String getOrgInstance() {
		return orgInstance;
	}
	public void setOrgInstance(String orgInstance) {
		this.orgInstance = orgInstance;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getOrgBucketName() {
		return orgBucketName;
	}
	public void setOrgBucketName(String orgBucketName) {
		this.orgBucketName = orgBucketName;
	}
	public String getsObjectName() {
		return sObjectName;
	}
	public void setsObjectName(String sObjectName) {
		this.sObjectName = sObjectName;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
}
