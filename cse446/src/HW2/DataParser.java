package HW2;

import java.util.*;
import java.io.*;

/**
 * Parse the data in given files
 * @author Dongchang
 */
public class DataParser {
	public static final String EXTRA = "movie_titles.txt";
	
	/**
	 * Parse the data of the given file
	 * @param the file name to be parsed
	 * @param trainData a map whose key is user id and values is a map from movie id to rating
	 * @param movieUser a map whose key is movie id and values is a map from user id to rating
	 * @return the number of training users
	 */
	public static int parseData(HashMap<Integer, HashMap<Integer, Integer>> trainData, 
			HashMap<Integer, HashMap<Integer, Integer>> movieUser, String file) {
		int count = 0;
		try {
			Scanner input = new Scanner(new File(file));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] features = line.split(",");
				int movieId = Integer.parseInt(features[0]);
				int userId = Integer.parseInt(features[1]);
				int rating = (int) Float.parseFloat(features[2]);
				if (!trainData.containsKey(userId)) {
					count++;
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
		return count;
	}
	
	/**
	 * Parse a map from movie id to its name
	 * @param movieIdName a map from movie id to its name
	 */
	public static void parseExtra(HashMap<Integer, String> movieIdName) {
		try {
			Scanner input1 = new Scanner(new File(EXTRA));
			while (input1.hasNextLine()) {
				String line = input1.nextLine();
				String[] data = line.split(",");
				int movieId = Integer.parseInt(data[0]);
				String name = data[2];
				movieIdName.put(movieId, name);
			}
			input1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
