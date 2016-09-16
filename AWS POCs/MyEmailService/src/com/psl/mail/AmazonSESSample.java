package com.psl.mail;

import java.io.IOException;



import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.*;

public class AmazonSESSample {
 
    static final String FROM = "onkaar_singh@persistent.co.in";  // Replace with your "From" address. This address must be verified.
    static final String TO = "onkaar_singh@persistent.co.in"; // Replace with a "To" address. If you have not yet requested
                                                      // production access, this address must be verified.
    static final String BODY = "Satyaki mail.";
    static final String SUBJECT = "Trying SNS Again";
  
    private static String accessKey = "AKIAIPSGWELRAGX527QQ";
    private static String secretKey = "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";
	
  

	static AWSCredentials credentials() 
	{
		 return new BasicAWSCredentials(accessKey, secretKey);
	}
    
    public static void main(String[] args) throws IOException {  
    	
    	
    	 final ClientConfiguration clientConfiguration;
    	 final String PROXY_HOST="ptproxy.persistent.co.in";
       	 final int PROXY_PORT=8080;
       	 
       	clientConfiguration = new ClientConfiguration();
		clientConfiguration.setProxyHost(PROXY_HOST);
		clientConfiguration.setProxyPort(PROXY_PORT);	
    	  
    	AWSCredentials credential = AmazonSESSample.credentials();
                
       // Construct an object to contain the recipient address.
        Destination destination = new Destination().withToAddresses(new String[]{TO});
        
        // Create the subject and body of the message.
        Content subject = new Content().withData(SUBJECT);
        Content textBody = new Content().withData(BODY); 
        Body body = new Body().withText(textBody);
        
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);
        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
        
        try
        {        
            System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
        
            // Instantiate an Amazon SES client, which will make the service call. The service call requires your AWS credentials. 
            // Because we're not providing an argument when instantiating the client, the SDK will attempt to find your AWS credentials 
            // using the default credential provider chain. The first place the chain looks for the credentials is in environment variables 
            // AWS_ACCESS_KEY_ID and AWS_SECRET_KEY. 
            // For more information, see http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
            
            
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credential,clientConfiguration);
            System.out.println("Client created");
            Region REGION = Region.getRegion(Regions.EU_WEST_1);
            client.setRegion(REGION);
            
           //The following code uses SNS to send notifications to confirm if a mail has been delivered properly.  
            SetIdentityNotificationTopicRequest identityNotificationTopicRequest =new SetIdentityNotificationTopicRequest();
            identityNotificationTopicRequest.setSnsTopic("arn:aws:sns:eu-west-1:409722190732:Topic_Onkar");
            identityNotificationTopicRequest.setIdentity("onkaar_singh@persistent.co.in");
            identityNotificationTopicRequest.setNotificationType(NotificationType.Delivery);
            client.setIdentityNotificationTopic(identityNotificationTopicRequest);
            
            // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your production 
            // access status, sending limits, and Amazon SES identity-related settings are specific to a given 
            // AWS region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using 
            // the US West (Oregon) region. Examples of other regions that Amazon SES supports are US_EAST_1 
            // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html 
            
            System.out.println("Region set");
           
       
       
            // Send the email.
            client.sendEmail(request);  
            System.out.println("Email sent!");
        }
        catch (Exception ex) 
        {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }
    }
}