package HW4;

import java.util.*;

import HW1.Features;;

/**
 * Implement a bagging algorithm that increase the accuracy of ID3 tree
 * @author Dongchang
 */
public class P3Controller {
	public static final int NUM_TREE = 25;

	public static void main(String[] args) {
		HashMap<Integer, List<Features>> trainData = new HashMap<Integer, List<Features>>();
		HashMap<Integer, List<Features>> testData = new HashMap<Integer, List<Features>>();
		int result[] = new int[2];
		List<HW1.Pair> retVal = DataParserP3.ParseData(trainData, testData, result);
		int[][] preds = new int[result[1]][NUM_TREE];
		int[] classes = new int[result[1]];
		for (int i = 1; i <= 10; i++) {
			// construct bagging from various number of samples
			HashSet<ID3Tree> trees = new HashSet<ID3Tree>();
			for (int j = 1; j <= NUM_TREE; j++) {
				// select 80 samples from data values
				HashMap<Integer, List<Features>> newTrainData = selectSample(retVal, result[0]);
				// train a tree with each training data
				trees.add(new ID3Tree(newTrainData, i));
				storePredict(testData, trees, classes, preds);
			}
			System.out.println("depth: " + i + " accuracy: " + predictResult(classes, preds) + "%");
			double[] biasvar = DataUtility.biasvar(classes, preds, result[1], NUM_TREE);
			System.out.println("bias: " + biasvar[1] + " variance: " + biasvar[2] + "\n");
		}
	}
	
	public static double predictResult(int[] classes, int[][] preds) {
		int correctCount = 0;
		for (int i = 0; i < classes.length; i++) {
			int label = classes[i];
			int temp = getMostCount(preds[i]);
			if (label == temp) {
				correctCount++;
			}
		}
		return 100.0 * correctCount / classes.length;
	}
	
	public static int getMostCount(int[] preds) {
		HashMap<Integer, Integer> resultCount = new HashMap<Integer, Integer>();
		for (int pred: preds) {
			if (!resultCount.containsKey(pred)) {
				resultCount.put(pred, 1);
			} else {
				int count = resultCount.get(pred);
				count++;
				resultCount.put(pred, count);
			}
		}
		int max = 0;
		int maxResult = 0;
		for (int result: resultCount.keySet()) {
			int count = resultCount.get(result);
			if (count > max) {
				max = count;
				maxResult = result;
			}
		}
		return maxResult;
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
	private static void storePredict(HashMap<Integer, List<Features>> data, 
			HashSet<ID3Tree> trees, int[] classes, int[][] predicts) {
		int testIndex = 0;
		for (Integer label: data.keySet()) {
			List<Features> feature = data.get(label);
			for (Features singleFeature: feature) {
				// for a feature, find its predict in every tree
				int treeCount = 0;
				for (ID3Tree idtree: trees) {
					int temp = idtree.predict(singleFeature, idtree.overallNode);
					predicts[testIndex][treeCount] = temp;
					treeCount++;
				}
				classes[testIndex] = label;
				testIndex++;
			}
		}
	}
	
	/**
	 * Random select 80 values with duplicates in a map
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
