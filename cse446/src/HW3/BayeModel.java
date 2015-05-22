package HW3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * A model for Naive Bayesian learning
 * @author Dongchang
 *
 */
public class BayeModel {
	public static final String PRE_FIX = "data/trec05p-1/data";
	// prefix for extra credit data file path
	public static final boolean EC = false;
	// whether to do extra credit or not
	public static final double EC_THESHOLD = 2.5;
	// the 
	public int[] result;
	//an array to represent spam counts, ham counts, spam words counts
	public Map<String, Integer> spamData;
	// a map from spam words to their counts
	public Map<String, Integer> hamData;
	// a map from ham words to their counts
	public Map<String, Double> spamPercent;
	// a map from spam words to their percentage
	public Map<String, Double> hamPercent;
	// a map from ham words to their percentage

	public final int FACTOR = 1;
	// a multiplying factor for m
	public int m;
	// the changing constant in m-estimate
	
	/**
	 * Constructs a model for Naive Bayesian learnings
	 * @param result an array to represent spam counts, ham counts, spam words counts
	 * @param spamData a map from spam words to their counts
	 * @param hamData a map from ham words to their counts
	 */
	public BayeModel(int[] result, Map<String, Integer> spamData, Map<String, Integer> hamData) {
		this.spamData = spamData;
		this.hamData = hamData;
		this.result = result;
		// get the value of m
		this.m = getM();
		// get percentage for either spam or ham words
		this.spamPercent = getPercentage(m, 0);
		this.hamPercent = getPercentage(m, 1);
		if (EC) {
			// remove rare words
			removeRare(0, 3);
			removeRare(1, 3);
		}
	}
	
	/**
	 * Remove rare words in data
	 * @param indicator to indicate use spam data or ham data
	 * @param threshold the threshold to indicate whether the
	 * word is rare
	 */
	public void removeRare(int indicator, int threshold) {
		Map<String, Integer> data;
		Map<String, Double> removeData;
		if (indicator == 0) {
			data = spamData;
			removeData = spamPercent;
		} else {
			data = hamData;
			removeData = hamPercent;
		}
		for (String word: data.keySet()) {
			if (data.get(word) <= threshold) {
				removeData.remove(word);
			}
		}
	}
	
	/**
	 * Get the percentage for either spam or ham words
	 * @param m the value in m-estimate
	 * @param indicator indicates whether to compute spam or ham data
	 * @return a map from a certain words to their percentage
	 */
	public Map<String, Double> getPercentage(int m, int indicator) {
		Map<String, Integer> data;
		int totalCount;
		if (indicator == 0) {
			// indicates a spam data
			data = spamData;
			totalCount = result[2];
		} else {
			// indicates a ham data
			data = hamData;
			totalCount = result[3];
		}
		Map<String, Double> wordPercent = new HashMap<String, Double>();
		// compute percentage for every word
		for (String word: data.keySet()) {
			wordPercent.put(word, (double) (data.get(word) + FACTOR) / (totalCount + FACTOR * m));
		}
		return wordPercent;
	}
	
	/**
	 * Get the m value in m-estimate
	 * @return the m value in m-estimate, which is the count for unique words
	 */
	public int getM() {
		Set<String> words = new HashSet<String>();
		// get the unique words in spam and ham
		words.addAll(spamData.keySet());
		words.addAll(hamData.keySet());
		return words.size();
	}
	
	/**
	 * Get the top five frequent words in data
	 * @param indicator to indicate whether the data is spam or ham
	 * @return the top five frequent words in data
	 */
	public PriorityQueue<Pair<String, Double>> getTopFive(int indicator) {
		Map<String, Double> data;
		if (indicator == 0) {
			// indicate a spam data
			data = spamPercent;
		} else {
			// indicate a ham data
			data = hamPercent;
		}
		Comparator<Pair<String, Double>> cmp = new PairComparator();
		PriorityQueue<Pair<String, Double>>  top5 = new PriorityQueue<Pair<String, Double>>(5, cmp);
		int count = 0;
		// get the top five frequent words in the data
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
	
	/**
	 * Predict whether an e-mail is spam or not
	 * @param lineInput an e-mail input
	 * @param spamRes a probability that the e-mail is a spam
	 * @param hamRes a probability that the e-mail is a ham
	 * @return a "spam" if it is a spam and "ham" if it is a ham
	 */
	public String predict(String[] lineInput, double spamRes, double hamRes) {
		String ret;
		for (int i = 2; i < lineInput.length; i += 2) {
			String word = lineInput[i];
			// if the e-mail doesn't contain the word, use m-estimate to compute the
			// probability for the word
			double spamSingle = Math.log(1.0 * FACTOR / (result[2] + m * FACTOR));
			double hamSingle = Math.log(1.0 * FACTOR / (result[3] + m * FACTOR));
			int count = Integer.parseInt(lineInput[i + 1]);
			// get the conditional probability if data contains the words
			if (spamPercent.containsKey(word)) {
				spamSingle = Math.log(spamPercent.get(word));
			}
			if (hamPercent.containsKey(word)) {
				hamSingle = Math.log(hamPercent.get(word));
			}	
			spamRes += (count * spamSingle);
			hamRes += (count * hamSingle);
		}
		if (EC) {
			// perform extra credit performance
			double diff = Math.abs(spamRes - hamRes);
			if (diff <= EC_THESHOLD) {
				// if the two predictions are close, search for keywords
				return scanEmail(lineInput[0], spamRes - hamRes);
			}
		}
		if (spamRes < hamRes) {
			// if the possibility of ham is bigger, return ham
			ret = "ham";
		} else {
			// if the possibility of spam is bigger, return spam
			ret = "spam";
		}
		return ret;
	}
	
	/**
	 * Scan the original e-mail and search for keywords to determine whether is
	 * spam or not
	 * @param emailId the id for a e-mail
	 * @param diff to indicate the original prediction is spam or not
	 * @return the prediction
	 */
	private static String scanEmail(String emailId, double diff) {
		String filePath = PRE_FIX + emailId;
		String result = "";
		try {
			@SuppressWarnings("resource")
			// read the original e-mail
			Scanner input = new Scanner(new File(filePath));
			while (input.hasNext()) {
				String word = input.next().toLowerCase();
				if (diff > 0) {
					// if the original prediction is spam
					if (word.contains("edu")) {
						// if the email is sent from an edu domain,
						// it might be ham
						return "ham";
					}
					if (word.contains("attach")) {
						// if the email has attachment,
						// it might be ham
						String nextWord = input.next();
						if (nextWord.contains("yes")) {
							return "ham";
						}
					}
					if (word.contains("from")) {
						// if the email is sent from a actual name,
						// it might be ham
						String nextWord = input.next();
						if (nextWord.contains(",")) {
							return "ham";
						}
					}
				} else {
					if (word.contains("!!!") || word.contains("free") || word.contains("money") ||
							word.contains("$")) {
						// if the email contains some spamming keywords,
						// it might be spam
						return "spam";
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}
