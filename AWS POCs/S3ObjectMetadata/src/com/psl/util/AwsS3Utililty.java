package com.psl.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringUtils;
import com.psl.config.AwsConfig;

public class AwsS3Utililty {
	
	AwsConfig awsConfig = new AwsConfig();
	AmazonS3 amazonS3 = AwsConfig.getS3();
	
	public String getObjectMetaData(String bucketName)
	{
			//String prefix = "";
			//ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		String data = "";
		ObjectListing objectListing = amazonS3.listObjects(bucketName);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) 
			{
                data = data+objectSummary.getKey() + "\t\t\t" + objectSummary.getSize() + "\t\t\t" + StringUtils.fromDate(objectSummary.getLastModified())+"\n";
			}
		 
		return data;
	}
	
	
	

}
