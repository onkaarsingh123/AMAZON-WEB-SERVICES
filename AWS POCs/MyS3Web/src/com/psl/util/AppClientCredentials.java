package com.psl.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.amazonaws.auth.PropertiesCredentials;

public class AppClientCredentials {
	static final String FIlE = "/AwsCredentials.properties";
	static private Properties prop = new Properties();
	static private PropertiesCredentials prop1;

	static{
		try {

			InputStream input = (AppClientCredentials.class).getResourceAsStream(FIlE);

			if (input == null) 
				System.out.println("Sorry, unable to find " + FIlE);

			prop.load(input);
			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}



	public static String getValue(String key){
		return prop.getProperty(key);
	}
}
