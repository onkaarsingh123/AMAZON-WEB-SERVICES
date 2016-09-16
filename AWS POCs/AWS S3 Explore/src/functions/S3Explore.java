package functions;

import java.io.File;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Explore {
	
	public ClientConfiguration GetClientConfiguration(){
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTPS);
		clientConfig.setProxyHost("ngproxy.persistent.co.in");
		clientConfig.setProxyPort(8080);
		return clientConfig;
	}

	public void UploadFile(AmazonS3Client s3Client, File file, String bucketName, String keyName){
		
		s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));
		
	}
	
	public List<S3ObjectSummary> getAllObjects(AmazonS3Client s3, String bucketName, String prefix){
		List<S3ObjectSummary> s3objects = s3.listObjects(bucketName,prefix).getObjectSummaries();
		return s3objects;
	}
}
