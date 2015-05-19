package HW3;

import java.util.*;

public class BayeModel {
	public static Map<String, Double> getPercentage(HashMap<String, Integer> data, long totalCount) {
		Map<String, Double> wordPercent = new HashMap<String, Double>();
		for (String word: data.keySet()) {
			wordPercent.put(word, 100.0 * data.get(word) / totalCount);
		}
		return wordPercent;
	}
}
