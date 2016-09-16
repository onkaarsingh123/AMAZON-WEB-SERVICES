package com.psl.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.util.AWSAdminConfiguration;
import com.psl.util.AWSClientsConfiguration;
import com.psl.util.PreRequisitesForClient;


/**
 * Servlet implementation class FileUploader
 */
@WebServlet("/upload")
public class FileUploader extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploader() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		System.out.println(request.getContentType());

		// needed for cross-domain communication
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Max-Age", "86400");
		
		DiskFileItemFactory factory = new DiskFileItemFactory();	
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);

        // Add here your own limit
		servletFileUpload.setSizeMax(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
		
		response.setContentType("multipart/form-data");
		// checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			PrintWriter writer = response.getWriter();
			writer.println("Request does not contain upload data");
			writer.flush();
			return;
		}

		// String pathInfo = request.getPathInfo();
		// request.setAttribute("path", pathInfo);
		// process only if its multipart content

		ArrayList<FileItem> formList = new ArrayList<FileItem>();
		FileItem itemFile = null;
		Upload upload = null;

		AmazonS3Client s3client = null;
		if (ServletFileUpload.isMultipartContent(request)) {

			try {
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);

				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						itemFile = item;

					} else {
						formList.add(item);
					}

				}

				if (itemFile != null) {

					// get item inputstream to upload file into s3 aws

					HttpSession session = request.getSession();
					if (session.getAttribute("userRole") != null
							&& session.getAttribute("userRole").equals("admin")) {

						s3client = new AmazonS3Client(
								AWSAdminConfiguration.getCredentials(),
								PreRequisitesForClient.getClientConfiguration());
						// s3client = new
						// AmazonS3Client(AWSAdminConfiguration.getCredentials());
					} else {
						s3client = new AmazonS3Client(
								AWSClientsConfiguration.getCredentials(),
								PreRequisitesForClient.getClientConfiguration());
						// s3client = new
						// AmazonS3Client(AWSClientsConfiguration.getCredentials());

					}

					TransferManager transferManager = new TransferManager(
							s3client);

					try {

						ObjectMetadata objectMetadata = new ObjectMetadata();
						objectMetadata.setContentLength(itemFile.getSize());

						// String ext =
						// FilenameUtils.getExtension(itemFile.getName());
						String keyName = itemFile.getName();
						String bucketName =  request.getParameter("bucketName");
						String folderName = request.getParameter("foldername");


						System.out.println("FolderName: "+folderName+"BucketName: "+bucketName);

						System.out.println(" upload folder =="
								+ folderName + "/" + keyName);


						if (folderName == null || folderName.equals("")) {
							System.out.println(bucketName + "  " + keyName);
							upload = transferManager.upload(bucketName,
									keyName, itemFile.getInputStream(),
									objectMetadata);

							// PutObjectResult
							// putObjectResult=s3client.putObject(new
							// PutObjectRequest("bucket-01-new", keyName,
							// itemFile.getInputStream(),objectMetadata));

							response.setContentType("text/html");
							PrintWriter out = response.getWriter();
							out.write("File Uploaded Successfully!");
							out.flush();
							out.close();

							request.setAttribute("message",
									"File Uploaded Successfully!");

						} else {
							System.out.println("folder file upload=="
									+ folderName + "/" + keyName);
							upload = transferManager.upload(bucketName,
									folderName + "/" + keyName,
									itemFile.getInputStream(),
									objectMetadata);

							// PutObjectResult
							// putObjectResult=s3client.putObject(new
							// PutObjectRequest("bucket-01-new", keyName,
							// itemFile.getInputStream(),objectMetadata));

							response.setContentType("text/html");
							PrintWriter out = response.getWriter();
							out.write("File Uploaded in folder " + folderName
									+ " Successfully!");
							out.flush();
							out.close();

							request.setAttribute("message",
									"File Uploaded in folder " + folderName
									+ " Successfully!");
						}

					} catch (AmazonServiceException amazonS3Exception) {

						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.write("Failed to Upload File in S3 Bucket "
								+ amazonS3Exception);
						out.flush();
						out.close();

						request.setAttribute("message",
								"Failed to Upload File in S3 Bucket "
										+ amazonS3Exception);

					} catch (AmazonClientException amazonClientException) {

						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.write("Amazon Client Exception "
								+ amazonClientException);
						out.flush();
						out.close();

						request.setAttribute("message",
								"Amazon Client Exception "
										+ amazonClientException);
					}

				}
			} catch (Exception ex) {

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

		} else {

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.write("Sorry this Servlet only handles file upload request");
			out.flush();
			out.close(); 

			request.setAttribute("message",
					"Sorry this Servlet only handles file upload request");
		}

	}

}
