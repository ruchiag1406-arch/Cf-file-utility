package com.cf.files.utility.model;

public class RegisterModel extends ServiceModel {
	
	private String orgId;
	private String orgName;
	private boolean isSandbox;
	private String clientConsumer;
	private String clientSecret;
	private String filePath;
	private String userName;
	private String nameSpace;
	
	public RegisterModel() {
		super();
	}
	

	public RegisterModel(String orgId, String orgName, boolean isSandbox, String bucketName, String clientConsumer, String clientSecret,String userName) {
		super(bucketName);
		this.orgId = orgId;
		this.orgName = orgName;
		this.isSandbox = isSandbox;
		this.clientConsumer = clientConsumer;
		this.clientSecret = clientSecret;
		this.userName = userName;
	}
	
	
	
	public RegisterModel(String orgId, String orgName, boolean isSandbox, String bucketName, String clientConsumer, String clientSecret,
			String userName,String filePath, String nameSpace) {
		super(bucketName);
		this.orgId = orgId;
		this.orgName = orgName;
		this.isSandbox = isSandbox;
		this.clientConsumer = clientConsumer;
		this.clientSecret = clientSecret;
		this.userName = userName;
		this.filePath = filePath;
		this.nameSpace = nameSpace;
	}



	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getClientConsumer() {
		return clientConsumer;
	}
	public void setClientConsumer(String clientConsumer) {
		this.clientConsumer = clientConsumer;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isSandbox() {
		return isSandbox;
	}
	public void setSandbox(boolean isSandbox) {
		this.isSandbox = isSandbox;
	}
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	

}
