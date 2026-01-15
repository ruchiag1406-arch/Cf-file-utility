package com.cf.files.utility.amazon.service;

import java.io.File;
import java.net.URL;
import java.util.Date;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AttachUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachUserPolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cf.files.utility.model.AwsUserModel;
import com.cf.files.utility.model.RegisterModel;
import com.cf.files.utility.service.PropService;
import com.cf.files.utility.util.Constants;

public class AmazonService {

	private static AmazonS3 s3client;
	private static RegisterModel rModel;

	private void initClient(String orgId) {

		rModel = new PropService().getData(orgId);
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(rModel.getAwsKey(), rModel.getAwsSecret());

		s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withRegion(Constants.AWS_REGION).build();
	}

	public AwsUserModel createUser(String userName) {

		// final AmazonIdentityManagement iam =
		// AmazonIdentityManagementClientBuilder.defaultClient();

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(Constants.CF_ACCESS_KEY, Constants.CF_SECRET_KEY);
		final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(Constants.AWS_REGION).build();

		CreateUserRequest request = new CreateUserRequest().withUserName(userName);

		CreateUserResult response = iam.createUser(request);

		if (response != null) {
			if (response.getUser().getUserId() != null) {

				AwsUserModel userModel = new AwsUserModel();
				userModel.setUserId(response.getUser().getUserId());
				userModel.setUserName(response.getUser().getUserName());

				AttachUserPolicyRequest policyReq = new AttachUserPolicyRequest()
						.withPolicyArn("arn:aws:iam::aws:policy/AmazonS3FullAccess").withUserName(userName);

				AttachUserPolicyResult policyRes = iam.attachUserPolicy(policyReq);
				if (policyRes != null) {
					/*
					 * CreateLoginProfileRequest clprequest = new
					 * CreateLoginProfileRequest(username, "Test123$"); CreateLoginProfileResult
					 * result = iam.createLoginProfile(clprequest); if (
					 * result.getLoginProfile()!=null) { if (
					 * result.getLoginProfile().getUserName()!=null ) { iam.shutdown(); return
					 * userModel; } }
					 */
					iam.shutdown();
					return userModel;
				}

			}
		}

		return null;
	}

	public AwsUserModel createAccessKey(AwsUserModel userModel) {

		// final AmazonIdentityManagement iam =
		// AmazonIdentityManagementClientBuilder.defaultClient();

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(Constants.CF_ACCESS_KEY, Constants.CF_SECRET_KEY);
		final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(Constants.AWS_REGION).build();

		CreateAccessKeyRequest request = new CreateAccessKeyRequest(userModel.getUserName());

		CreateAccessKeyResult response = iam.createAccessKey(request);

		if (response != null) {
			if (response.getAccessKey() != null) {
				userModel.setAccessKeyId(response.getAccessKey().getAccessKeyId());
				userModel.setAccessSecret(response.getAccessKey().getSecretAccessKey());

				/*
				 * GetAccessKeyLastUsedRequest lastreq = new
				 * GetAccessKeyLastUsedRequest().withAccessKeyId(userModel.getAccessKeyId());
				 * GetAccessKeyLastUsedResult result = iam.getAccessKeyLastUsed(lastreq); if (
				 * result.getAccessKeyLastUsed()!=null ) { System.out.println(" LD " +
				 * result.getAccessKeyLastUsed().getLastUsedDate());
				 * 
				 * }
				 */

				iam.shutdown();
				return userModel;
			}
		}

		return null;
	}

	public AwsUserModel createBucket(AwsUserModel userModel) {

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(userModel.getAccessKeyId(), userModel.getAccessSecret());

		final AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(Constants.AWS_REGION).build();

		CreateBucketRequest request = new CreateBucketRequest(userModel.getBucketName());

		Bucket bucket = s3client.createBucket(request);

		if (bucket != null) {
			s3client.shutdown();
			return userModel;
		}

		return null;

	}

	public URL generateFileUrl(String orgId, String objectKey, Date expiration) {

		if (s3client != null && rModel != null && orgId.equals(rModel.getOrgId())) {

		} else {
			this.initClient(orgId);
		}

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
				rModel.getBucketName(), objectKey).withMethod(HttpMethod.GET).withExpiration(expiration);
		return s3client.generatePresignedUrl(generatePresignedUrlRequest);

	}

	public void uploadFileTos3Url(String orgId, String objectKey, File file) {

		if (s3client != null && rModel != null && orgId.equals(rModel.getOrgId())) {

		} else {
			this.initClient(orgId);
		}

		PutObjectResult result = s3client.putObject(new PutObjectRequest(rModel.getBucketName(), objectKey, file));
		System.out.println("File Upload result is " + result.getETag());

	}

}
