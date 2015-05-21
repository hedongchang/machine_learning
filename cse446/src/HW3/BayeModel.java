package HW3;

import java.util.*;

public class BayeModel {
	
	public int[] result;
	public Map<String, Integer> spamData;
	public Map<String, Integer> hamData;
	public Map<String, Double> spamPercent;
	public Map<String, Double> hamPercent;
	public final int FACTOR = 1;
	public int m;
	
	public BayeModel(int[] result, Map<String, Integer> spamData, Map<String, Integer> hamData) {
		this.spamData = spamData;
		this.hamData = hamData;
		this.result = result;
		this.m = getM();
		this.spamPercent = getPercentage(m, 0);
		this.hamPercent = getPercentage(m, 1);
	}
	
	public Map<String, Double> getPercentage(int m, int indicator) {
		Map<String, Integer> data;
		int totalCount;
		if (indicator == 0) {
			data = spamData;
			totalCount = result[2];
		} else {
			data = hamData;
			totalCount = result[3];
		}
		Map<String, Double> wordPercent = new HashMap<String, Double>();
		for (String word: data.keySet()) {
			wordPercent.put(word, (double) (data.get(word) + FACTOR) / (totalCount + FACTOR * m));
		}
		return wordPercent;
	}
	
	public int getM() {
		Set<String> words = new HashSet<String>();
		words.addAll(spamData.keySet());
		words.addAll(hamData.keySet());
		return words.size();
	}
	
	public PriorityQueue<Pair<String, Double>> getTopFive(int indicator) {
		Map<String, Double> data;
		if (indicator == 0) {
			data = spamPercent;
		} else {
			data = hamPercent;
		}
		Comparator<Pair<String, Double>> cmp = new PairComparator();
		PriorityQueue<Pair<String, Double>>  top5 = new PriorityQueue<Pair<String, Double>>(5, cmp);
		int count = 0;
		for (String word: data.keySet()) {
			double percent = data.get(word);
			if (count < 5) {
				top5.add(new Pair<String, Double>(word, percent));
			} else {
				Pair<String, Double> min = top5.peek();
				if (percent > min.b) {
					top5.remove();
					top5.add(new Pair<String, Double>(word, percent));
				}
			}
			count++;
		}
		return top5;
	}
	
	public String predict(String[] lineInput, double spamTotal, double hamTotal) {
		double spamRes = spamTotal;
		double hamRes = hamTotal;
		for (int i = 2; i < lineInput.length; i += 2) {
			String word = lineInput[i];
			double spamSingle = Math.log(1.0 * FACTOR / (result[2] + m * FACTOR));
			double hamSingle = Math.log(1.0 * FACTOR / (result[3] + m * FACTOR));
			int count = Integer.parseInt(lineInput[i + 1]);
			if (spamPercent.containsKey(word)) {
				spamSingle = Math.log(spamPercent.get(word));
			}
			if (hamPercent.containsKey(word)) {
				hamSingle = Math.log(hamPercent.get(word));
			}	
			spamRes += (count * spamSingle);
			hamRes += (count * hamSingle);
		}
		if (spamRes < hamRes) {
			return "ham";
		}
		return "spam";
	}
}
