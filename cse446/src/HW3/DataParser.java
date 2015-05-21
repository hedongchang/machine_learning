package HW3;

import java.io.*;
import java.util.*;

public class DataParser {
	public static int[] parseData(Map<String, Integer> spamData, Map<String, Integer> hamData) {
		File train = new File("train.txt");
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
					spamCount++;
					spamWord += parseMap(lineInput, spamData);
				} else {
					hamCount++;
					hamWord += parseMap(lineInput, hamData);
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new int[] {spamCount, hamCount, spamWord, hamWord};
	}
	
	public static int parseMap(String[] lineInput, Map<String, Integer> data) {
		int totalCount = 0;
		for (int i = 2; i < lineInput.length; i = i + 2) {
			String word = lineInput[i];
			int count = Integer.parseInt(lineInput[i + 1]);
			if (!data.containsKey(word)) {
				data.put(word, count);
			} else {
				int oldCount = data.get(word);
				data.put(word, oldCount + count);
			}
			totalCount += count;
		}
		return totalCount;
	}
}
