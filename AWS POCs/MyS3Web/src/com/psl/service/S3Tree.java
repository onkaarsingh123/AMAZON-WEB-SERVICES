package com.psl.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.json.JSONArray;
import com.psl.bean.BucketsData;
import com.psl.bean.Data;
import com.psl.bean.TreeNode;
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
		// TODO Auto-generated method stub
		Data data = null;
		AmazonS3Client amazonS3Client = new AmazonS3Client(
				AWSClientsConfiguration.getCredentials(),
				PreRequisitesForClient.getClientConfiguration());
		JSONArray JSONArray;
		List<BucketsData> bucketsDataList = new ArrayList<BucketsData>();
		BucketsData bucketsData = null;

		// data =new Data();
		// data.setKey("root");
		// TreeNode<Data> root = new TreeNode<Data>(data);
		String key = "";
		String[] str;
		Set<String> bucketNodelist = new TreeSet<String>();
		int count = 0;

		List<Bucket> buckets = amazonS3Client.listBuckets();
		for (Bucket bucket : buckets) {
			if (bucket.getName().contains("testbucket")) {
				bucketsData = new BucketsData();
				data = new Data();
				data.setId("bucket" + count);
				data.setKey(bucket.getName());
				// TreeNode<Data> node = root.addChild(data);
				TreeNode<Data> root = new TreeNode<Data>(data);

				bucketNodelist = new TreeSet<String>();
				for (S3ObjectSummary bucketSummarys : S3Objects.inBucket(
						amazonS3Client, bucket.getName())) {
					key = bucketSummarys.getKey();
					str = key.split("/");
					bucketNodelist.add(str[0]);
				}

				for (String bucketSummary : bucketNodelist) {
					// File inside bucket
					if (bucketSummary.contains(".")) {
						// data = new Data();
						// data.setId("role" + count);
						// data.setKey(bucketSummary);
						// TreeNode<Data> parentNode = node.addChild(data);
					} else {
						// Folder inside bucket
						data = new Data();
						data.setKey(bucketSummary);
						data.setId(bucket.getName());
						TreeNode<Data> parentNode = root.addChild(data);

						TreeNode<Data> childNode = traverse(amazonS3Client,
								bucketSummary, data, parentNode, count,
								bucket.getName());
					}
					count++;
				}

				bucketsData.setBucketName(bucket.getName());
				bucketsData.setBucketList(root.getChildren());
				bucketsDataList.add(bucketsData);

			}
		}

		JSONArray = new JSONArray(bucketsDataList);
		System.out.println(JSONArray);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.write(JSONArray.toString());
		out.flush();
		out.close();

	}

	static TreeNode<Data> traverse(AmazonS3Client amazonS3Client,
			String parentKey, Data data, TreeNode<Data> node, int count,
			String bucketName) {
		String key = "";
		ObjectListing objectListing = amazonS3Client.listObjects(bucketName,
				parentKey);
		List<S3ObjectSummary> s3ObjectSummaries = objectListing
				.getObjectSummaries();
		
		for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
			key = s3ObjectSummary.getKey();
			if (key != null) {
				if (key.contains("/")) {
					// Key contains folder and file name
					if (key.contains("/") && key.contains(".")) {

					} else {
						// Key contains folder name only
						String folderName = "";
						for (String s : key.split("/")) {
							folderName = s;
						}

						String validParent="";
						String str[] = key.split("/");
						if(str.length==1){
							validParent=str[0]	;
						}else{
							validParent=str[str.length-2];
						}
						
						
						String parent = "";
						for (String s : parentKey.split("/")) {
							parent = s;
						}

						if (!folderName.equals(parent) && validParent.equals(parent) ) {
							
								data = new Data();
								data.setKey(folderName);
								data.setId(parentKey);
								TreeNode<Data> childNode = node.addChild(data);
								data = new Data();

								// recursively add child folder or file
								traverse(amazonS3Client, key, data, childNode,
										count, bucketName);
							
						}

					}

				} else {
					// For file

				}
			}
		}

		return node;
	}

}
