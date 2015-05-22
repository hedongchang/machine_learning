package HW2;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
/**
 * The model for the collaborative filtering.
 * @author Dongchang
 *
 */
public class CollabFilter {
	public float[][] cache; // store weights between users
		
	public HashMap<Integer, HashMap<Integer, Integer>> trainData; // a map from user id to a map from movie id to rating
	public HashMap<Integer, HashMap<Integer, Integer>> movieUser; // a map from movie id to a map from user id to rating
	
	public HashMap<Integer, Integer> userIndex; // a map from user id to its index in trainUser array
	public int[] trainUser; // an array of training users
	public HashMap<Integer, Double> userAverage; // a map from user id to its average ratings of all movies
	
	public static ForkJoinPool fjPool = new ForkJoinPool();
	// the fork join pool for the parallel computing
	
	/**
	 * Constructs CollabFilter
	 * @param trainData the map from user id to a map from movie id to rating
	 * @param movieUser the map from movie id to a map from user id to rating
	 * @param trainUser an array of all training user id
	 * @param userIndex a map from user to its index in trainUser array
	 */
	public CollabFilter(HashMap<Integer, HashMap<Integer, Integer>> trainData,
			HashMap<Integer, HashMap<Integer, Integer>> movieUser, int[] trainUser,
			HashMap<Integer, Integer> userIndex) {
		System.out.println("construct average...");
		this.trainUser = trainUser;
		this.trainData = trainData;
		this.movieUser = movieUser;
		this.userIndex = userIndex;
		this.userAverage = computeAverage();
		this.cache = new float[trainUser.length][trainUser.length];
		System.out.println("construct weight...");
		constructCache(this.cache);
		System.out.println("start predict...");
	}
	
	/**
	 * Store all weights between users into cache
	 * @param cache the storage of all weights between users
	 */
	private void constructCache(float[][] cache) {
		fjPool.invoke(new ComputeCache(0, trainUser.length, cache, userAverage, trainData, trainUser));
	}
	
	/**
	 * Predict the rating given user id and a movie ids
	 * @param userId user id of the prediction
	 * @param movieId movie id of the prediction
	 * @return the predict of the rating
	 */
	public double predict(int userId, int movieId) {
		double userAvg = userAverage.get(userId);
		double sumWeight = 0;
		double partial = 0;
		double weight = 0;
		if (!movieUser.containsKey(movieId)) {
			return userAvg;
		}
		for (int user2Id: movieUser.get(movieId).keySet()) {
			weight = cache[userIndex.get(userId)][userIndex.get(user2Id)];		
			partial += (weight * (trainData.get(user2Id).get(movieId) - userAverage.get(user2Id)));
			sumWeight += Math.abs(weight);
		}
		if (sumWeight != 0 && !Double.isNaN(userAvg + (1 / sumWeight) * partial)) {
			return userAvg + (1 / sumWeight) * partial;
		} else {
			return userAvg;
		}
	}
	
	/**
	 * Compute user id map to the user's average ratings of all movies
	 * @return a map from user id to the user's average ratings of all movies
	 */
	private HashMap<Integer, Double> computeAverage() {
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		for (int userId: trainData.keySet()) {
			double totalRating = 0.0;
			for (int movieId: trainData.get(userId).keySet()) {
				totalRating += trainData.get(userId).get(movieId);
			}
			double avgRating = (double) totalRating / trainData.get(userId).keySet().size();
			result.put(userId, avgRating);
		}
		return result;
	}
}
