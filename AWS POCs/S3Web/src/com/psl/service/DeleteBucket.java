package com.psl.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.psl.util.AWSAdminConfiguration;
import com.psl.util.AWSClientsConfiguration;
import com.psl.util.PreRequisitesForClient;

/**
 * Servlet implementation class DeleteBucket
 */
@WebServlet("/deleteBucket")
public class DeleteBucket extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteBucket() {
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
		// TODO Auto-generated method stub
		String bucketName = request.getParameter("bucketName");
		
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
		
	      try
	      {
		      DeleteBucketRequest bucketRequest = new DeleteBucketRequest(bucketName);
		      s3client.deleteBucket(bucketRequest);

	      }
	      catch (Exception ex) {

				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.write("File Upload Failed due to "
						+ ex);
				out.flush();
				out.close();

				request.setAttribute("message", "File Upload Failed due to "
						+ ex);
				ex.printStackTrace();
			}
	      
	      System.out.println("Bucket deleted successfully");
	      
		System.out.println("Inside Deleting bucket..."+bucketName);
	}

}
