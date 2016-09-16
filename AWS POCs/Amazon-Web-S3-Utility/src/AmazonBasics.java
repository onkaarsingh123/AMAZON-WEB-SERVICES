import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;


public class AmazonBasics {
	
	static AmazonS3 s3;
	private static String accessKey = "AKIAIPSGWELRAGX527QQ";
    private static String secretKey = "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";
	
	private final ClientConfiguration clientConfiguration;
	private final String PROXY_HOST="ptproxy.persistent.co.in";
   	private final int PROXY_PORT=8080;	
    
	public AmazonBasics() {
		clientConfiguration = new ClientConfiguration();
		clientConfiguration.setProxyHost(PROXY_HOST);
		clientConfiguration.setProxyPort(PROXY_PORT);	
		
		s3 =  new AmazonS3Client(credentials(), clientConfiguration);
	}

	 static AWSCredentials credentials() {

		return new BasicAWSCredentials(accessKey, secretKey);
	}
	
	public List<Bucket> getAllBuckets()
	{
		List<Bucket>  buckets = s3.listBuckets();								//Get all the S3 buckets which are owned.
   		
   		
   		return buckets;
   		
	}

	public static void main(String[] args) {
		
		AmazonBasics  amazonBasics = new AmazonBasics();
		System.out.println("*********Displaying all Buckets*********** ");
		System.out.println(amazonBasics.getAllBuckets());
		
		

	}

}
