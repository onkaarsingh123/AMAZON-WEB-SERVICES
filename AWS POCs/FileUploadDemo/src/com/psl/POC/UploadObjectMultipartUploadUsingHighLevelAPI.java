package com.psl.POC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.transfer.PauseResult;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.CONFIGURATION.AmazonS3Basic;

public class UploadObjectMultipartUploadUsingHighLevelAPI {

	public void pauseUploading(TransferManager tm, Upload upload) throws Exception{
		long MB = 1024 * 1024 ;
    	TransferProgress progress = upload.getProgress();
    	System.out.println("The pause will occur once 5 MB of data is uploaded");
    	while( progress.getBytesTransferred() < 5*MB )
    		Thread.sleep(2000);
	
		boolean forceCancel = true;
    	float dataTransfered = (float) upload.getProgress().getBytesTransferred();
    	System.out.println("Data Transfered until now: " + dataTransfered);
    	PauseResult<PersistableUpload> pauseResult = ((Upload) upload).tryPause(forceCancel);
    	System.out.println("The upload has been paused. The code that we've got is " + pauseResult.getPauseStatus());
    	pauseResult = ((Upload) upload).tryPause(forceCancel);
    	PersistableUpload persistableUpload = (PersistableUpload) pauseResult.getInfoToResume();
    	System.out.println("Storing information into file");
    	File f = new File("resume-upload");
    	if( !f.exists() )
    		f.createNewFile();
    	
    	FileOutputStream fos = new FileOutputStream(f);
    	persistableUpload.serialize(fos);
    	fos.close();
	}
	
	
	public void resumeUploading(TransferManager tm) throws Exception{
		FileInputStream fis = new FileInputStream(new File("resume-upload"));
    	System.out.println("Reading information from the file"); 
    	PersistableUpload persistableUpload;
    	persistableUpload = PersistableTransfer.deserializeFrom(fis);
    	
    	System.out.println("Reading information completed");
    	System.out.println("The system will resume upload now");
    	Upload upload =tm.resumeUpload(persistableUpload);
    	while(!upload.isDone())
    	{
    		System.out.println("Progress... :" + upload.getProgress().getBytesTransferred());
    		Thread.sleep(2500);
    	}
    	fis.close();
//    	System.out.println("Upload complete.");
	}
	

	
	
	
    public static void main(String[] args) throws Exception {
    	String existingBucketName  = "onkaarbucket"; 
        String keyName             = "EXE_File1";
        String filePath            = "C:\\Users\\onkaar_singh\\Downloads\\S3File"; 
        
        
		AmazonS3 amazonS3Client = AmazonS3Basic.getS3();
      
        TransferManagerConfiguration configuration = new TransferManagerConfiguration();
        TransferManager tm = new TransferManager(amazonS3Client); 
        configuration.setMultipartUploadThreshold(1024 * 1024);
        tm.setConfiguration(configuration);
        System.out.println("************* Upload Manager *************");
        try {
        	Upload upload = tm.upload(existingBucketName, keyName, new File(filePath));        
		    System.out.println("Upload Started");
		    System.out.println("Transfer: " + upload.getDescription());
		    
		    UploadObjectMultipartUploadUsingHighLevelAPI multipartPause = 
		    		new UploadObjectMultipartUploadUsingHighLevelAPI();
		    multipartPause.pauseUploading(tm, upload);
		    System.out.println("PAUSED...!!");
		    Thread.sleep(3000);
		    
		    System.out.println("RESUME...!");
		    UploadObjectMultipartUploadUsingHighLevelAPI multipartResume = 
		    		new UploadObjectMultipartUploadUsingHighLevelAPI();
		    multipartResume.resumeUploading(tm);
        	
        	
		} 
        catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload was aborted.");
        	amazonClientException.printStackTrace();
        }
        catch (Exception e) {
			e.printStackTrace();
		}
    }
}

