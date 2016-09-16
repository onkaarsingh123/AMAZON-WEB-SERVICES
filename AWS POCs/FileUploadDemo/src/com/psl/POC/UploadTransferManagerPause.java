package com.psl.POC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.PauseResult;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.CONFIGURATION.AmazonS3Basic;

/**
 * PAUSE  WORKING...!
 * @author onkaar_singh
 *
 */

public class UploadTransferManagerPause {

		public static void main(String[] args) {
		
			AmazonS3Basic amazonS3Basic = new AmazonS3Basic();
			AmazonS3 amazonS3Client = AmazonS3Basic.getS3();
			String existingBucketName  = "onkaarbucket"; 
	        String keyName             = "Big-DATA.zip";
	        String filePath            = "C:\\Users\\onkaar_singh\\Downloads\\Amazon CloudWatch Developer Guide.pdf"; 
	        File myFile = new File(filePath);
	        TransferManager manager = new TransferManager(amazonS3Client);
			
	        
	        //P A U S E     U P L O A D
			try
			{
				long MB = 1024 * 1024 ;
				
				Upload upload = manager.upload(existingBucketName, keyName, myFile);
				TransferProgress progress = upload.getProgress();
				System.out.println("The pause will occur once 50 MB of data is uploaded");
				while( progress.getBytesTransferred() < 50*MB )
				    {
					System.out.println("Data Transfered until now: " + upload.getProgress().getBytesTransferred()/MB+"Mb");
					Thread.sleep(500);
				    
				    }

				boolean forceCancel = true;
				float dataTransfered = (float) upload.getProgress().getBytesTransferred();
				System.out.println("Data Transfered until now: " + dataTransfered);
				PauseResult<PersistableUpload> pauseResult = ((Upload) upload).tryPause(forceCancel);
				System.out.println("The upload has been paused. The code that we've got is " +   pauseResult.getPauseStatus());
				
				PersistableUpload persistableUpload = (PersistableUpload) pauseResult.getInfoToResume();
				System.out.println("Storing information into file");
				File f = new File("resume-upload.txt");
				if( !f.exists() )
				    f.createNewFile();

				FileOutputStream fos = new FileOutputStream(f);
				persistableUpload.serialize(fos);
				
				
				fos.close();
				}
				
			
			catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("!!!...PAUSED FINALLY...!!!");
			
			//R E S U M E     U P L O A D
			try 
			{
				Thread.sleep(4000);
				System.out.println("Resuming...!!");
				InputStream fis = new FileInputStream(new File("resume-upload.txt"));
				System.out.println("Reading information from the file"); 
				PersistableUpload persistableUpload;
				persistableUpload = PersistableTransfer.deserializeFrom(fis);
				System.out.println("Reading information completed");
				System.out.println(persistableUpload);
				System.out.println("The system will resume upload now");
				
				Upload myUpload = manager.resumeUpload(persistableUpload);
				fis.close();
				
				while(myUpload.isDone()==false)
				{
					Thread.sleep(1000);
					System.out.println("Transfer: " + myUpload.getDescription());
				       System.out.println("  - State: " + myUpload.getState());
				       System.out.println("  - Progress: "
				                       + myUpload.getProgress().getBytesTransferred()/1000+"kb");
				       
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
