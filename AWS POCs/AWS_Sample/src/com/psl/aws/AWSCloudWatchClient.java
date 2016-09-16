package com.psl.aws;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.*;
import java.text.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateVolumeRequest;
import com.amazonaws.services.ec2.model.CreateVolumeResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.Dimension;

class AWSCloudWatchClient {

	static KeyPair keyPair;
    static int count = 1;    
    static AmazonEC2 ec2 ;
	private static String accessKey = "AKIAJEW4GNJLWOZ6BDDA";
    private static String secretKey = "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";
	private AmazonCloudWatchClient cw;
	private static ArrayList<String> endPointList = null;
	private static ArrayList<String> endPointMonitoringList = null;
	
	private final ClientConfiguration clientConfiguration;
	private final String PROXY_HOST="ptproxy.persistent.co.in";
   	private final int PROXY_PORT=8080;	
 
    static {

		endPointList = new ArrayList<String>();
		endPointList.add("ec2.us-east-1.amazonaws.com");	
		endPointList.add("ec2.us-west-2.amazonaws.com");
		endPointList.add("ec2.us-west-1.amazonaws.com");
		endPointList.add("ec2.eu-west-1.amazonaws.com");
		endPointList.add("ec2.eu-central-1.amazonaws.com");
		endPointList.add("ec2.ap-southeast-1.amazonaws.com");	
		endPointList.add("ec2.ap-southeast-2.amazonaws.com");	
		endPointList.add("ec2.ap-northeast-1.amazonaws.com");	
		endPointList.add("ec2.sa-east-1.amazonaws.com");

		endPointMonitoringList = new ArrayList<String>();
		endPointMonitoringList.add("monitoring.us-east-1.amazonaws.com");	
		endPointMonitoringList.add("monitoring.us-west-2.amazonaws.com");
		endPointMonitoringList.add("monitoring.us-west-1.amazonaws.com");
		endPointMonitoringList.add("monitoring.eu-west-1.amazonaws.com");
		endPointMonitoringList.add("monitoring.eu-central-1.amazonaws.com");
		endPointMonitoringList.add("monitoring.ap-southeast-1.amazonaws.com");	
		endPointMonitoringList.add("monitoring.ap-southeast-2.amazonaws.com");	
		endPointMonitoringList.add("monitoring.ap-northeast-1.amazonaws.com");	
		endPointMonitoringList.add("monitoring.sa-east-1.amazonaws.com");		
	}

	public AWSCloudWatchClient() {
		clientConfiguration = new ClientConfiguration();
		clientConfiguration.setProxyHost(PROXY_HOST);
		clientConfiguration.setProxyPort(PROXY_PORT);		
	}

	private static AWSCredentials credentials() {

		return new BasicAWSCredentials(accessKey, secretKey);
	}

	private static AmazonCloudWatchClient getAmazonCloudWatchClient(AWSCredentials credentials, ClientConfiguration clientConfig, ArrayList<String> endPoints) {
        
		AmazonCloudWatchClient awsCloudWatchClient = new AmazonCloudWatchClient(credentials, clientConfig);
        //awsCloudWatchClient.setEndpoint("monitoring.us-west-2.amazonaws.com");
		
		for(String endPoint : endPoints){
			awsCloudWatchClient.setEndpoint(endPoint);  
		}		
		return awsCloudWatchClient;
	}

	private void getCPUUtilization(AmazonCloudWatchClient cw, String instanceId, Date startDate, Date endDate) {	 

	   try {

		   long offsetInMilliseconds = 1000 * 60 * 60 * 24;
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
			   .withStartTime(new Date(startDate.getTime()))
			   .withNamespace("AWS/EC2")
			   .withPeriod(60 * 60 * 24)
			   .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
			   .withMetricName("CPUUtilization")
			   .withStatistics("Average")
			   .withEndTime(new Date(endDate.getTime()));

		   GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

		   double avgCPUUtilization = 0;
		   Date timestamp = null;
		   List dataPoint = getMetricStatisticsResult.getDatapoints();
		   int count = 1; 		   
		   for (Object aDataPoint : dataPoint) {
			   Datapoint dp = (Datapoint) aDataPoint;
			   avgCPUUtilization = dp.getAverage();	
			   timestamp = dp.getTimestamp(); 
			   System.out.println("CPU Usage"+count+++" of "+instanceId+" is "+dp.getAverage()+" at "+timestamp);	
		   }
		   //System.out.println(instanceId + " instance's average CPU utilization : " 
		   //  + avgCPUUtilization);
		   //return avgCPUUtilization;

	   } catch (AmazonServiceException ase) {
		   System.out.println("Caught an AmazonServiceException, which means the request was made  "
			   + "to Amazon EC2, but was rejected with an error response for some reason.");
		   System.out.println("Error Message:    " + ase.getMessage());
		   System.out.println("HTTP Status Code: " + ase.getStatusCode());
		   System.out.println("AWS Error Code:   " + ase.getErrorCode());
		   System.out.println("Error Type:       " + ase.getErrorType());
		   System.out.println("Request ID:       " + ase.getRequestId());
	   }	  
	}

	private void getNetworkIn(AmazonCloudWatchClient cw, String instanceId) {

	   try {

		   long offsetInMilliseconds = 1000 * 60 * 60 * 24;
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
			   .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
			   .withNamespace("AWS/EC2")
			   .withPeriod(60 * 60 * 24)
			   .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
			   .withMetricName("NetworkIn")
			   .withStatistics("Average")
			   .withEndTime(new Date());
		   GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

		   double avgCPUUtilization = 0;
		   List dataPoint = getMetricStatisticsResult.getDatapoints();
		  
		   for (Object aDataPoint : dataPoint) {
			   Datapoint dp = (Datapoint) aDataPoint;
			   avgCPUUtilization = dp.getAverage();	
			   System.out.println("NetworkIn of "+instanceId+" :: "+dp.getAverage());	
		   }
		   //System.out.println(instanceId + " instance's average CPU utilization : " 
		   //  + avgCPUUtilization);
		   //return avgCPUUtilization;

	   } catch (AmazonServiceException ase) {
		   System.out.println("Caught an AmazonServiceException, which means the request was made  "
		   + "to Amazon EC2, but was rejected with an error response for some reason.");
		   System.out.println("Error Message:    " + ase.getMessage());
		   System.out.println("HTTP Status Code: " + ase.getStatusCode());
		   System.out.println("AWS Error Code:   " + ase.getErrorCode());
		   System.out.println("Error Type:       " + ase.getErrorType());
		   System.out.println("Request ID:       " + ase.getRequestId());
	   }	  
	}

	private void getNetworkOut(AmazonCloudWatchClient cw, String instanceId) {

	   try {

		   long offsetInMilliseconds = 1000 * 60 * 60 * 24;
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
			   .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
			   .withNamespace("AWS/EC2")
			   .withPeriod(60 * 60 * 24)
			   .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
			   .withMetricName("NetworkOut")
			   .withStatistics("Average")
			   .withEndTime(new Date());
		   GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

		   double avgCPUUtilization = 0;
		   List dataPoint = getMetricStatisticsResult.getDatapoints();
		  
		   for (Object aDataPoint : dataPoint) {
			   Datapoint dp = (Datapoint) aDataPoint;
			   avgCPUUtilization = dp.getAverage();	
			   System.out.println("NetworkOut of "+instanceId+" :: "+dp.getAverage());	
		   }
		   //System.out.println(instanceId + " instance's average CPU utilization : " 
		   //  + avgCPUUtilization);
		   //return avgCPUUtilization;

	   } catch (AmazonServiceException ase) {
		   System.out.println("Caught an AmazonServiceException, which means the request was made  "
			   + "to Amazon EC2, but was rejected with an error response for some reason.");
		   System.out.println("Error Message:    " + ase.getMessage());
		   System.out.println("HTTP Status Code: " + ase.getStatusCode());
		   System.out.println("AWS Error Code:   " + ase.getErrorCode());
		   System.out.println("Error Type:       " + ase.getErrorType());
		   System.out.println("Request ID:       " + ase.getRequestId());
	   }	  
	}

	private void getDiskReadBytes(AmazonCloudWatchClient cw, String instanceId) {

	   try {

		   long offsetInMilliseconds = 1000 * 60 * 60 * 24;
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
			   .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
			   .withNamespace("AWS/EC2")
			   .withPeriod(60 * 60 * 24)
			   .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
			   .withMetricName("DiskReadBytes")
			   .withStatistics("Average")
			   .withEndTime(new Date());
		   GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

		   double avgCPUUtilization = 0;
		   List dataPoint = getMetricStatisticsResult.getDatapoints();
		  
		   for (Object aDataPoint : dataPoint) {
			   Datapoint dp = (Datapoint) aDataPoint;
			   avgCPUUtilization = dp.getAverage();	
			   System.out.println("DiskReadBytes of "+instanceId+" :: "+dp.getAverage());	
		   }
		   //System.out.println(instanceId + " instance's average CPU utilization : " 
		   //  + avgCPUUtilization);
		   //return avgCPUUtilization;

	   } catch (AmazonServiceException ase) {
		   System.out.println("Caught an AmazonServiceException, which means the request was made  "
			   + "to Amazon EC2, but was rejected with an error response for some reason.");
		   System.out.println("Error Message:    " + ase.getMessage());
		   System.out.println("HTTP Status Code: " + ase.getStatusCode());
		   System.out.println("AWS Error Code:   " + ase.getErrorCode());
		   System.out.println("Error Type:       " + ase.getErrorType());
		   System.out.println("Request ID:       " + ase.getRequestId());
	   }	  
	}

	private void getDiskWriteBytes(AmazonCloudWatchClient cw, String instanceId) {

	   try {

		   long offsetInMilliseconds = 1000 * 60 * 60 * 24;
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
			   .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
			   .withNamespace("AWS/EC2")
			   .withPeriod(60 * 60 * 24)
			   .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
			   .withMetricName("DiskWriteBytes")
			   .withStatistics("Average")
			   .withEndTime(new Date());
		   GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

		   double avgCPUUtilization = 0;
		   List dataPoint = getMetricStatisticsResult.getDatapoints();
		  
		   for (Object aDataPoint : dataPoint) {
			   Datapoint dp = (Datapoint) aDataPoint;
			   avgCPUUtilization = dp.getAverage();	
			   System.out.println("DiskWriteBytes of "+instanceId+" :: "+dp.getAverage());	
		   }
		   //System.out.println(instanceId + " instance's average CPU utilization : " 
		   //  + avgCPUUtilization);
		   //return avgCPUUtilization;

	   } catch (AmazonServiceException ase) {
		   System.out.println("Caught an AmazonServiceException, which means the request was made  "
			   + "to Amazon EC2, but was rejected with an error response for some reason.");
		   System.out.println("Error Message:    " + ase.getMessage());
		   System.out.println("HTTP Status Code: " + ase.getStatusCode());
		   System.out.println("AWS Error Code:   " + ase.getErrorCode());
		   System.out.println("Error Type:       " + ase.getErrorType());
		   System.out.println("Request ID:       " + ase.getRequestId());
	   }	  
	}

	private void getDiskReadOps(AmazonCloudWatchClient cw, String instanceId) {

	   try {

		   long offsetInMilliseconds = 1000 * 60 * 60 * 24;
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
			   .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
			   .withNamespace("AWS/EC2")
			   .withPeriod(60 * 60 * 24)
			   .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
			   .withMetricName("DiskReadOps")
			   .withStatistics("Average")
			   .withEndTime(new Date());
		   GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

		   double avgCPUUtilization = 0;
		   List dataPoint = getMetricStatisticsResult.getDatapoints();
		  
		   for (Object aDataPoint : dataPoint) {
			   Datapoint dp = (Datapoint) aDataPoint;
			   avgCPUUtilization = dp.getAverage();	
			   System.out.println("DiskReadOps of "+instanceId+" :: "+dp.getAverage());	
		   }
		   //System.out.println(instanceId + " instance's average CPU utilization : " 
		   //  + avgCPUUtilization);
		   //return avgCPUUtilization;

	   } catch (AmazonServiceException ase) {
		   System.out.println("Caught an AmazonServiceException, which means the request was made  "
			   + "to Amazon EC2, but was rejected with an error response for some reason.");
		   System.out.println("Error Message:    " + ase.getMessage());
		   System.out.println("HTTP Status Code: " + ase.getStatusCode());
		   System.out.println("AWS Error Code:   " + ase.getErrorCode());
		   System.out.println("Error Type:       " + ase.getErrorType());
		   System.out.println("Request ID:       " + ase.getRequestId());
	   }	  
	}

	private void getDiskWriteOps(AmazonCloudWatchClient cw, String instanceId) {

	   try {

		   long offsetInMilliseconds = 1000 * 60 * 60 * 24;
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
			   .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
			   .withNamespace("AWS/EC2")
			   .withPeriod(60 * 60 * 24)
			   .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
			   .withMetricName("DiskWriteOps")
			   .withStatistics("Average")
			   .withEndTime(new Date());
		   GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

		   double avgCPUUtilization = 0;
		   List dataPoint = getMetricStatisticsResult.getDatapoints();
		  
		   for (Object aDataPoint : dataPoint) {
			   Datapoint dp = (Datapoint) aDataPoint;
			   avgCPUUtilization = dp.getAverage();	
			   System.out.println("DiskWriteOps of "+instanceId+" :: "+dp.getAverage());	
		   }
		   //System.out.println(instanceId + " instance's average CPU utilization : " 
		   //  + avgCPUUtilization);
		   //return avgCPUUtilization;

	   } catch (AmazonServiceException ase) {
		   System.out.println("Caught an AmazonServiceException, which means the request was made  "
			   + "to Amazon EC2, but was rejected with an error response for some reason.");
		   System.out.println("Error Message:    " + ase.getMessage());
		   System.out.println("HTTP Status Code: " + ase.getStatusCode());
		   System.out.println("AWS Error Code:   " + ase.getErrorCode());
		   System.out.println("Error Type:       " + ase.getErrorType());
		   System.out.println("Request ID:       " + ase.getRequestId());
	   }	  
	}

	public static void main(String[] args) throws Exception {
	  
	  AWSCredentials credentials = AWSCloudWatchClient.credentials();	  
	  AWSCloudWatchClient awsCldWtchClntObj = new AWSCloudWatchClient();
	  System.out.println("Create Amazon Client object");
	  ec2 = new AmazonEC2Client(credentials, awsCldWtchClntObj.clientConfiguration);	 
	  AmazonCloudWatchClient cw = AWSCloudWatchClient.getAmazonCloudWatchClient(credentials, awsCldWtchClntObj.clientConfiguration, endPointMonitoringList);

	  try {		  
		    // Describe Current Instances
			System.out.println("Describe Current Instances");	

			ArrayList<Reservation> reservations = new ArrayList<Reservation>();
			
			for(String endPoint : endPointList)	{
				ec2.setEndpoint(endPoint);				
				DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
				reservations.addAll(describeInstancesRequest.getReservations());
			}

			Set<Instance> instances = new HashSet<Instance>();
		   
			// add all instances to a Set
			
			for (Reservation reservation : reservations) {				
				instances.addAll(reservation.getInstances());
			}
			
			System.out.println("You have " + instances.size() + " Amazon EC2 instance(s).");
			String iamInstanceProfile = null;
			String check = null;
			int i = 0;
			
			for (Instance ins : instances) {
				// instance id
				String instanceId = ins.getInstanceId();
				String status = ins.getState().getName();
				Date startTime = AWSCloudWatchClient.getStartDate();
				Date endTime = AWSCloudWatchClient.getEndDate();                
				//System.out.println("START TIME : "+startTime);
				//System.out.println("END TIME : "+endTime);
				if(status.equals("running")) {
					System.out.println("INSTANCE ID : "+instanceId); 
				    System.out.println("INSTANCE STATUS :: "+status); 
					awsCldWtchClntObj.getCPUUtilization(cw, instanceId, startTime, endTime);				
				}
			}
		} 
		catch (AmazonServiceException ase) {

			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}		
	}

	public static Date getStartDate() {

		Date sddate = null; 				
		try { 
		  
		  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		  String sdDate = "01-11-2014 23:59:59";		  
		  sddate = sdf.parse(sdDate);		  
		  //System.out.println(sddate.getTime());

		} catch (ParseException e) { 
		  System.out.println("Unparseable using " + e); 
		}
		return sddate;
	}

	public static Date getEndDate() {
		
		Date eddate = null; 		
		try { 
		  
		  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");		  
		  String edDate = "17-11-2014 23:59:59";		  
		  eddate = sdf.parse(edDate);
		  //System.out.println(eddate.getTime());

		} catch (ParseException e) { 
		  System.out.println("Unparseable using " + e); 
		}
		return eddate;
	}
}
