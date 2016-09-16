package com.aws.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	
	public static AmazonS3 getS3() {
		return s3;
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
		List<String> list = new ArrayList<>();
		list.add("Folders...\n");
		list.addAll(getChildFolders(bucketName, prefix));
		list.add("\nFiles...\n");
		list.addAll(getFilesOnly(bucketName, prefix));
		
		return list;
	}
	
	public Map<String,List<String>> getAll(String bucketName, String prefix)
	{
		ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		List<String> listFolder = s3.listObjects(lor).getCommonPrefixes();
		
		List<String> listFile = new ArrayList<>();
		
		Map<String, List<String>> map = new HashMap<>();
		
		ObjectListing objectListing = s3.listObjects(lor);
		List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
		 Iterator<S3ObjectSummary> it = s3ObjectSummaries.iterator();
			int i=0;
				while(it.hasNext())
				{
					S3ObjectSummary summary = it.next();
					if(i==0)
						{
						i++;
						continue;
						}

					String fileName = summary.getKey();
					listFile.add(fileName);
				}
		map.put("folder", listFolder);
		map.put("file", listFile);
		return map;
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
	
	
	public List<S3ObjectSummary> getS3ObjectSummaries(String bucketName, String prefix){
		
		List<S3ObjectSummary> s3objects = s3.listObjects(bucketName,prefix).getObjectSummaries();
		return s3objects;
	}
	
	public Map<String,List<String>> getUParents(List<S3ObjectSummary> s3Objects, String bucketName)
	{
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		
		for(S3ObjectSummary s: s3Objects)
		{
			String key = s.getKey();
			
			if(!key.contains("."))
				{
				List<String> list = getChildFolders(bucketName, key);
				list.addAll(getFilesOnly(bucketName, key));
				map.put(key, list);
				}
		
		}
		return map;
	}
	
	public Map<String,List<String>> getUniqueParents(List<S3ObjectSummary> s3Objects)
	{

	     Map<String,List<String>> map = new HashMap<>();
		for(S3ObjectSummary s: s3Objects)
	      {
	    	String key = s.getKey();  
				if(key.contains("/")&&(countCharacters(key) > 1))
				{

				  System.out.println("key...."+key);
	    		  int i = key.lastIndexOf("/");
	    		  String parent = key.substring(0, i);
	    		  String child = key.substring(i+1,key.length());
	    		  List<String > childList;
	    		  if(map.containsKey(parent))
	    		  {
	    		  childList = map.get(parent);
	    		  childList.add(child);
	    		  }
	    		  else
	    		  {
	    			childList = new ArrayList<>();
		    		childList.add(child);
	    		  }
	    		  map.put(parent, childList);
				}
				
				else if(key.contains(".")||(key.lastIndexOf("/")==key.length()-1))
				{

					List<String > childList;
					if(map.containsKey("ROOT_FILE"))
		    		  {
		    		  childList = map.get("ROOT_FILE");
		    		  childList.add(key);
		    		  }
		    		  else
		    		  {
		    			childList = new ArrayList<>();
			    		childList.add(key);
		    		  }
					map.put("ROOT_FILE", childList);
					
				}
	    }
		
		
		return map;
	}
	
	private int countCharacters(String input)
	{
		int charCount = 0;
        for(int i =0 ; i<input.length(); i++){
            if(input.charAt(i) == '/'){
                charCount++;
            }
        }
        return charCount;
	}

	 


}
