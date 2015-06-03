package HW4;

import java.util.*;
import HW1.*;

/**
 * A controller for ID3 tree operations
 * @author Dongchang
 */
public class ID3Controller {
	public static void main(String[] args) {
		// parse the data files
		HashMap<Integer, List<Features>> trainData = new HashMap<Integer, List<Features>>();
		HashMap<Integer, List<Features>> testData = new HashMap<Integer, List<Features>>();
		int[] totalSize = new int[2];
		DataParserP3.ParseData(trainData, testData, totalSize);

		// constructs a new ID3 tree
		for (int i = 1 ; i <= 10; i++) {
			ID3Tree idtree = new ID3Tree(trainData, i);
			// prints out the total number of nodes in the tree
			System.out.println("total number of nodes is " + idtree.nodeNum);
			// labels stores each path of the tree and its corresponding label
			HashMap<List<Integer>, Set<Integer>> labels = new HashMap<List<Integer>, Set<Integer>>();
			// get the prediction accuracy for test data
			System.out.println("The percentage of correct prediction for test data is " 
					+ calculatePredict(testData, idtree, totalSize[1], labels) + "%");
			// get the prediction accuracy for training data*/
			System.out.println("The percentage of correct prediction for train data is " 
					+ calculatePredict(trainData, idtree, totalSize[0], labels) + "%\n");
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
			ID3Tree idtree, int totalNum, HashMap<List<Integer>, Set<Integer>> labels) {
		int correctCount = 0;
		for (Integer label: data.keySet()) {
			List<Features> feature = data.get(label);
			for (Features singleFeature: feature) {
				int result = idtree.predict(singleFeature, idtree.overallNode);
				if (result == label) {
					// if the prediction is correct,
					// increment the correct count and 
					// store the path and its label in labels
					correctCount++;
				}
			}
		}
		// calculate the percentage
		return (double) 100 * correctCount / totalNum;
	}
}