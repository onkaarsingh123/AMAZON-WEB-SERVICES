package com.psl.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.s3.model.Bucket;
import com.google.gson.Gson;
import com.psl.amazon.AwsS3Utility;



/**
 * Servlet implementation class amazonServlet
 */
@WebServlet("/amazonServlet")
public class AmazonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AmazonServlet() {
        super();
    }


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String accessKey = "AKIAIPSGWELRAGX527QQ";
	      String secretKey = "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";
	     AwsS3Utility s3Utility = new AwsS3Utility(accessKey, secretKey);
	     Gson gson = new Gson();
	     
	    List<Bucket> buckets = s3Utility.getAllBuckets();
	    String list = gson.toJson(buckets);
	    
	    response.getOutputStream().print(list);
	    
	     
	}

}
