package com.psl.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.auth.PropertiesCredentials;

public class AppAdminCredentails {
	static final String ADMIN_FILE = "/AwsAdminCredentials.properties";
	static private Properties prop = new Properties();
	static private PropertiesCredentials prop1;

	static{
		try {

			InputStream input = (AppAdminCredentails.class).getResourceAsStream(ADMIN_FILE);

			if (input == null) 
				System.out.println("Sorry, unable to find " + ADMIN_FILE);

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
