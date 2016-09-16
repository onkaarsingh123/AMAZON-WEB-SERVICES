package com.psl.bean;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;

import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

public class BucketBean implements JSONStreamAware {

	private String bucketName;
	private String key;
	private long size;
	private String parent;


	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	private String type;
	private String date;


	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();
		obj.put("key", key);
		obj.put("size", size);
		obj.put("type", type);
		obj.put("date", date);
		obj.put("bucketName", bucketName);
		obj.put("parent", parent);
		JSONValue.writeJSONString(obj, out);

	}
	


}
