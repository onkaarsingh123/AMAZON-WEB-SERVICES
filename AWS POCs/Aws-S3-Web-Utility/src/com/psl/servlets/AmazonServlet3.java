package com.psl.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.psl.amazon.AwsS3Utility;

/**
 * Servlet implementation class AmazonServlet3
 */
public class AmazonServlet3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public AmazonServlet3() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		String folder = request.getParameter("name");
		String bucketName = request.getParameter("bucketname");

		System.out.println("Folder..."+folder);
		System.out.println("Bucket..."+bucketName);
		String accessKey = "AKIAIPSGWELRAGX527QQ";
	      String secretKey = "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";
	     AwsS3Utility s3Utility = new AwsS3Utility(accessKey, secretKey);
	     Gson gson = new Gson();
	     
	     Map<String,List<String>> map = new HashMap<>();
	     
	     
	     String prefix = folder.trim();
	     
	     List<String> list = s3Utility.getChildFolders(bucketName.trim(), prefix);
	     map.put("Folder", list);
	     List<String> list2 =s3Utility.getFilesOnly(bucketName, prefix);
	     map.put("Files", list2);
	     
	     list.addAll(list2);
	     
	   
	     String jsonList = gson.toJson(list);
	     response.getOutputStream().println(jsonList);
	     
	}

}
