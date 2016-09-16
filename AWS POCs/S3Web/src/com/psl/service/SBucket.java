package com.psl.service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.psl.bean.BucketBean;
import com.psl.util.AWSAdminConfiguration;
import com.psl.util.AWSClientsConfiguration;
import com.psl.util.PreRequisitesForClient;
/**
 * Servlet implementation class SBucket
 */
@WebServlet("/SBucket")
public class SBucket extends HttpServlet {
	private String jsonResponse="";
	String bucketName="";
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SBucket() {
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
		String parent = null;
		Bucket bucket = null;
		AmazonS3Client amazonS3Client =null;
		try {

			HttpSession session =request.getSession();	
			String role = (String) session.getAttribute("userRole");
			//System.out.println("Printing role..."+role);
			if(session.getAttribute("userRole")!=null &&  session.getAttribute("userRole").equals("admin")){
				amazonS3Client = new AmazonS3Client(AWSAdminConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
				//amazonS3Client = new AmazonS3Client(AWSAdminConfiguration.getCredentials());
			}
			else{
				amazonS3Client = new AmazonS3Client(AWSClientsConfiguration.getCredentials(),PreRequisitesForClient.getClientConfiguration());
				// amazonS3Client = new AmazonS3Client(AWSClientsConfiguration.getCredentials());

			}
			String operation = request.getParameter("operation");

			if (operation!=null &&  operation.equals("create")) {
				StringBuffer sb = new StringBuffer();
					
				System.out.println("Inside Create");
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
				JSONObject joUser = null;
				try
				{
					joUser = (JSONObject) parser.parse(sb.toString());
					System.out.println("joUser=="+ joUser);
				} catch (ParseException e) { e.printStackTrace(); }

				String name = (String) (joUser.get("name"));
				System.out.println(name.toLowerCase());
				bucket = amazonS3Client.createBucket(name.toLowerCase());
				bucketName = bucket.getName();
				request.setAttribute("message", "Bucket " + bucketName
						+ "Created Successfully");
				Owner owner = bucket.getOwner();
				Date date = bucket.getCreationDate();
				request.setAttribute("BUCKET", bucket);
				jsonResponse=bucketName;
			} else if(operation.equals("selectBucket") ){	
					bucketName=request.getParameter("bucketName");
					displayTableFromBucket(amazonS3Client, bucketName);

			}
			else if (operation.equals("displayTableFolder") ) {

					String nodeId=request.getParameter("NodeId");
					String nodeKey=request.getParameter("NodeKey");
					bucketName=request.getParameter("bucketName");
					displayTableFromFolder(amazonS3Client,nodeId,nodeKey,bucketName);

			}
			else if (operation.equals("tableFolder") ) {

				String nodeKey=request.getParameter("NodeKey");
				bucketName=request.getParameter("bucketName");
				parent = request.getParameter("Parent");
				displayTable(amazonS3Client,parent,nodeKey,bucketName);

			}


		} catch (AmazonS3Exception amazonS3Exception) {
			request.setAttribute("message", "Bucket Creation Failed due to "
					+ amazonS3Exception);
			amazonS3Exception.printStackTrace();
		}
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.write(jsonResponse);
		out.flush();
		out.close();
	}

	private void displayTableFromBucket(AmazonS3Client amazonS3Client, String bucketName) {

			List<BucketBean> bucketList = new ArrayList<BucketBean>();
			String prefix="";
			bucketList = getBucketList(amazonS3Client, bucketName, prefix);
			System.out.println("Display Table From Folder");
			generateJSon(bucketList);
	}
	
	private List<BucketBean> getBucketList(AmazonS3Client amazonS3Client,String bucketName,String prefix)
	{
		List<BucketBean> bucketList = new ArrayList<>();
		ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		ObjectListing objectListing = amazonS3Client.listObjects(lor);
		List<String> listFolder =objectListing.getCommonPrefixes();
		
		for(String s : listFolder)
		{
			
			String data[]=s.split("/");
			
			S3Object object = amazonS3Client.getObject(new GetObjectRequest(bucketName, s));
			
			System.out.println(object);
			
			BucketBean bean = new BucketBean();
			bean.setBucketName(bucketName);
			bean.setKey(data[data.length-1]);
			bean.setParent(data[0]);
			bean.setType("folder");
			bean.setSize(object.getObjectMetadata().getContentLength());
			
			Date date = object.getObjectMetadata().getLastModified();
			SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
			String dateString = simpleDateFormat.format(date);
			
			bean.setDate(dateString);
			
			bucketList.add(bean);
		}

		List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
		Iterator<S3ObjectSummary> it = s3ObjectSummaries.iterator();
			
				while(it.hasNext())
				{
					S3ObjectSummary summary = it.next();
					
BucketBean bean = new BucketBean();
					bean.setBucketName(bucketName);
					bean.setKey(summary.getKey());
					bean.setType("file");
					bean.setSize(summary.getSize());
					bean.setParent(bucketName);
					
					Date date = summary.getLastModified();
					SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
					String dateString = simpleDateFormat.format(date);
					
					bean.setDate(dateString);
					bucketList.add(bean);
	}
		return bucketList;
	}


	private void displayTableFromFolder(AmazonS3Client amazonS3Client , String nodeId, String nodeKey,String bucketName)  {
		ArrayList<BucketBean> folderList= new ArrayList<BucketBean>();
		String parentKey="";
		try{
			//System.out.println("Displaying Folder Data into Table With Parent Node");
			if(nodeId.equals(bucketName)){
				parentKey = (nodeKey+"/");
			}else
				if(!nodeId.contains("/")){
					parentKey = nodeId+"/"+nodeKey;
				}else{
				parentKey = nodeId+"/"+nodeKey;
			}
			//	System.out.println("parentKey:"+parentKey);
				ObjectListing objectListing = amazonS3Client.listObjects(bucketName,parentKey);
				List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
				folderList = renderTable(s3ObjectSummaries,nodeKey,parentKey);
				generateJSon(folderList);
		}catch(Exception e){
			e.printStackTrace();
		}
		
//		List<BucketBean> bucketList = new ArrayList<BucketBean>();
//		String prefix=nodeKey;
//		bucketList = getBucketList(amazonS3Client, bucketName, prefix);
//		System.out.println("Inside Display Table");
//		generateJSon(bucketList);
	}


	private void displayTable(AmazonS3Client amazonS3Client , String nodeId, String nodeKey,String bucketName)  {

//		System.out.println("Inside Display Table");
//		
//		System.out.println("Node Id.."+nodeId+"   Node Key..."+nodeKey);
//		
//		List<BucketBean> bucketList = new ArrayList<BucketBean>();
//		String prefix="";
//		
//		if(nodeId.equals(bucketName)){
//			prefix = nodeKey+"/";
//			System.out.println("Buck...");
//		}
//		else if(nodeId.equals(nodeKey)){
//			prefix = nodeKey+"/";
//		}
//		else if(!nodeId.equals(nodeKey)){
//			prefix = nodeId+"/"+nodeKey+"/";
//			System.out.println("Not Buck...");
//		}	
//		
//		System.out.println("Prefix..."+prefix);
//		bucketList = getBucketList(amazonS3Client, bucketName, prefix);
//		
//		generateJSon(bucketList);
		
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
				List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
				
				folderList = renderTable(s3ObjectSummaries,nodeKey,parentKey);
     			generateJSon(folderList);
				
				
					
		}catch(Exception e){
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
	
	

	private ArrayList<BucketBean> renderTable(List<S3ObjectSummary> s3ObjectSummaries, String nodeKey,String nodeId) {

		ArrayList<BucketBean> arrayList= new ArrayList<BucketBean>();
		BucketBean bucketBean=null;
		String key = "";
		
		for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {

			key = s3ObjectSummary.getKey();
			//System.out.println("key=" + key  );

			bucketBean= new BucketBean();

			if (key != null) {
				if (key.contains("/")) {
					// Key contains folder and file name
					
					if (key.contains("/") && key.contains(".")) {
						
						String parent = "";
						for (String s : nodeKey.split("/")) {
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
						for (String s : nodeKey.split("/")) {
							parent = s;
						}
						if (!folderName.equals(parent )  && validParent.equals(parent)) {
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

		return arrayList;
	}
	
	
	private String generateJSon(List<BucketBean> arrayList){
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

		return jsonResponse;
	}
	
	

}
