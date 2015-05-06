package hw2_duplicate;

import java.util.*;
import java.io.*;

public class DataParser {
	public static void parseData(HashMap<Integer, HashMap<Integer, Integer>> trainData, 
			HashMap<Integer, HashMap<Integer, Integer>> movieUser) {
		try {
			Scanner input = new Scanner(new File("TrainingRatings.txt"));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] features = line.split(",");
				int movieId = Integer.parseInt(features[0]);
				int userId = Integer.parseInt(features[1]);
				int rating = (int) Float.parseFloat(features[2]);
				if (!trainData.containsKey(userId)) {
					trainData.put(userId, new HashMap<Integer, Integer>());
				}
				trainData.get(userId).put(movieId, rating);
				if (!movieUser.containsKey(movieId)) {
					movieUser.put(movieId, new HashMap<Integer, Integer>());
				}
				movieUser.get(movieId).put(userId, rating);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
