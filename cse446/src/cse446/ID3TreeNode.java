package cse446;

import java.util.*;

public class ID3TreeNode {
	public HashMap<Integer, List<Features>> classes;
	public ID3TreeNode left;
	public ID3TreeNode right;
	public int attribute;
	public int threshold; 
	
	public ID3TreeNode(HashMap<Integer, List<Features>> classes, ID3TreeNode left, ID3TreeNode right, int attribute,
			int threshold) {
		this.classes = classes;
		this.left = left;
		this.right = right;
		this.attribute = attribute;
		this.threshold = threshold;
	}

}
