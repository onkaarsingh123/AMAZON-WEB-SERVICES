package com.psl.bean;

import java.util.ArrayList;
import java.util.List;

public class BucketsData {
	private String bucketName;
	
	private List bucketList = new ArrayList();

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public List getBucketList() {
		return bucketList;
	}

	public void setBucketList(List bucketList) {
		this.bucketList = bucketList;
	}
	
	
	
	
	

}
