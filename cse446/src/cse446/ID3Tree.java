package cse446;

import java.util.HashSet;

public class ID3Tree {
	ID3TreeNode overallNode;
	
	public ID3Tree(HashSet<Value> values) {
		int initialAttribute = findAttribute();
		int initialThreshold = findThreshold();
		this.overallNode = new ID3TreeNode(values, null, null, initialAttribute, initialThreshold);
	}
	
	public void growTree(HashSet<Integer> values) {
		growTree(values);
		if () {
			
		}
	}
	
	/**
	 * find the threshold of a given attribute
	 */
	public int findThreshold(int attribute) {
		return 0;
	}
	
	/**
	 * find the best attribute of a given set of values
	 * @param values
	 * @return the best attribute
	 */
	public int findAttribute(HashSet<Value> values) {
		return 0;
	}
	
	/**
	 * compute the entropy of a given set of values
	 * @return
	 */
	private int computeEntropy() {
		
	}
	
}
