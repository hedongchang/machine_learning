package HW2;

import java.util.*;
import java.io.*;

/**
 * The controller for the collaborative filtering.
 * @author Dongchang
 *
 */
public class CollabMain {
	
	public static final boolean EXTRA = false;
	// whether to operate extra credit
		
	public static void main(String[] args) {
		// parse data
		System.out.println("parse data...");
		HashMap<Integer, HashMap<Integer, Integer>> trainData = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, HashMap<Integer, Integer>> movieUser = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> userIndex = new HashMap<Integer, Integer>();
		int trainUserNum;
		HashMap<Integer, String> movieIdName = new HashMap<Integer, String>();
		if (!EXTRA) {
			trainUserNum = DataParser.parseData(trainData, movieUser, "TrainingRatings.txt");
		} else {
			// extra credit uses different training data and parse extra movie names
			trainUserNum = DataParser.parseData(trainData, movieUser, "TrainingRatings2.txt");
			DataParser.parseExtra(movieIdName);
		}
		int[] trainUser = new int[trainUserNum];
		// copy training users into an array
		System.out.println("copy users...");
		int index = 0;
		for (int userId: trainData.keySet()) {
			trainUser[index] = userId;
			userIndex.put(userId, index);
			index++;
		}
		// construct a CollabFilter model
		CollabFilter filter = new CollabFilter(trainData, movieUser, trainUser, userIndex);
		try {
			double sumAbs = 0.0;
			double sumSqr = 0.0;
			int count = 0;
			if (!EXTRA) {
				PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
				Scanner input = new Scanner(new File("TestingRatings.txt"));
				// iterate through the input file and compute accuracies
				while (input.hasNextLine()) {
					String line = input.nextLine();
					String[] features = line.split(",");
					int movieId = Integer.parseInt(features[0]);
					int userId = Integer.parseInt(features[1]);
					int rating = (int) Double.parseDouble(features[2]);
					double predict = filter.predict(userId, movieId);
					sumAbs += Math.abs(predict - rating);
					sumSqr += Math.pow(predict - rating, 2);
					count++;
				}
			    // report the computing results
				writer.println("absolute " + sumAbs / count);
				writer.println("square " + Math.sqrt(sumSqr / count));
				System.out.println("absolute " + sumAbs / count);
				System.out.println("square " + Math.sqrt(sumSqr / count));
				writer.close();
				input.close();
			} else {
				// extra credit predicts the results of all movies given a id
				TreeMap<Double, Set<Integer>> ratingMovie = new TreeMap<Double, Set<Integer>>();
				for (int movie: movieIdName.keySet()) {
					double predict = filter.predict(99999999, movie);
					if (!ratingMovie.containsKey(predict)) {
						ratingMovie.put(predict, new HashSet<Integer>());
					}
					ratingMovie.get(predict).add(movie);
				}
				// prints the top 100 movies in the predictions
				int countEx = 0;
				for (double rating: ratingMovie.descendingKeySet()) {
					if (countEx <= 100) {
						System.out.println(rating + ": ");
						for (int movie: ratingMovie.get(rating)) {
							countEx++;
							System.out.println("   " + movie + " " + movieIdName.get(movie));
						}
					} else {
						break;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}