package com.aws.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.aws.utility.AwsS3Utility;

public class Main {

	public static void main(String[] args) {
		
		  String accessKey = "AKIAIPSGWELRAGX527QQ";
	      String secretKey = "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";
	      AwsS3Utility s3Utility = new AwsS3Utility(accessKey, secretKey);
	      
	      //s3Utility.getAllBuckets();
		
	      String bucketName = "onkaarbucket";
	      
	      //System.out.println("Getting only folders in the next level. ");
	      System.out.println(s3Utility.getChildFolders(bucketName));
	      
	      //System.out.println("Files Names");
	      //System.out.println(s3Utility.getFilesOnly(bucketName, "new folder/new folder 2/new folder21/"));
	      //System.out.println(s3Utility.getChildFolders(bucketName, "new folder/Another Folder/"));
	      
	      List<S3ObjectSummary> s3objects = s3Utility.getS3ObjectSummaries(bucketName,"") ;
//	      for(S3ObjectSummary s: s3objects)
//	      {
//	    	  System.out.println("S3 Key..."+s.getKey()+"...Parent..."+s.getETag());
//	      }
	     // s3Utility.getUniqueParents(s3objects);
	   //   System.out.println(s3Utility.getUParents(s3objects,bucketName));
	     // System.out.println(s3Utility.getAll(bucketName, "new folder/"));
	
	      
	}

}
