package com.psl.service;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.psl.util.AWSAdminConfiguration;
import com.psl.util.AWSClientsConfiguration;
import com.psl.util.PreRequisitesForClient;

/**
 * Servlet implementation class DeleteObject
 */
@WebServlet("/deleteObject")
public class DeleteObject extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		
		System.out.println("Inside Delete....!");
		//response.setContentType("text/html");
	  StringBuffer jb = new StringBuffer();
	  String line = null;
	  AmazonS3Client s3client=null;
      HttpSession session =request.getSession();  
      session.setAttribute("userRole", "admin");
      if(session.getAttribute("userRole")!=null &&  session.getAttribute("userRole").equals("admin")){
          s3client = new AmazonS3Client(AWSAdminConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
          //s3client = new AmazonS3Client(AWSAdminConfiguration.getCredentials());
      }
      else{
          s3client = new AmazonS3Client(AWSClientsConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
          //s3client = new AmazonS3Client(AWSClientsConfiguration.getCredentials());
      }
	  try {
	    BufferedReader reader = request.getReader();
	    while ((line = reader.readLine()) != null)
	      jb.append(line);
	  } catch (Exception e) { /*report an error*/ }
	  
	  JSONParser parser = new JSONParser();
      JSONArray objToBeDeleted = null;
      try
      {
        objToBeDeleted = (JSONArray) parser.parse(jb.toString());
        System.out.println("Displaying object..."+objToBeDeleted);
      } catch (ParseException e) { e.printStackTrace(); }
      
      for(int i=0;i<objToBeDeleted.size();i++)
      {
            JSONObject obj = (JSONObject)objToBeDeleted.get(i);
            String bucketName = (String) obj.get("bucketName");
            String parent = (String) obj.get("parent");
            String key = (String) obj.get("key");
            String type = (String) obj.get("type");
            
            
            
            if(parent.endsWith("/")){
              parent = parent.substring(0, parent.length()-1);
            }
            
            try{
              if(type.equals("folder")){
                s3client.deleteObject(new DeleteObjectRequest(bucketName, parent+"/"));
              }else{
                s3client.deleteObject(new DeleteObjectRequest(bucketName, parent+"/"+key));
              }
              
              
              
            }catch (AmazonServiceException ase) {
              System.out.println("Caught an AmazonServiceException.");
              System.out.println("Error Message:    " + ase.getMessage());
              System.out.println("HTTP Status Code: " + ase.getStatusCode());
              System.out.println("AWS Error Code:   " + ase.getErrorCode());
              System.out.println("Error Type:       " + ase.getErrorType());
              System.out.println("Request ID:       " + ase.getRequestId());
          } catch (AmazonClientException ace) {
              System.out.println("Caught an AmazonClientException.");
              System.out.println("Error Message: " + ace.getMessage());
          }
            
      }
	  
		/*try{
			String folderName = request.getParameter("foldername").trim();
			String bucketName =  request.getParameter("bucketName");
			System.out.println(folderName + "  " + bucketName);
			AmazonS3Client s3client=null;
			HttpSession session =request.getSession();	
			session.setAttribute("userRole", "admin");
			if(session.getAttribute("userRole")!=null &&  session.getAttribute("userRole").equals("admin")){
				s3client = new AmazonS3Client(AWSAdminConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
				//s3client = new AmazonS3Client(AWSAdminConfiguration.getCredentials());
			}
			else{
				s3client = new AmazonS3Client(AWSClientsConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
				//s3client = new AmazonS3Client(AWSClientsConfiguration.getCredentials());
			}

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			if(folderName==null || folderName.equals("")){
				System.out.println("Inside if");
				s3client.deleteBucket(bucketName);
				out.write(" "+bucketName+" is deleted");

			}else{
				System.out.println("Inside delete else");
				System.out.println(folderName + "  " + bucketName);
				for (S3ObjectSummary file : s3client.listObjects(bucketName, folderName).getObjectSummaries()){
					s3client.deleteObject(bucketName, file.getKey());
				}

				out.write(" "+folderName+" is deleted");

			}

			out.flush();
			out.close();

		}catch(Exception exception){
			exception.printStackTrace();
			System.out.println("In Catch Block!!");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.write("AmazonS3Exception: You cannot delete the Bucket unless it is empty!");
			out.flush();
			out.close();
		}*/
	}
}
