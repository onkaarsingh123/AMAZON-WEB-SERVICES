package com.psl.bean;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<Data> implements Iterable<TreeNode<Data>> {
	private List<TreeNode<Data>> children;
	private TreeNode<Data> parent;
	private Data data = null;

	public TreeNode(Data data) {
		this.data = data;
		this.children = new LinkedList<TreeNode<Data>>();
	}

	public List<TreeNode<Data>> getChildren() {
		return children;
	}

	public void setParent(TreeNode<Data> parent) {
		this.parent = parent;
	}

	public TreeNode<Data> addChild(Data data) {
		TreeNode<Data> child = new TreeNode<Data>(data);
		child.setParent(this);
		this.children.add(child);
		return child;
	}

	public Data getData() {
		return this.data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	@Override
	public Iterator<TreeNode<Data>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
