import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.StringUtils;


public class AmazonS3Basics {
	
 
    static AmazonS3 s3;
	private static String accessKey = "AKIAIPSGWELRAGX527QQ";
    private static String secretKey = "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";
	
	private final ClientConfiguration clientConfiguration;
	private final String PROXY_HOST="ptproxy.persistent.co.in";
   	private final int PROXY_PORT=8080;	
 
    
	public AmazonS3Basics() {
		clientConfiguration = new ClientConfiguration();
		clientConfiguration.setProxyHost(PROXY_HOST);
		clientConfiguration.setProxyPort(PROXY_PORT);		
	}

	private static AWSCredentials credentials() {

		return new BasicAWSCredentials(accessKey, secretKey);
	}

   	
   	public static void main(String[] args) {
   		
   		AmazonS3Basics amazonS3Basics = new AmazonS3Basics();
   		AWSCredentials credentials = AmazonS3Basics.credentials();
   		
   		s3 =  new AmazonS3Client(credentials, amazonS3Basics.clientConfiguration);
   		String bucketName="snehalpatil";
   		String prefix = "";
   		

   		
		ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		ObjectListing objectListing = s3.listObjects(lor);
		List<String> listFolder =objectListing.getCommonPrefixes();
		

		System.out.println("  List Folder... " + listFolder);
		for(String s : listFolder)
		{
			System.out.println("S... "+s);
			System.out.println("Inside S3 bucket...."+s.trim()+"   bucket name..." + bucketName);
			S3Object object = s3.getObject(new GetObjectRequest(bucketName, s.trim()));
			
			System.out.println(object);
			
		}
   		
//   		List<Bucket>  buckets = s3.listBuckets();								//Get all the S3 buckets which are owned.
//   		
//   		Bucket backet = buckets.get(0);
//   		for(Bucket bucket: buckets )
//   		{
//   			System.out.println("Bucket Name :" + bucket.getName()+ "\t" +
//   	                StringUtils.fromDate(bucket.getCreationDate()));
//   		}
//   		
//   		System.out.println("Number Of Buckets  : " + buckets.size() );
//   		
//   		//System.out.println("Creating Bucket...!!!");
//   		//Bucket bucket =  s3.createBucket("onkaarbucket");
//   		
//   		System.out.println("Number Of Buckets  : " + buckets.size() );
//   		
//   		ObjectListing objects = s3.listObjects(backet.getName());
//   		do {
//   		        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
//   		                System.out.println(objectSummary.getKey() + "\t" + objectSummary.getBucketName()+"\t" +
//   		                        objectSummary.getSize() + "\t" +
//   		                        StringUtils.fromDate(objectSummary.getLastModified()));
//   		        }
//   		        objects = s3.listNextBatchOfObjects(objects);
//   		} while (objects.isTruncated());
//   		
//   		
//   		//s3.deleteBucket("onkaarbucket");
//   		
//   		ByteArrayInputStream input = new ByteArrayInputStream("So i am just trying to create a file within the bucket. Seriously ?".getBytes());
//   		s3.putObject("onkaarbucket", "hello.txt", input, new ObjectMetadata());
//   		
//   		s3.setObjectAcl("onkaarbucket","hello.txt", CannedAccessControlList.Private);
//   	
//   		System.out.println("Reading Onkaar Bucket");
//   		
//   		objects = s3.listObjects("onkaarbucket");
//   		do {
//   		        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
//   		                System.out.println(objectSummary.getKey() + "\t" + objectSummary.getBucketName()+"\t" +
//   		                        objectSummary.getSize() + "\t" +
//   		                        StringUtils.fromDate(objectSummary.getLastModified()));
//   		        }
//   		        objects = s3.listNextBatchOfObjects(objects);
//   		} while (objects.isTruncated());
//   		
//   		System.out.println("******************************************************************");
//   		
//   		File file = new File("C:/Users/onkaar_singh/Downloads/GiftsLog.xlsx");
//   		TransferManager manager = new TransferManager(s3) ;
//   		Upload upload = manager.upload("onkaarbucket", "EC2.pdf",file);
//   		
//   		while (upload.isDone() == false ) {
//   			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	
//   	       System.out.println("Transfer: " + upload.getDescription());
//   	       System.out.println("  - State: " + upload.getState());
//   	       System.out.println("  - Progress: "
//   	                       + upload.getProgress().getBytesTransferred());
//   	       
//   	 }
//   		Download download = manager.download("onkaarbucket", "hello.txt", file);
//   	
//   		while(download.isDone() == false)
//   		{try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
//	       System.out.println("Transfer: " + download.getDescription());
//	       System.out.println("  - State: " + download.getState());
//	       System.out.println("  - Progress: "
//	                       + download.getProgress().getBytesTransferred());
//	       
//	 }
   	}	
   	

}
