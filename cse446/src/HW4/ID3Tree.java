package HW4;

import HW1.*;

import java.util.*;

public class ID3Tree {
	
	// the number of attributes in values
	public static final int NUM_ATTRIBUTE = 22;
	// the number of labels in values
	public static final int NUM_LABEL = 2;
	
	// the number of attributes that have been split
	public int nodeNum;
	// the overall node of the tree
	public ID3TreeNode overallNode;
	
	public static final int MAX_DEPTH = 1;
	
	/**
	 * constructs a new ID3Tree
	 * @param values the values of data
	 */
	public ID3Tree(HashMap<Integer, List<Features>> values) {
		ValueSize valueSize = new ValueSize(80, values);
		this.overallNode = growTree(valueSize, 0);
	}

	/**
	 * Grow a ID3 tree
	 * @param values the values of data
	 * @return a updated ID3 tree node
	 */
	public ID3TreeNode growTree(ValueSize values, int height) {
		nodeNum++;
		if (height >= MAX_DEPTH) {
			return new ID3TreeNode(values.values, null, null, -1);
		} else if (sameLabel(values.values)) {
			// the whole data is pure
			return new ID3TreeNode(values.values, null, null, -1);
		} else {
			height++;
			//System.out.println();
			int attribute = findAttribute(values);
			//System.out.println(attribute);
			ValueSize leftValue = splitValues(attribute, values.values, true);
			ValueSize rightValue = splitValues(attribute, values.values, false);
			return new ID3TreeNode(values.values, growTree(leftValue, height), growTree(rightValue, height), attribute);
		}
	}
	
	/**
	 * predict the label of a given list of Features
	 * @param Features a single feature of a value
	 * @param node the updated ID3 tree node
	 * @param path the updated current path from root
	 * @return an integer that indicates the label of a given list of Features
	 */
	public int predict(Features features, ID3TreeNode node) {
		if (node.left == null && node.right == null) {
			return findMostCommonLabel(node.classes);
		} else if (features.getFeature(node.attribute) == 0) {
			// negative values indicates goes left from the current node
			// first two digits store attribute and the last two store threshold 
			return predict(features, node.left);
		} else {
			// positive values indicates goes left from the current node
			// first two digits store attribute and the last two store threshold 
			return predict(features, node.right);
		}
	}
	
	private int findMostCommonLabel(HashMap<Integer, List<Features>> values) {
		int maxSize = 0;
		int maxLabel = 0;
		for (Integer label: values.keySet()) {
			if (values.get(label).size() > maxSize) {
				maxSize = values.get(label).size();
				maxLabel = label;
			}
		}
		return maxLabel;
	}
	
	/**
	 * split the training data set in two according to the threshold of values
	 * @param attribute the attribute to split
	 * @param threshold the threshold of attribute to split
	 * @param values the data values
	 * @param indicator whether return left split or right split
	 * @return either the left split or the right split
	 */
	private ValueSize splitValues
		(int attribute, HashMap<Integer, List<Features>> values, boolean indicator) {
		HashMap<Integer, List<Features>> newValues = new HashMap<Integer, List<Features>>();
		Set<Integer> labels = values.keySet();
		int count = 0;
		for (int label: labels) {
			List<Features> features = values.get(label);
			if (!newValues.containsKey(label)) {
				newValues.put(label, new ArrayList<Features>());
			}
			for (Features singleFeatures: features) {
				// the left split of the values 
				if (indicator) {
					if (singleFeatures.getFeature(attribute) == 0) {
						newValues.get(label).add(singleFeatures);
						count++;
					}
				// the right split of the values
				} else {
					if (singleFeatures.getFeature(attribute) == 1) {
						newValues.get(label).add(singleFeatures);
						count++;
					}
				}
			}
		}
		return new ValueSize(count, newValues);
	}
	
	
	/**
	 * return whether a class has the same label
	 * @param values the data values
	 * @return whether a class has the same label
	 */
	private boolean sameLabel(HashMap<Integer, List<Features>> values) {
		int count = 0;
		for (int i = 0; i < NUM_LABEL; i++) {
			if (values.get(i).size() != 0) {
				count++;
			}
		}
		return count == 1;
	}
	
	/**
	 * find the best attribute of a given set of values
	 * @param values the valus of data
	 * @return the best attribute with least conditional entropy
	 */
	public int findAttribute(ValueSize values) {
		int minAttribute = 0;
		double minEntropy = Double.MAX_VALUE;
		for (int i = 1; i < 22; i++) {
			double entropy = computeEntropySingleAttribute(values, i);
			if (entropy <= minEntropy) {
				minEntropy = entropy;
				minAttribute = i;
			}
		}
		return minAttribute;
	}
	
	/**
	 * given a attribute and return all the values with the same value of attribute
	 * @param attribute the given attribute
	 * @param value the given value of one attribute
	 * @param values the values of data
	 * @return the values with the same value of the given attribute
	 */
	private ValueSize getFeaturesOfValue(int attribute, int value, HashMap<Integer, List<Features>> values) {
		HashMap<Integer, List<Features>> newValues = new HashMap<Integer, List<Features>>();
		int count = 0;
		for (Integer label: values.keySet()) {
			List<Features> features = values.get(label);
			if (!newValues.containsKey(label)) {
				newValues.put(label, new ArrayList<Features>());
			}
			for (Features singleFeature: features) {
				if (singleFeature.getFeature(attribute) == value) {
					newValues.get(label).add(singleFeature);
					count++;
				}
			}
		}
		return new ValueSize(count, newValues);
	}
	
	/**
	 * compute the entropy of a given set of values
	 * @param values the values of data
	 * @param attribute the attribute that the entropy would be computed
	 * @return the entropy of the attribute
	 */
	private double computeEntropySingleAttribute(ValueSize values, int attribute) {
		double totalEntropy = 0.0;
		for (int i = 0; i < NUM_ATTRIBUTE; i++) {
			double entropy = 0.0;
			ValueSize valueSize = getFeaturesOfValue(attribute, i, values.values);
			double p1 = (double) valueSize.size / values.size;
			for (int j = 0; j < NUM_LABEL; j++) {
				double p2;
				if (valueSize.values.containsKey(j)) {
					if (valueSize.values.get(j).size() != 0) {
						p2 = (double) valueSize.values.get(j).size() / (double) valueSize.size;
						entropy += -p2 * Math.log(p2) / Math.log(2);
					}
				}
			}
			totalEntropy += p1 * entropy;
		}
		return totalEntropy;
	}
}