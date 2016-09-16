package com.aws.main;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;





import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.aws.utility.AwsS3Utility;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;


public class PreSignedURL {

	public static void main(String[] args) {

		String accessKey = "AKIAIPSGWELRAGX527QQ";
        String secretKey = "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";
		AwsS3Utility awsS3Utility = new AwsS3Utility(accessKey, secretKey);
		
		AmazonS3 s3client = awsS3Utility.getS3();
		String bucketName = "onkaarbucket";
		
		try {
			System.out.println("Generating pre-signed URL.");
			java.util.Date expiration = new java.util.Date();
			long milliSeconds = expiration.getTime();
			milliSeconds += 1000 * 60 * 60; // Add 1 hour.
			expiration.setTime(milliSeconds);

			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				    new GeneratePresignedUrlRequest(bucketName, "tempFile");
			generatePresignedUrlRequest.setMethod(HttpMethod.PUT); 
			generatePresignedUrlRequest.setExpiration(expiration);

			URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest); 

			System.out.println("Pre-Signed URL = " + url.toString());
			
			
			UploadObject(url);
			
			File file = new File("D:\\Amazon-Web-S3-Utility.war");

			String output = uploadFile(file, url.toString());
			
			System.out.println("File Uploaded..." + output);
			
		} catch (AmazonServiceException exception) {
			System.out.println("Caught an AmazonServiceException, " +
					"which means your request made it " +
					"to Amazon S3, but was rejected with an error response " +
			"for some reason.");
			System.out.println("Error Message: " + exception.getMessage());
			System.out.println("HTTP  Code: "    + exception.getStatusCode());
			System.out.println("AWS Error Code:" + exception.getErrorCode());
			System.out.println("Error Type:    " + exception.getErrorType());
			System.out.println("Request ID:    " + exception.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, " +
					"which means the client encountered " +
					"an internal error while trying to communicate" +
					" with S3, " +
			"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static String uploadFile(File resourceUrl,String url) throws HttpException, IOException{
		File f = resourceUrl;
		PostMethod filePost = new PostMethod(url);
		Part[] parts = {new FilePart(f.getName(), f)};
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		HttpClient client = new HttpClient();
		int status = client.executeMethod(filePost);
		String resultUUid=null;
		resultUUid = filePost.getResponseBodyAsString();
		filePost.releaseConnection();
		return resultUUid;
	}
	
	public static void UploadObject(URL url) throws IOException
	{
		HttpURLConnection connection=(HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		
		
		
		
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream());
		out.write("This text uploaded as object.");
		out.close();
		int responseCode = connection.getResponseCode();
		System.out.println("Service returned response code " + responseCode);

	}

}
