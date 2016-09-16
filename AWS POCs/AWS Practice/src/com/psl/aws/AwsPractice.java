package com.psl.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;

public class AwsPractice {
	
	 static KeyPair keyPair;
	 static AmazonEC2 amazonEC2;
	 private static String accessKey = "AKIAJEW4GNJLWOZ6BDDA";
	 private static String secretKey = "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";
	 private final ClientConfiguration clientConfiguration;
	 private final String PROXY_HOST="ptproxy.persistent.co.in";
	 private final int PROXY_PORT=8080;	
	 
	 public AwsPractice() {
		 	clientConfiguration = new ClientConfiguration();
			clientConfiguration.setProxyHost(PROXY_HOST);
			clientConfiguration.setProxyPort(PROXY_PORT);
	}
	 
	 private static AWSCredentials credentials() {

			return new BasicAWSCredentials(accessKey, secretKey);
		}
		
	
	public static void main(String[] args) {
		
		AWSCredentials credentials = AwsPractice.credentials();
		AwsPractice awsPractice = new AwsPractice();
		amazonEC2 = new AmazonEC2Client(credentials, awsPractice.clientConfiguration);
		
		System.out.println("Amazon Instance : " + amazonEC2);
	
		AmazonCloudWatchClient amazonCloudWatchClient =  new AmazonCloudWatchClient(credentials);
		System.out.println("Cloud Watch : " + amazonCloudWatchClient );
		System.out.println("Service Name: " + amazonCloudWatchClient.getServiceName());
		System.out.println("Region :  "+amazonCloudWatchClient.getSignerRegionOverride());
		System.out.println("Time Offset  "+amazonCloudWatchClient.getTimeOffset());
			
		
	}
	
}
