package cse446;

import java.util.*;

public class ID3Controller {
	public static void main(String[] args) {
		HashMap<Integer, List<Features>> trainData = new HashMap<Integer, List<Features>>();
		HashMap<Integer, List<Features>> testData = new HashMap<Integer, List<Features>>();
		DataParser.ParseData(trainData, testData);
		/*// the data parser should work fine
		for (int label: trainData.keySet()) {
			List<Features> features = trainData.get(label);
			for (Features feature: features) {
				System.out.println(label + " " + feature);
			}
		}*/
		ID3Tree idtree = new ID3Tree(trainData);
		//idtree.findAttribute(trainData);
		int correctCount = 0;
		for (Integer label: testData.keySet()) {
			List<Features> feature = testData.get(label);
			for (Features singleFeature: feature) {
				int result = idtree.predict(singleFeature, idtree.overallNode);
				if (result == label) {
					correctCount++;
				}
			}
		}
		System.out.println(idtree.attributeNum);
		System.out.println("The percentage of correct prediction is " 
				+ (double) 100 * correctCount / 8000.0);
	}
}
