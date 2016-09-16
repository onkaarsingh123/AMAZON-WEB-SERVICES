package com.psl.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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

		//response.setContentType("text/html");
		try{
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
		}
	}
}
