package HW3;

import java.util.*;
import java.io.*;

public class BayeController {
	public static void main(String[] args) {
		Map<String, Integer> spamData = new HashMap<String, Integer>();
		Map<String, Integer> hamData = new HashMap<String, Integer>();
		int[] result = DataParser.parseData(spamData, hamData);
		BayeModel model = new BayeModel(result, spamData, hamData);
		reportTrain(result, spamData, hamData, model);
		predictSpam("test.txt", model, result);
	}
	
	private static void predictSpam(String fileName, BayeModel model, int[] result) {
		File testFile = new File(fileName);
		try {
			Scanner input = new Scanner(testFile);
			double spamTotal = Math.log((double) result[0] / (result[0] + result[1]));
			double hamTotal = Math.log((double) result[1] / (result[0] + result[1]));;
			int correctCount = 0;
			int totalCount = 0;
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] lineInput = line.split(" ");
				String actual = lineInput[1];
				String predict = model.predict(lineInput, spamTotal, hamTotal);
				if (actual.equals(predict)) {
					correctCount++;
				}
				totalCount++;
			}
			System.out.println("\npercentage of correct predictions: " + 100.0 * correctCount / totalCount + "%");
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void reportTrain(int[] result, Map<String, Integer> spamData,
			Map<String, Integer> hamData, BayeModel model) {
		System.out.println("the percentage of spam: " + 100.0 * result[0] / (result[0] + result[1]) + "%");
		Queue<Pair<String, Double>> spamTop5 = model.getTopFive(0);		
		for (int i = 0; i < 5; i++) {
			Pair<String, Double> res = spamTop5.poll();
			System.out.println(res.a + " " + res.b);
		}
		System.out.println("\nthe percentage of ham: " + 100.0 * result[1] / (result[0] + result[1]) + "%");
		Queue<Pair<String, Double>> hamTop5 = model.getTopFive(1);
		for (int i = 0; i < 5; i++) {
			Pair<String, Double> res = hamTop5.poll();
			System.out.println(res.a + " " + res.b);
		}
	}
}
