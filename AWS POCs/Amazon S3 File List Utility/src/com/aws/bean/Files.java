package com.aws.bean;

import java.util.Date;

import com.amazonaws.services.s3.model.Owner;

public class Files {

	private String name;
	private Date dateLastModified;
	private String owner;
	private long size;
		
	
	
	
	public Files(String name, Date dateLastModified, String owner,long size) {
		super();
		this.name = name;
		this.dateLastModified = dateLastModified;
		this.owner = owner;
		this.size = size;
	}



	@Override
	public String toString() {
		return "Files [name=" + name + ", dateLastModified=" + dateLastModified
				+ ", owner=" + owner + ", size=" + size + "]";
	}



	public void setName(String name) {
		this.name = name;
	}



	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}



	public void setOwner(String owner) {
		this.owner = owner;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
