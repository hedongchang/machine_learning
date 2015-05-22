package HW1;

import java.util.*;

/**
 * A controller for ID3 tree operations
 * @author Dongchang
 */
public class ID3Controller {
	public static void main(String[] args) {
		// parse the data files
		HashMap<Integer, List<Features>> trainData = new HashMap<Integer, List<Features>>();
		HashMap<Integer, List<Features>> testData = new HashMap<Integer, List<Features>>();
		DataParser.ParseData(trainData, testData);
		
		// different thresholds for criterion set
		double[] pvalues = {0.01, 0.05, 1};
		
		for (double pValue: pvalues) {
			// constructs a new ID3 tree
			ID3Tree idtree = new ID3Tree(trainData, pValue);
			// prints out the total number of nodes in the tree
			System.out.println(idtree.nodeNum);
			// map stores each path of the tree and its corresponding count
			HashMap<List<Integer>, Integer> map = new HashMap<List<Integer>, Integer>();
			// labels stores each path of the tree and its corresponding label
			HashMap<List<Integer>, Set<Integer>> labels = new HashMap<List<Integer>, Set<Integer>>();
			// get the prediction accuracy for test data
			System.out.println("The percentage of correct prediction for test data is " 
					+ calculatePredict(testData, idtree, 8000, map, labels) + "\n");
			
			// sort stores the count of each path and the path itself
			// it helps to sort the path according to its count
			TreeMap<Integer, List<List<Integer>>> sort = 
					new TreeMap<Integer, List<List<Integer>>>();
			// stores the counts of paths to help sort them
			TreeSet<Integer> keys = new TreeSet<Integer>();
			for (List<Integer> list: map.keySet()) {
				int count = map.get(list);
				if (!sort.containsKey(count)) {
					sort.put(count, new ArrayList<List<Integer>>());
					keys.add(count);
				}
				sort.get(count).add(list);
			}
			// find all the information about five paths that has most counts
			// in a given ID3 tree
			int i = 1;
			while (i <= 5) {
				// get the current most counts
				int count = keys.last();
				for (List<Integer> list: sort.get(count)) {
					// examine information in each path
					for (int result: list) {
						if (result < 0) {
							// an negative value indicates it would go left next
							System.out.print("left ");
							result = - result;
						} else {
							// a positive value indicates it goes right next 
							// from the current node
							System.out.print("right ");
						}
						// the first two digits of result store the attribute
						// the last two digits of result store the threshold
						System.out.print(result/ 100 + " " + result % 100 + " ");
					}
					System.out.println();
				}
				// get the labels for each path
				for (List<Integer> list: sort.get(count)) {
					for (int label: labels.get(list)) {
						System.out.print(label + " ");
					}
					System.out.println();
				}
				// remove the current most counts of path
				keys.remove(count);
				// increment the number of path examined
				i += sort.get(count).size();
				// 
				System.out.println(count + "\n");
			}
			// get the prediction accuracy for training data
			System.out.println("The percentage of correct prediction for train data is " 
					+ calculatePredict(trainData, idtree, 12000, map, labels) + "\n");
		}	
	}
	
	/**
	 * help to calculate the percentage of accurate predictions
	 * @param data the values
	 * @param idtree the ID3 tree
	 * @param totalNum total number of 
	 * @param map store the path and its count
	 * @param labels store the path and its labels
	 * @return the percentage of accurate predictions
	 */
	private static double calculatePredict(HashMap<Integer, List<Features>> data, 
			ID3Tree idtree, int totalNum, HashMap<List<Integer>, Integer> map, 
			HashMap<List<Integer>, Set<Integer>> labels) {
		int correctCount = 0;
		for (Integer label: data.keySet()) {
			List<Features> feature = data.get(label);
			for (Features singleFeature: feature) {
				List<Integer> path = new ArrayList<Integer>();
				int result = idtree.predict(singleFeature, idtree.overallNode, map, path);
				if (result == label) {
					// if the prediction is correct,
					// increment the correct count and 
					// store the path and its label in labels
					if (!labels.containsKey(path)) {
						labels.put(path, new HashSet<Integer>());
					}
					labels.get(path).add(label);
					correctCount++;
				}
			}
		}
		// calculate the percentage
		return (double) 100 * correctCount / totalNum;
	}
}
