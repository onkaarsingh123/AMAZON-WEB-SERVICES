package main;

import java.io.File;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import functions.S3Explore;

public class MainClass {

	public static void main(String[] args) {
		
		S3Explore s3Explore = new S3Explore();
		AmazonS3Client s3Client = new AmazonS3Client(s3Explore.GetClientConfiguration());
		//String pathName = "D:\\Project Tools\\S3Web.7z"; 
		//String bucketName = "onkaarbucket";
		//String keyName = "new folder/S3Zip";
		
		//File file = new File(pathName);
		
		String bucketName = "onkaarbucket";
		String prefix = "";
		
		
		try
		{
			System.out.println("Fetching list...!!!");
			//s3Explore.UploadFile(s3Client, file, bucketName, keyName);
			 List<S3ObjectSummary> summaries = s3Explore.getAllObjects(s3Client, bucketName, prefix);
			 for (S3ObjectSummary summary : summaries) {
		            String summaryKey = summary.getKey();
		            System.out.println(summaryKey);
		        }
		}
		catch(AmazonServiceException exception)
		{
			System.out.println("Exception Occurred  !!" + exception);
		}
		
		
		System.out.println("Displayed Successfull !!!");

	}

}
