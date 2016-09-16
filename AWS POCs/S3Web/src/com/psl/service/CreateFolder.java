package com.psl.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.psl.bean.BucketBean;
import com.psl.util.AWSAdminConfiguration;
import com.psl.util.AWSClientsConfiguration;
import com.psl.util.PreRequisitesForClient;

/**
 * Servlet implementation class CreateFolder
 */
@WebServlet("/createFolder")
public class CreateFolder extends HttpServlet {
	public String jsonResponse="";
	String bucketName="";
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateFolder() {
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
			String folderName = request.getParameter("foldername");
			System.out.println("Folder Name..." + folderName);
			Upload upload=null;
			bucketName =  request.getParameter("bucketName");

			System.out.println("foldername="+folderName + "  " + bucketName);
			AmazonS3Client amazonS3Client=null;
			HttpSession session =request.getSession();	
			session.setAttribute("userRole", "admin");
			if(session.getAttribute("userRole")!=null &&  session.getAttribute("userRole").equals("admin")){

				amazonS3Client = new AmazonS3Client(AWSAdminConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
				//s3client = new AmazonS3Client(AWSAdminConfiguration.getCredentials());

			}
			else{
				amazonS3Client = new AmazonS3Client(AWSClientsConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
				//s3client = new AmazonS3Client(AWSClientsConfiguration.getCredentials());

			}

			StringBuffer sb = new StringBuffer();

			try 
			{
				BufferedReader reader = request.getReader();
				String line = null;
				while ((line = reader.readLine()) != null)
				{
					sb.append(line);
				}
			} catch (Exception e) { e.printStackTrace(); }

			JSONParser parser = new JSONParser();
			JSONObject jsonObj = null;
			try
			{
				jsonObj = (JSONObject) parser.parse(sb.toString());
				//	System.out.println("joUser=="+ jsonObj);
			} catch (ParseException e) { e.printStackTrace(); }

			String name = (String) jsonObj.get("name");

			System.out.println("Name: "+name);


			TransferManager transferManager = new TransferManager(
					amazonS3Client);

			// Create metadata for your folder & set content-length to 0

			ObjectMetadata metadata = new ObjectMetadata();

			metadata.setContentLength(0);

			// Create empty content

			InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

			//Create folder suffix
			String FOLDER_SUFFIX = "/";
			
			
			if(folderName==null || folderName.equals("")){
				upload = transferManager.upload(bucketName,name+FOLDER_SUFFIX,emptyContent, metadata);
			}else{
			    if(folderName.endsWith("/")){
			      folderName = folderName.substring(0,folderName.length()-1);
			    }
				upload = transferManager.upload(bucketName,folderName+FOLDER_SUFFIX+name+FOLDER_SUFFIX,emptyContent, metadata);
			}

			String nodeId = folderName;
			upload.waitForCompletion();
			
			if(folderName==null || folderName.equals("")){
				System.out.println("(inside folder");
				displayTableFromBucket(amazonS3Client,bucketName);
			}
			else{
				displayTable(amazonS3Client,nodeId,name,bucketName);
			}

		}catch(Exception exception){
			exception.printStackTrace();
		}

		System.out.println("JSONDATA: "+jsonResponse);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.write(jsonResponse);
		out.flush();
		out.close();

	}

	private void displayTable(AmazonS3Client amazonS3Client , String nodeId, String nodeKey,String bucketName)  {
		//System.out.println("Inside Display");
		//System.out.println("NodeId:"+nodeId+"NodeKey:"+nodeKey);
		ArrayList<BucketBean> folderList= new ArrayList<BucketBean>();
		String parentKey="";
		try{
			if(nodeId.equals(bucketName)){
				parentKey = (nodeKey+"/");
			}else{
				parentKey = nodeId;
			}	

			System.out.println("parentKey:"+parentKey);
			ObjectListing objectListing = amazonS3Client.listObjects(bucketName,parentKey);
			System.out.println("Object Listing:"+objectListing.toString());
			List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
			System.out.println("Summary: "+s3ObjectSummaries);
			folderList = renderTable(s3ObjectSummaries,nodeKey,parentKey);
			generateJSon(folderList);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private ArrayList<BucketBean> renderTable(List<S3ObjectSummary> s3ObjectSummaries, String nodeKey,String nodeId) {
		//System.out.println("Inside render");
		//System.out.println("Summaries: "+s3ObjectSummaries);
		ArrayList<BucketBean> arrayList= new ArrayList<BucketBean>();
		BucketBean bucketBean=null;
		String key = "";
		System.out.println("nodeKey=" + nodeKey  );
		System.out.println("nodeId=" + nodeId  );
		for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {

			//	System.out.println("Inside for loop!");

			key = s3ObjectSummary.getKey();
			System.out.println("key=" + key  );

			bucketBean= new BucketBean();

			if (key != null) {
				if (key.contains("/")) {
					// Key contains folder and file name

					if (key.contains("/") && key.contains(".")) {

						String parent = "";
						for (String s : nodeId.split("/")) {
							parent = s;
						}
						String filename[] = key.split("/");
						if ((filename[filename.length - 2]).equals(parent)) {
							bucketBean.setKey(filename[filename.length - 1]);
							bucketBean.setType("file");
							bucketBean.setSize(s3ObjectSummary.getSize());
							Date date = s3ObjectSummary.getLastModified();
							SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
							String dateString = simpleDateFormat.format(date);
							bucketBean.setDate(dateString);
							bucketBean.setBucketName(bucketName);
							bucketBean.setParent(nodeId);

							arrayList.add(bucketBean);
						}
					}else {
						// Key contains folder name only
						//System.out.println("Key Contains folder name only!");
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
						for (String s : nodeId.split("/")) {
							parent = s;
						}
						System.out.println("foldername: "+folderName+"parent: "+parent+"validParent"+validParent);
						if (!folderName.equals(parent )  && validParent.equals(parent)) {
							System.out.println("Inside If!");
							bucketBean.setKey(folderName);
							bucketBean.setType("folder");
							bucketBean.setSize(s3ObjectSummary.getSize());
							Date date = s3ObjectSummary.getLastModified();
							SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
							String dateString = simpleDateFormat.format(date);
							bucketBean.setDate(dateString);
							bucketBean.setBucketName(bucketName);
							bucketBean.setParent(key);

							arrayList.add(bucketBean);
						}
					}
				}
				else  {
					// Key contains file name only
					bucketBean.setKey(s3ObjectSummary.getKey());
					bucketBean.setType("file");
					bucketBean.setSize(s3ObjectSummary.getSize());
					Date date = s3ObjectSummary.getLastModified();
					SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
					String dateString = simpleDateFormat.format(date);
					bucketBean.setDate(dateString);
					bucketBean.setBucketName(bucketName);
					bucketBean.setParent(nodeId);

					arrayList.add(bucketBean);
				}


			}
		}
		System.out.println("List:" +arrayList);
		return arrayList;
	}

	private String generateJSon(List<BucketBean> arrayList){
		System.out.println("Arraylist:"+arrayList);
		for (BucketBean bucketBean : arrayList) {
			System.out.println("Arraylist:"+bucketBean.getBucketName());
		}
	//	System.out.println("Inside Generate");
		jsonResponse="";
		JSONArray jsonArray = new JSONArray();
		StringWriter writer = null;
		try {
			for (BucketBean object : arrayList) {
				jsonArray.add(object);
				writer = new StringWriter();

				jsonArray.writeJSONString(writer);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (writer != null) {
			jsonResponse = writer.toString();
			System.out.println("Json data:" + jsonResponse.toString());
		} else {
			jsonResponse = "";
		}
		System.out.println("Response"+jsonResponse);
		return jsonResponse;
	}


	private void displayTableFromBucket(AmazonS3Client amazonS3Client, String bucketName) {
	//	System.out.println("Bucket name: "+bucketName);
		try {
			ArrayList<BucketBean> bucketList= new ArrayList<BucketBean>();
			//System.out.println("Displaying Bucket Data into Table");
			ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
			List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
			bucketList = renderTableFromBucket(s3ObjectSummaries);
			generateJSon(bucketList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ArrayList<BucketBean> renderTableFromBucket(List<S3ObjectSummary> s3ObjectSummaries) {
		ArrayList<BucketBean> arrayList= new ArrayList<BucketBean>();
		BucketBean bucketBean=null;
		String key = "";
		String[] str;
		Set<String> bucketNodelist = new TreeSet<String>();
		for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {

			key = s3ObjectSummary.getKey();
			System.out.println("renderTableFromBucket key:" + key);
			str = key.split("/");
			bucketNodelist.add(str[0]);
		}


		for(String bucket:bucketNodelist){
			for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
				String bucketSummary=s3ObjectSummary.getKey();
				bucketBean= new BucketBean();
				if(bucketSummary.contains(bucket)){
					if (bucketSummary.contains(".")) {
						bucketBean.setKey(bucket);
						bucketBean.setType("file");

					} else {
						// Folder inside bucket
						bucketBean.setKey(bucket);
						bucketBean.setType("folder");
					}
					bucketBean.setSize(s3ObjectSummary.getSize());
					Date date = s3ObjectSummary.getLastModified();
					SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
					String dateString = simpleDateFormat.format(date);
					bucketBean.setDate(dateString);
					bucketBean.setBucketName(bucketName);
					bucketBean.setParent(s3ObjectSummary.getKey());
					arrayList.add(bucketBean);
					break;
				}

			}

		}
		return arrayList;
	}

}
