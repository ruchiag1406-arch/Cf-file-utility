package com.cf.files.utility.model;

public class AwsUserModel {
	
	private String userId;
	private String userName;
	private String accessKeyId;
	private String accessSecret;
	private String bucketName;
	private String bucketOwner;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAccessKeyId() {
		return accessKeyId;
	}
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}
	public String getAccessSecret() {
		return accessSecret;
	}
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getBucketOwner() {
		return bucketOwner;
	}
	public void setBucketOwner(String bucketOwner) {
		this.bucketOwner = bucketOwner;
	}
	
	
	
	
}
