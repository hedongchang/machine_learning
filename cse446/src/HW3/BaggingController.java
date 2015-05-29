package HW3;

import java.util.*;
import HW1.*;

/**
 * Implement a bagging algorithm that increase the accuracy of ID3 tree
 * @author Dongchang
 */
public class BaggingController {
	public static final double[] NUM_P = {1, 0.05, 0.01};
	public static final int NUM_TREE = 40;
	public static void main(String[] args) {
		HashMap<Integer, List<Features>> trainData = new HashMap<Integer, List<Features>>();
		HashMap<Integer, List<Features>> testData = new HashMap<Integer, List<Features>>();
		List<HW1.Pair> retVal = HW1.DataParser.ParseData(trainData, testData);
		// run with different values for p
		for (double p: NUM_P) {
			// construct bagging from various number of samples
			for (int numTrain = 40; numTrain >= 30; numTrain--) {
				HashSet<ID3Tree> trees = new HashSet<ID3Tree>();
				for (int i = 1; i <= numTrain; i++) {
					// select 12000 samples from data values
					HashMap<Integer, List<Features>> newTrainData = selectSample(retVal, 12000);
					// train a tree with each training data
					trees.add(new ID3Tree(newTrainData, p));
				}
				// the accuracy of test data
				System.out.println("p-value " + p + " number of train " + numTrain + " accuracy " +
						calculatePredict(testData, trees, 8000, new HashMap<List<Integer>, Integer>()) 
						+ "%");
			}
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
			HashSet<ID3Tree> trees, int totalNum, HashMap<List<Integer>, Integer> map) {
		int correctCount = 0;
		for (Integer label: data.keySet()) {
			List<Features> feature = data.get(label);
			for (Features singleFeature: feature) {
				HashMap<Integer, Integer> resultCount = new HashMap<Integer, Integer>();
				// for a feature, find its predict in every tree
				for (ID3Tree idtree: trees) {
					List<Integer> path = new ArrayList<Integer>();
					int temp = idtree.predict(singleFeature, idtree.overallNode, map, path);
					if (!resultCount.containsKey(temp)) {
						//  store the labels and its counts
						resultCount.put(temp, 1);
					} else {
						//  store the labels and its counts
						int count = resultCount.get(temp);
						count++;
						resultCount.put(temp, count);
					}
				}
				// compute the most common label
				int result = getMostCount(resultCount);
				if (result == label) {
					// if the prediction is correct,
					// increment the correct count and 
					correctCount++;
				}
			}
		}
		return (double) 100 * correctCount / totalNum;
	}
	
	/**
	 * Get the most frequent prediction
	 * @param map map stores each path and its corresponding counts
	 * @return 
	 */
	private static int getMostCount(HashMap<Integer, Integer> map) {
		int max = 0;
		int maxResult = 0;
		for (int result: map.keySet()) {
			int count = map.get(result);
			if (count > max) {
				max = count;
				maxResult = result;
			}
		}
		return maxResult;
	}
	
	/**
	 * Random select 12000 values with duplicates in a map
	 * @param retVal retVal 
	 * @param numSample the number of sample data
	 * @return a map that store label and features
	 */
	private static HashMap<Integer, List<Features>> 
		selectSample(List<HW1.Pair> retVal, int numSample) {
		HashMap<Integer, List<Features>> retMap = new HashMap<Integer, List<Features>>();
		for (int i = 1; i <= numSample; i++) {
			int index = (int) Math.floor(numSample * Math.random());
			int label = retVal.get(index).label;
			Features feature = new Features(retVal.get(index).feature);
			if (!retMap.containsKey(label)) {
				retMap.put(label, new ArrayList<Features>());
			}
			retMap.get(label).add(feature);
		}
		return retMap;
	}
}
