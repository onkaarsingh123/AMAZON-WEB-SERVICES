package com.psl.main;

import com.psl.util.AwsS3Utililty;

public class Main {
	public static void main(String[] args) {
	
			AwsS3Utililty awsS3Utililty = new AwsS3Utililty();
			String data = 	awsS3Utililty.getObjectMetaData("onkaarbucket");
		
			System.out.println(data);
	}
}
