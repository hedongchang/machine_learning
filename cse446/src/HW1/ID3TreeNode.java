package HW1;

import java.util.*;

/**
 * A class of ID3TreeNode that stores
 * its children, the values it holds, current attribute
 * and current theshold for the attribute
 * @author Dongchang
 *
 */
public class ID3TreeNode {
	public HashMap<Integer, List<Features>> classes; // values the node holds
	public ID3TreeNode left;  // left child
	public ID3TreeNode right;  // right child
	public int attribute;  // current attribute
	public int threshold;   // current threshold
	
	public int height;
	
	public ID3TreeNode(HashMap<Integer, List<Features>> classes, ID3TreeNode left, ID3TreeNode right, int attribute,
			int threshold) {
		this.classes = classes;
		this.left = left;
		this.right = right;
		this.attribute = attribute;
		this.threshold = threshold;
		this.height = 0;
	}

}
