package com.aws.main;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;
import com.amazonaws.services.s3.model.StorageClass;
import com.aws.utility.AwsS3Utility;

public class Glaciertest {

       public static void main(String[] args) {
              
    	   String accessKey = "AKIAIPSGWELRAGX527QQ";
    	   String secretKey = "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";
    	   AwsS3Utility awsS3Utility = new AwsS3Utility(accessKey, secretKey);
    	   AmazonS3 s3 = awsS3Utility.getS3();
           String bucketName="ashutoshjhapsl";
           
           Glaciertest glaciertest = new Glaciertest();
           glaciertest.GlacierTest(bucketName,s3);
    	    	 
           

       }
       
       public void GlacierTest(String bucketName, AmazonS3 s3)
       {
    	   Transition transition = new Transition()
           .withDays(0).withStorageClass(StorageClass.Glacier);
	   
     BucketLifecycleConfiguration.Rule ruleArchiveAndExpire = new BucketLifecycleConfiguration.Rule()
	    	    .withId("Archive immediately rule")
	    	    .withPrefix("new/")
	    	    .withTransition(transition)
	    	    .withExpirationInDays(5)
	    	    .withStatus(BucketLifecycleConfiguration.ENABLED.toString());
      
	List<BucketLifecycleConfiguration.Rule> rules = new ArrayList<BucketLifecycleConfiguration.Rule>();
	rules.add(ruleArchiveAndExpire);

	BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
	    .withRules(rules);

	// Save configuration.
	s3.setBucketLifecycleConfiguration(bucketName, configuration);  
	 System.out.println("Life cycle configurationn added to bucket successfully.");
       }

}

