package com.psl.POC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.PauseResult;
import com.amazonaws.services.s3.transfer.PersistableDownload;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.CONFIGURATION.AmazonS3Basic;

public class MyDownloadPause {

	public static void main(String[] args) {
		
		
		AmazonS3Basic amazonS3Basic = new AmazonS3Basic();
		AmazonS3 amazonS3Client = AmazonS3Basic.getS3();
		
		String existingBucketName  = "onkaarbucket"; 
        String keyName             = "EXE_File";
        String filePath            = "C:\\Users\\onkaar_singh\\Downloads\\S3File"; 
        File myFile = new File(filePath);
        TransferManager manager = new TransferManager(amazonS3Client);
		
        
        //P A U S E     D O W N L O A D
		try
		{
			long MB = 1024 * 1024 ;
			
			Download download = manager.download(existingBucketName, keyName, myFile);
			TransferProgress progress = download.getProgress();
			System.out.println("The pause will occur once 1 MB of data is uploaded");
			while( progress.getBytesTransferred() < 0.03*MB )
			    {
				System.out.println("Data Transfered until now: " + download.getProgress().getBytesTransferred());
				Thread.sleep(500);
			    
			    }

			boolean forceCancel = true;
			float dataTransfered = (float) download.getProgress().getBytesTransferred();
			System.out.println("Data Transfered until now: " + dataTransfered);
			PersistableDownload persistableDownload = download.pause();
			
			
			System.out.println("Storing information into file");
			File f = new File("resume-download.txt");
			if( !f.exists() )
			    f.createNewFile();

			FileOutputStream fos = new FileOutputStream(f);
			persistableDownload.serialize(fos);
			fos.close();
			}
			
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("!!!...PAUSED FINALLY...!!!");
		
		
		try 
		{
			Thread.sleep(4000);
			System.out.println("Resuming...!!");
			InputStream fis = new FileInputStream(new File("resume-download.txt"));
			System.out.println("Reading information from the file"); 
			
			PersistableDownload persistableDownload;
			persistableDownload = PersistableTransfer.deserializeFrom(fis);
			System.out.println("Reading information completed");
			System.out.println(persistableDownload);
			System.out.println("The system will resume upload now");
			Download myDownload = manager.resumeDownload(persistableDownload);
			fis.close();
			
			while(myDownload.isDone()==false)
			{
				Thread.sleep(1000);
				System.out.println("Transfer: " + myDownload.getDescription());
			       System.out.println("  - State: " + myDownload.getState());
			       System.out.println("  - Progress: "
			                       + myDownload.getProgress().getBytesTransferred()/1000+"kb");
			       
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
