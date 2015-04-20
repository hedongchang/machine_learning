package cse446;

import java.util.*;

public class ID3Controller {
	public static void main(String[] args) {
		HashMap<Integer, List<Features>> trainData = new HashMap<Integer, List<Features>>();
		HashMap<Integer, List<Features>> testData = new HashMap<Integer, List<Features>>();
		DataParser.ParseData(trainData, testData);
		
		double[] pvalues = {0.05, 0.5, 1};

		
		for (double pvalue: pvalues) {
			ID3Tree idtree = new ID3Tree(trainData, pvalue);
			System.out.println("total number of nodes is " + idtree.nodeNum);
			System.out.println("The percentage of correct prediction for test data is " 
					+ calculatePredict(testData, idtree, 8000));
			System.out.println("The percentage of correct prediction for train data is " 
					+ calculatePredict(trainData, idtree, 12000) + "\n");
		}
	}
	
	private static double calculatePredict(HashMap<Integer, List<Features>> data, 
			ID3Tree idtree, int totalNum) {
		int correctCount = 0;
		for (Integer label: data.keySet()) {
			List<Features> feature = data.get(label);
			for (Features singleFeature: feature) {
				int result = idtree.predict(singleFeature, idtree.overallNode);
				if (result == label) {
					correctCount++;
				}
			}
		}
		return (double) 100 * correctCount / totalNum;
	}
}
