package com.psl.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class AWSAdminConfiguration {
	
	
	private static AWSCredentials credentials ;

	static{

		/* This credentials provider implementation loads your AWS credentials
		 * from a properties file at the root of your classpath.*/

		credentials = new BasicAWSCredentials(AppAdminCredentails.getValue("accessKey"), 
				AppAdminCredentails.getValue("secretKey"));
		
	
	}

	public static AWSCredentials getCredentials() {
		return credentials;
	}

	public static void setCredentials(AWSCredentials credentials) {
		AWSAdminConfiguration.credentials = credentials;
	}


}
