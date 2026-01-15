package com.cf.files.utility.model;

public class ServiceModel {
	
	private String generateService = "generateFile";
	private String mergeService = "merge";
	private String baseUrl="https://34.231.129.177:8081/";
	private String bucketName;
	private String s3Host = "s3.amazonaws.com";
	private String awsKey;
	private String awsSecret;
	private String signService = "sign";
	
	public ServiceModel() {
		super();
	}
	
	public ServiceModel(String bucketName) {
		super();
		this.bucketName = bucketName;
	}



	public String getGenerateService() {
		return generateService;
	}
	public void setGenerateService(String generateService) {
		this.generateService = generateService;
	}
	public String getMergeService() {
		return mergeService;
	}
	public void setMergeService(String mergeService) {
		this.mergeService = mergeService;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getS3Host() {
		return s3Host;
	}
	public void setS3Host(String s3Host) {
		this.s3Host = s3Host;
	}
	public String getAwsKey() {
		return awsKey;
	}
	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}
	public String getAwsSecret() {
		return awsSecret;
	}
	public void setAwsSecret(String awsSecret) {
		this.awsSecret = awsSecret;
	}
	public String getSignService() {
		return signService;
	}
	public void setSignService(String signService) {
		this.signService = signService;
	}
	
	

}
