package com.aws.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.aws.utility.AwsS3Utility;


public class GetUniqueParents {

	public Map<String,List<String>> getUParents(AmazonS3 s3,String bucketName)
	{
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		List<S3ObjectSummary> s3Objects = s3.listObjects(bucketName).getObjectSummaries();
		
		for(S3ObjectSummary s: s3Objects)
		{
			String key = s.getKey();
			
			if(!key.contains("."))
				{
				ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(key).withDelimiter("/");
				List<String> list = s3.listObjects(lor).getCommonPrefixes();
				ObjectListing objectListing = s3.listObjects(lor);
				List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
					
				int i=0;
				for(S3ObjectSummary summary: s3ObjectSummaries)
					{
						if(i==0)
						{
							i++;
							continue;
						}
						String data[] = summary.getKey().split("/");
						String fileName = data[data.length-1];
						list.add(fileName);
					}
				map.put(key, list);
				}
		
		}
	return map;
	}
	
	public static void main(String[] args) {
		
		String accessKey = "AKIAIPSGWELRAGX527QQ";
        String secretKey = "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";
		AwsS3Utility awsS3Utility = new AwsS3Utility(accessKey, secretKey);
		
		AmazonS3 s3 = awsS3Utility.getS3();
		String bucketName = "onkaarbucket";
		
		GetUniqueParents getU = new GetUniqueParents();
		System.out.println(getU.getUParents(s3, bucketName));
	}

}
