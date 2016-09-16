package com.psl.amazon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsS3Utility 
{
	static AmazonS3 s3;
	
	private final ClientConfiguration clientConfiguration;
	private final String PROXY_HOST="ptproxy.persistent.co.in";
   	private final int PROXY_PORT=8080;	
    
	public AwsS3Utility(String accessKey, String secretKey) {
		clientConfiguration = new ClientConfiguration();
		clientConfiguration.setProxyHost(PROXY_HOST);
		clientConfiguration.setProxyPort(PROXY_PORT);	
		
		s3 =  new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey), clientConfiguration);
	}
	
	
	/**
	 * This method returns all the buckets present in S3.
	 */
	
	public List<Bucket> getAllBuckets()
	{
		return s3.listBuckets();
	}
	
	
	/**
	 * This method returns all the folders in the bucket.
	 */
	public List<String> getChildFolders(String bucketName)
	{
		String prefix = "";
		ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		List<String> list = s3.listObjects(lor).getCommonPrefixes();
		
		return list;
	}
	
	/**
	 * This method returns all the sub folders inside a bucket.
	 */
	public List<String> getChildFolders(String bucketName, String prefix)
	{
		ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		List<String> list = s3.listObjects(lor).getCommonPrefixes();
		
		return list;
	}
	
	
	
	
	/**
	 * This method returns only files within a folder and not the sub folders.
	 */
	public List<String> getFilesOnly(String bucketName, String prefix)
	{
		 ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		 ObjectListing objectListing = s3.listObjects(lor);
		 
	
		 List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
		 List<String> fileNames = getFileName(s3ObjectSummaries);
		
		return fileNames;
	}
	
	public List<String> getAllFilesAndFolders(String bucketName, String prefix)
	{
		List<String> list = new ArrayList<String>();
		list.add("Folders...\n");
		list.addAll(getChildFolders(bucketName, prefix));
		list.add("\nFiles...\n");
		list.addAll(getFilesOnly(bucketName, prefix));
		
		return list;
	}
	
	
	
	/**
	 * This is a private method that will return a list of file names from the list of S3ObjectSummarry objects. 
	 */
	private List<String> getFileName(List<S3ObjectSummary> objectSummaries)
	{
		List<String> list = new ArrayList<String>();
		Iterator<S3ObjectSummary> it = objectSummaries.iterator();
		int i=0;
			while(it.hasNext())
			{
				S3ObjectSummary summary = it.next();
				if(i==0)
					{
					i++;
					continue;
					}
				
				String data[] = summary.getKey().split("/");
				String fileName = data[data.length-1];
				list.add(fileName);
			}
		return list;
	}

	 


}
