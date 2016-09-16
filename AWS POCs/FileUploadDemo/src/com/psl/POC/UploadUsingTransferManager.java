package com.psl.POC;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.PauseResult;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.CONFIGURATION.AmazonS3Basic;

/**
 * PAUSE NOT WORKING...!
 * @author onkaar_singh
 *
 */

public class UploadUsingTransferManager {

		public static void main(String[] args) {
		
			AmazonS3Basic amazonS3Basic = new AmazonS3Basic();
			AmazonS3 amazonS3Client = amazonS3Basic.getS3();
			
			String existingBucketName  = "onkaarbucket"; 
	        String keyName             = "onkarSingh.txt";
	        String filePath            = "C:\\Users\\onkaar_singh\\Downloads\\aws-toolkit-eclipse-guide.pdf"; 
	        File myFile = new File(filePath);
	        
			
			TransferManager manager = new TransferManager(amazonS3Client);
			Upload myUpload = manager.upload(existingBucketName, keyName, myFile);
			PauseResult<PersistableUpload> pauseResult  = null;
			
			 int count =0;
			// You can poll your transfer's status to check its progress
			while (myUpload.isDone() == false) {
				
				try 
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			       count++;
					System.out.println("Transfer: " + myUpload.getDescription());
			       System.out.println("  - State: " + myUpload.getState());
			       System.out.println("  - Progress: "
			                       + myUpload.getProgress().getBytesTransferred()/1000+"kb");
			       
			       if(count==6)
			       {
			    	   pauseResult = myUpload.tryPause(true);
			    	   System.out.println("PAUSED...!!");
			    	   System.out.println(pauseResult);
			    	   try {
						Thread.sleep(4000);
						System.out.println(myUpload.isDone());
						System.out.println("Transfer: " + myUpload.getDescription());
					       System.out.println("  - State: " + myUpload.getState());
					       System.out.println("  - Progress: "
					                       + myUpload.getProgress().getBytesTransferred()/1000+"kb");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	  
			       }
			 }
			
			
			

	}

}
