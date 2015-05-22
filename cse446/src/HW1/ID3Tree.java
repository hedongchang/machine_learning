package HW1;

import java.util.*;

public class ID3Tree {
	
	// the number of attributes in values
	public static final int NUM_ATTRIBUTE = 16;
	// the number of labels in values
	public static final int NUM_LABEL = 26;
	
	// the number of attributes that have been split
	public int nodeNum;
	// the threshold for criterion set
	public double pvalue;
	// the overall node of the tree
	public ID3TreeNode overallNode;
	
	/**
	 * constructs a new ID3Tree
	 * @param values the values of data
	 */
	public ID3Tree(HashMap<Integer, List<Features>> values, double pvalue) {
		ValueSize valueSize = new ValueSize(12000, values);
		this.pvalue = pvalue;
		this.overallNode = growTree(valueSize);
	}
	
	/**
	 * Grow a ID3 tree
	 * @param values the values of data
	 * @return a updated ID3 tree node
	 */
	public ID3TreeNode growTree(ValueSize values) {
		int attribute = findAttribute(values);
		int threshold = findThreshold(attribute, values.values);
		nodeNum++;
		
		// split the current data into two parts based on the newly calculate
		// attribute and theshold
		ValueSize leftValues = splitValues(attribute, threshold, values.values, true);
		ValueSize rightValues = splitValues(attribute, threshold, values.values, false);
		
		if (sameLabel(values.values)) {
			// the whole data is pure
			return new ID3TreeNode(values.values, null, null, attribute, threshold);
		} else if (isNotRelevant(attribute, values.values, leftValues, rightValues)) {
			// the attribute is not relevant
			return new ID3TreeNode(values.values, null, null, attribute, -1);
		} else if (sameLabel(leftValues.values)) {
			// left split is pure
			return new ID3TreeNode(leftValues.values, null, growTree(rightValues), attribute, threshold);
		} else if (sameLabel(rightValues.values)) {
			// right split is pure
			return new ID3TreeNode(rightValues.values, growTree(leftValues), null, attribute, threshold);
		} else {
			return new ID3TreeNode(values.values, growTree(leftValues), growTree(rightValues), attribute, threshold);
		}
	}
	
	/**
	 * predict the label of a given list of Features
	 * @param Features a single feature of a value
	 * @param node the updated ID3 tree node
	 * @param path the updated current path from root
	 * @return an integer that indicates the label of a given list of Features
	 */
	public int predict(Features features, ID3TreeNode node, HashMap<List<Integer>, Integer> map,
			List<Integer> path) {
		if (node.left == null && node.right == null) {
			insertMap(map, path);
			return findMostCommonLabel(node.classes);
		} else if (features.getFeature(node.attribute) < node.threshold) {
			if (node.left == null) {
				insertMap(map, path);
				return findMostCommonLabel(node.classes);
			}
			// negative values indicates goes left from the current node
			// first two digits store attribute and the last two store threshold 
			path.add(-node.attribute * 100 - node.threshold);
			return predict(features, node.left, map, path);
		} else {
			if (node.right == null) {
				insertMap(map, path);
				return findMostCommonLabel(node.classes);
			}
			// positive values indicates goes left from the current node
			// first two digits store attribute and the last two store threshold 
			path.add(node.attribute * 100 + node.threshold);
			return predict(features, node.right, map, path);
		}
	}
	
	/**
	 * Update path and its corresponding counts
	 * @param map map stores all paths and their correspond counts
	 * @param path the current path
	 */
	private static void insertMap(HashMap<List<Integer>, Integer> map, List<Integer> path) {
		if (!map.containsKey(path)) {
			map.put(path, 1);
		} else {
			int count = map.get(path);
			count++;
			map.put(path, count);
		}
	}
	
	/**
	 * find the most common labels of a given set of values
	 * @param features signle features of a value
	 * @return the most common label
	 */
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
	 * test whether an attribute is relevant based on Chi values 
	 * @param attribute the attribute to be examined
	 * @param values the data values
	 * @return whether an attribute is relevant
	 */
	private boolean isNotRelevant(int attribute,  HashMap<Integer, List<Features>> values, ValueSize leftValues,
			ValueSize rightValues) {
		double chiValue = Chi.critchi(pvalue, 25);
		double sValue = 0.0;
		for (int j = 0; j < NUM_ATTRIBUTE; j++) {
			ValueSize valueSize = getFeaturesOfValue(attribute, j, values);				
			double e1j = valueSize.size * leftValues.size / (double) (leftValues.size + rightValues.size);
			double e2j = valueSize.size * rightValues.size / (double) (leftValues.size + rightValues.size);
			ValueSize valueSize1 = getFeaturesOfValue(attribute, j, leftValues.values);
			ValueSize valueSize2 = getFeaturesOfValue(attribute, j, rightValues.values);
			if (e1j != 0 && e2j != 0) {
				double s1 = Math.pow(valueSize1.size - e1j, 2) / e1j;
				double s2 = Math.pow(valueSize2.size - e2j, 2) / e2j;
				sValue += s1 + s2;
			}
		}
		return sValue <= chiValue;
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
		(int attribute, int threshold, HashMap<Integer, List<Features>> values, boolean indicator) {
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
					if (singleFeatures.getFeature(attribute) < threshold) {
						newValues.get(label).add(singleFeatures);
						count++;
					}
				// the right split of the values
				} else {
					if (singleFeatures.getFeature(attribute) >= threshold) {
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
		int indicator = 0;
		for (int label: values.keySet()) {
			if (values.get(label).size() != 0) {
				indicator++;
			}
		}
		return indicator < 2;
	}
	
	/**
	 * find the threshold of a given attribute
	 * @param attribute the attribute whose theshold would be computed
	 * @param values the values of data
	 * @return the best theshold for a attribute
	 */
	public int findThreshold(int attribute, HashMap<Integer, List<Features>> values) {
		double minEntropy = Double.MAX_VALUE;
		int minThreshold = 0;
		for (int i = 0; i < NUM_ATTRIBUTE; i++) {
			double entropy = 0.0;
			ValueSize leftValues = splitValues(attribute, i, values, true);
			ValueSize rightValues = splitValues(attribute, i, values, false);
			if (leftValues.values.values().size() == 0 || rightValues.values.values().size() == 0) {
				continue;
			}
			double entropy1 = computeEntropySingleAttribute(leftValues, attribute);
			double entropy2 = computeEntropySingleAttribute(rightValues, attribute);
			entropy = entropy1 + entropy2;
			if (entropy < minEntropy) {
				minEntropy = entropy;
				minThreshold = i;
			}
		}
		return minThreshold;
	}
	
	/**
	 * find the best attribute of a given set of values
	 * @param values the valus of data
	 * @return the best attribute with least conditional entropy
	 */
	public int findAttribute(ValueSize values) {
		int minAttribute = 0;
		double minEntropy = Double.MAX_VALUE;
		for (int i = 1; i < 16; i++) {
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