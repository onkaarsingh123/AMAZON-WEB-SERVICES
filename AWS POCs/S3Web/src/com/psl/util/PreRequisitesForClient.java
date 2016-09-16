package com.psl.util;
import java.io.File;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

/*AWS Client needs to have the Access_key, Secret_key, proxy and port number to connect to AWS service. 
 * This class will set all these required information. 
 * 
 */
public class PreRequisitesForClient {
	private static BasicAWSCredentials awsCredentials;
	private static ClientConfiguration clientConfiguration;

	static {
		//PropertiesCredentials propertiesCredentials=PreRequisitesForClient.getCredentials();
		//PreRequisitesForClient.awsCredentials=new BasicAWSCredentials(propertiesCredentials.getAWSAccessKeyId(), propertiesCredentials.getAWSSecretKey());
		PreRequisitesForClient.clientConfiguration=PreRequisitesForClient.setClientConfiguration();
	}


	//proxy and port settings
	private static ClientConfiguration setClientConfiguration()
	{
		final String PROXY_HOST="ptproxy.persistent.co.in";
		final int PROXY_PORT=8080;
		ClientConfiguration clientConfiguration=new ClientConfiguration();
		clientConfiguration.setProxyHost(PROXY_HOST);
		clientConfiguration.setProxyPort(PROXY_PORT);
		return clientConfiguration;
	}

	//This method will read the access and secret key from the AwsCredentials.properties file.
	/*private static PropertiesCredentials getCredentials()
	{
		PropertiesCredentials credentials=null;
		try{
			credentials=new PropertiesCredentials(new File("AwsCredentials.properties"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return credentials;
	}*/

	public static BasicAWSCredentials getAwsCredentials() {
		return awsCredentials;
	}

	public static ClientConfiguration getClientConfiguration() {
		return clientConfiguration;
	}
}
