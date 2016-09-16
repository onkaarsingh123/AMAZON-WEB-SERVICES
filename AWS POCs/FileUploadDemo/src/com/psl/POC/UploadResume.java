package com.psl.POC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.CONFIGURATION.AmazonS3Basic;

public class UploadResume {

	
	

	
	public static void main(String[] args) {
		
		
		try 
		{
			AmazonS3Basic amazonS3Basic = new AmazonS3Basic();
			 AmazonS3 amazonS3Client = AmazonS3Basic.getS3();
			Thread.sleep(4000);
			System.out.println("Resuming...!!");
			InputStream fis = new FileInputStream(new File("resume-upload.txt"));
			System.out.println("Reading information from the file"); 
			PersistableUpload persistableUpload;
			persistableUpload = PersistableTransfer.deserializeFrom(fis);
			System.out.println("Reading information completed");
			System.out.println(persistableUpload);
			System.out.println("The system will resume upload now");
			long MB = 1024 * 1024 ;
			
			TransferManager manager = new TransferManager(amazonS3Client);
			
			Upload myUpload = manager.resumeUpload(persistableUpload);
			fis.close();
			
			while(myUpload.isDone()==false)
			{
				Thread.sleep(1000);
				System.out.println("Transfer: " + myUpload.getDescription());
			       System.out.println("  - State: " + myUpload.getState());
			       System.out.println("  - Progress: "
			                       + myUpload.getProgress().getBytesTransferred()/MB+"MB");
			       
			}
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
