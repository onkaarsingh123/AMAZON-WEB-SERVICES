package com.psl.servlets;

import java.io.IOException;
import java.util.ArrayList;
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
 * Servlet implementation class AmazonServlet2
 */
public class AmazonServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AmazonServlet2() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

			String name = request.getParameter("name");

			String accessKey = "AKIAIPSGWELRAGX527QQ";
		      String secretKey = "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";
		     AwsS3Utility s3Utility = new AwsS3Utility(accessKey, secretKey);
		     Gson gson = new Gson();
		     
		     Map<String,List<String>> map = new HashMap<>();
		     
		     
		     
		     List<String> list = s3Utility.getChildFolders(name.trim());
		     map.put("Folder", list);
		     List<String> list2 =s3Utility.getFilesOnly(name, "");
		     map.put("Files", list2);
		     
		     list.addAll(list2);
		     String jsonList = gson.toJson(list);
		     response.getOutputStream().println(jsonList);
		     
		
	}

}
