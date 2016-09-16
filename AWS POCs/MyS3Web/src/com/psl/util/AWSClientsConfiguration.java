package com.psl.util;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;


/**
 * The only information needed to create a client are security credentials
 * consisting of the AWS Access Key ID and Secret Access Key. All other
 * configuration, such as the service endpoints, are performed
 * automatically. Client parameters, such as proxies, can be specified in an
 * optional ClientConfiguration object when constructing a client.
 *
 * @see com.amazonaws.auth.BasicAWSCredentials
 * @see com.amazonaws.auth.PropertiesCredentials
 * @see com.amazonaws.ClientConfiguration
 */
public class AWSClientsConfiguration {	

	private static AWSCredentials credentials ;

	static{

		/* This credentials provider implementation loads your AWS credentials
		 * from a properties file at the root of your classpath.*/

		credentials = new BasicAWSCredentials(AppClientCredentials.getValue("accessKey"), 
				AppClientCredentials.getValue("secretKey"));

	}

	public static AWSCredentials getCredentials() {
		return credentials;
	}

	public static void setCredentials(AWSCredentials credentials) {
		AWSClientsConfiguration.credentials = credentials;
	}



}
