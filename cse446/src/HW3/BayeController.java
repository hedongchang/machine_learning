package HW3;

import java.util.*;

public class BayeController {
	public static void main(String[] args) {
		HashMap<String, Integer> spamData = new HashMap<String, Integer>();
		HashMap<String, Integer> hamData = new HashMap<String, Integer>();
		long[] result = DataParser.parseData(spamData, hamData);
		System.out.println(" the percentage of spam: " + 100.0 * result[0] / (result[0] + result[1]) + "%");
		Map<String, Double> spamPercent = BayeModel.getPercentage(spamData, result[2]);
		for (String word: spamPercent.keySet()) {
			System.out.println(word + " " + spamPercent.get(word) +" percent");
		}
		Map<String, Double> hamPercent = BayeModel.getPercentage(hamData, result[3]);
	}
}
