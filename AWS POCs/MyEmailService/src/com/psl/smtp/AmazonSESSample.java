package com.psl.smtp;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class AmazonSESSample {

    static final String FROM = "onkaar_singh@persistent.co.in";   // Replace with your "From" address. This address must be verified.
    static final String TO = "onkaar_singh@persistent.co.in";  // Replace with a "To" address. If you have not yet requested
                                                       // production access, this address must be verified.
    
    static final String BODY = "Hi Onkaar, \n I am sending this email to my self. \n Thanks, \n Onkaar";
    static final String SUBJECT = "Sending Email through Amazon SES smtp";
    
    // Supply your SMTP credentials below. Note that your SMTP credentials are different from your AWS credentials.
    static final String SMTP_USERNAME = "AKIAIFSCQPTMRJD3747A";  // Replace with your SMTP username.
    static final String SMTP_PASSWORD = "AtJLISfWhFRhUT2NLqkZ2F7mT+irDlnFdX56Lq6YwntM";  // Replace with your SMTP password.
    
    // Amazon SES SMTP host name. This example uses the us-west-2 region.
    static final String HOST = "email-smtp.us-west-2.amazonaws.com";    
    
    // Port we will connect to on the Amazon SES SMTP endpoint. We are choosing port 25 because we will use
    // STARTTLS to encrypt the connection.
    static final int PORT = 25; //what about ssl port like 465 or 587 ?

    public static void main(String[] args) throws Exception {

    	
    	
    	
        // Create a Properties object to contain connection configuration information.
    	Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", PORT); 
    	
    	// Set properties indicating that we want to use STARTTLS to encrypt the connection.
    	// The SMTP session will begin on an unencrypted connection, and then the client
        // will issue a STARTTLS command to upgrade to an encrypted connection.
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.starttls.required", "true");
    	
    	

        // Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information. 
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
        msg.setSubject(SUBJECT);
        msg.setContent(BODY,"text/plain");
            
        // Create a transport.        
        Transport transport = session.getTransport();
                    
        // Send the message.
        try
        {
            System.out.println("Attempting to send an email through the Amazon SES SMTP interface...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
        	System.out.println("Connecting...!");
            
            
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        }
        catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();        	
        }
    }
}
