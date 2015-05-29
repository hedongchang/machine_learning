package HW3;

import java.util.*;
import java.io.*;

/**
 * The class is a controller for Naive Bayesian learning.
 */
public class BayeController {
	
	public static final String FILENAME = "data/test";
	// the file name for test data set
	
	/**
	 * Control the Naive Byesian learning
	 */
	public static void main(String[] args) {
		Map<String, Integer> spamData = new HashMap<String, Integer>();
		// a map from spam words to their counts
		Map<String, Integer> hamData = new HashMap<String, Integer>();
		// a map from ham words to their counts
		int[] result = DataParser.parseData(spamData, hamData);
		// construct model
		BayeModel model = new BayeModel(result, spamData, hamData);
		// report training result
		reportTrain(result, spamData, hamData, model);
		// predict a list of e-mails to be spam or not
		predictSpam(FILENAME, model, result);
	}
	
	/**
	 * Predict a list of e-mails to be a spam or not
	 * @param fileName the test file name
	 * @param model the model for Naive Bayesian learning
	 * @param result an array to represent spam counts, ham counts, spam words counts
	 * and ham words counts
	 */
	private static void predictSpam(String fileName, BayeModel model, int[] result) {
		File testFile = new File(fileName);
		try {
			Scanner input = new Scanner(testFile);
			// compute the prior
			double spamTotal = Math.log((double) result[0] / (result[0] + result[1]));
			double hamTotal = Math.log((double) result[1] / (result[0] + result[1]));;
			int correctCount = 0;
			int totalCount = 0;
			// predict a single e-mail
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] lineInput = line.split(" ");
				String actual = lineInput[1];
				String predict = model.predict(lineInput, spamTotal, hamTotal);
				if (actual.equals(predict)) {
					// if the prediction matches, increment correct counts
					correctCount++;
				}
				totalCount++;
			}
			// report the accuracy
			System.out.println("\npercentage of correct predictions: " 
					+ 100.0 * correctCount / totalCount + "%");
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Report the training result
	 * @param result an array to represent spam counts, ham counts, spam words counts
	 * @param spamData a map from spam words to their counts
	 * @param hamData a map from ham words to their counts
	 * @param model a Naive Bayesian learning model
	 */
	private static void reportTrain(int[] result, Map<String, Integer> spamData,
			Map<String, Integer> hamData, BayeModel model) {
		// report the prior for spam
		System.out.println("the percentage of spam: " + 100.0 * result[0] / (result[0] + result[1]) + "%");
		Queue<Pair<String, Double>> spamTop5 = model.getTopFive(0);
		// report the top five frequent words in spam
		for (int i = 0; i < 5; i++) {
			Pair<String, Double> res = spamTop5.poll();
			System.out.println(res.a + " " + 100 * res.b + "%");
		}
		// report the prior for ham
		System.out.println("\nthe percentage of ham: " + 100.0 * result[1] / (result[0] + result[1]) + "%");
		// report the top five frequent words in ham
		Queue<Pair<String, Double>> hamTop5 = model.getTopFive(1);
		for (int i = 0; i < 5; i++) {
			Pair<String, Double> res = hamTop5.poll();
			System.out.println(res.a + " " + 100 * res.b + "%");
		}
	}
}