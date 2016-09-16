package com.psl.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class AmazonS3Basic {

	 static AmazonS3 s3;
	

	private static String accessKey = "AKIAIPSGWELRAGX527QQ";
    private static String secretKey = "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";
	
	private final ClientConfiguration clientConfiguration;
	private final String PROXY_HOST="ptproxy.persistent.co.in";
   	private final int PROXY_PORT=8080;	
    
	public AmazonS3Basic() {
		clientConfiguration = new ClientConfiguration();
		clientConfiguration.setProxyHost(PROXY_HOST);
		clientConfiguration.setProxyPort(PROXY_PORT);	
		
		s3 =  new AmazonS3Client(credentials(), clientConfiguration);
	}

	 static AWSCredentials credentials() {

		return new BasicAWSCredentials(accessKey, secretKey);
	}
	 
	 public static AmazonS3 getS3() {
			return s3;
		}
}
