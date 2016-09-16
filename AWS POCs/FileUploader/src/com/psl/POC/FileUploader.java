package com.psl.POC;

import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.CONFIGURATION.AmazonS3Basic;

public class FileUploader {

	public void uploadFile(AmazonS3 s3,String bucketName, File file)
	{
		String keyName = file.getName();
		
		TransferManager manager = new TransferManager(s3);
		Upload upload = manager.upload(bucketName, keyName, file);
		TransferProgress progress = upload.getProgress();
		
		while(!upload.isDone())
		{
			System.out.println(progress.getBytesTransferred()+"  bytes transferred");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception...! ");
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) 
	{
	AmazonS3Basic  s3Basic = new AmazonS3Basic();
	AmazonS3 amazonS3 = s3Basic.getS3();
	
	FileUploader fileUploader = new FileUploader();
	
	String bucketName="";
	File file = new File("");
	fileUploader.uploadFile(amazonS3, bucketName, file);

	}

}
