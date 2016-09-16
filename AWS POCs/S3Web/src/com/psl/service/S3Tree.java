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

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.psl.bean.BucketsData;
import com.psl.util.AWSClientsConfiguration;
import com.psl.util.PreRequisitesForClient;

/**
 * Servlet implementation class S3Tree
 */
@WebServlet(description = "Display s3 tree form", urlPatterns = { "/S3Tree" })
public class S3Tree extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public S3Tree() {
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
		
		AmazonS3Client amazonS3Client = new AmazonS3Client(
				AWSClientsConfiguration.getCredentials(),
				PreRequisitesForClient.getClientConfiguration());
		
		HttpSession session =request.getSession(); 
		String role = (String) session.getAttribute("userRole");
		System.out.println("Printing role in S3Tree..."+role);
		JSONArray jsonArray;
		
		List<Bucket> buckets = amazonS3Client.listBuckets();
		System.out.println(buckets);
		
		jsonArray = new JSONArray(buckets);
		System.out.println("JSON Array..."+jsonArray);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.write(jsonArray.toString());
		out.flush();
		out.close();

	}



}
