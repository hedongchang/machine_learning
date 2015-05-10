package hw2_version1;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class CollabFilter {
	
	public static final int TRAIN_USER_NUM = 28978;
	
	public static double[][] cache = new double[TRAIN_USER_NUM][TRAIN_USER_NUM];
		
	public HashMap<Integer, HashMap<Integer, Integer>> trainData;
	public HashMap<Integer, HashMap<Integer, Integer>> movieUser;
	public HashMap<Integer, Double> moviePartial;
	public HashMap<Integer, Integer> trainUserIndex;
	public HashMap<Integer, HashMap<Integer, Double>> usersWeight;
	public int[] trainUser;
	public HashMap<Integer, Double> userAverage;
	
	public static ForkJoinPool fjPool = new ForkJoinPool();
	
	public CollabFilter(HashMap<Integer, HashMap<Integer, Integer>> trainData,
			HashMap<Integer, HashMap<Integer, Integer>> movieUser, int[] trainUser) {
		this.trainUser = trainUser;
		this.trainData = trainData;
		this.movieUser = movieUser;
		this.userAverage = computeAverage();
		this.usersWeight = new HashMap<Integer, HashMap<Integer, Double>>();
	}
	
	public Double predict(int userId, int movieId, int testUser) {
		double userAvg = userAverage.get(userId);
		double sumWeight = 0;
		double partial = 0;
		double weight = 0;
		for (int user2Id: trainData.keySet()) {
			if (trainData.get(user2Id).containsKey(movieId)) {
				weight = 1;//cache[trainUserIndex.get(userId)][trainUserIndex.get(user2Id)];		
			}
			partial += (weight * (trainData.get(user2Id).get(movieId) - userAverage.get(user2Id)));
			sumWeight += Math.abs(weight);
		}
		if (sumWeight != 0 && !Double.isNaN(userAvg + (1 / sumWeight) * partial)) {
			return userAvg + (1 / sumWeight) * partial;
		} else {
			return userAvg;
		}
	}
	
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
