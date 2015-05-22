package HW3;

import java.io.*;
import java.util.*;

/**
 * Parse the given data file
 * @author Dongchang
 */
public class DataParser {
	public static final String FILENAME = "data/train";
	// the file name for training
	
	/**
	 * Parse the given file
	 * @param spamData a map from spam words to their counts
	 * @param hamData a map from ham words to their counts
	 * @return an array to represent spam counts, ham counts, spam words counts
	 */
	public static int[] parseData(Map<String, Integer> spamData, Map<String, Integer> hamData) {
		File train = new File(FILENAME);
		int spamCount = 0;
		int hamCount = 0;
		int spamWord = 0;
		int hamWord = 0;
		try {
			Scanner input = new Scanner(train);
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] lineInput = line.split(" ");
				String indicator = lineInput[1];
				if (indicator.equals("spam")) {
					// increment the count for spam
					spamCount++;
					// store the words and count in spamData, increment the counts 
					// for spam words
					spamWord += parseMap(lineInput, spamData);
				} else {
					// increment the count for ham
					hamCount++;
					// store the words and count in hamData, increment the counts
					// for ham words
					hamWord += parseMap(lineInput, hamData);
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new int[] {spamCount, hamCount, spamWord, hamWord};
	}
	
	/**
	 * Store the words and their counts for a signle e-mail
	 * @param lineInput an representation of a single e-mail
	 * @param data a map from words to their counts
	 * @return the total count of words in a single e-mail
	 */
	public static int parseMap(String[] lineInput, Map<String, Integer> data) {
		int totalCount = 0;
		for (int i = 2; i < lineInput.length; i = i + 2) {
			String word = lineInput[i];
			int count = Integer.parseInt(lineInput[i + 1]);
			if (!data.containsKey(word)) {
				// if it doesn't contain word, put it in data
				data.put(word, count);
			} else {
				// if it contains the word, update the count
				int oldCount = data.get(word);
				data.put(word, oldCount + count);
			}
			// update the total count
			totalCount += count;
		}
		return totalCount;
	}
}