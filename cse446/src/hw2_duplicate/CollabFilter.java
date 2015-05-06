package hw2_duplicate;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class CollabFilter {
	
	public static final int TRAIN_USER_NUM = 28978;
		
	public HashMap<Integer, HashMap<Integer, Integer>> trainData;
	public HashMap<Integer, HashMap<Integer, Integer>> movieUser;
	public HashMap<Integer, Double> moviePartial;
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
		Double userAvg = userAverage.get(userId);
		Pair<Double, Double> result = fjPool.invoke(new ComputeWeight(0, trainUser.length, userId, testUser, movieId, trainUser, userAverage, trainData));
		Double partial = result.a;
		Double sumWeight = result.b;
		if (sumWeight != 0 && !Double.isNaN(userAvg + (1 / sumWeight) * partial)) {
			return userAvg + (1 / sumWeight) * partial;
		} else {
			return userAvg;
		}
	}
	
	private HashMap<Integer, Double> computeAverage() {
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		for (int userId: trainData.keySet()) {
			Double totalRating = 0.0;
			for (int movieId: trainData.get(userId).keySet()) {
				totalRating += trainData.get(userId).get(movieId);
			}
			double avgRating = (double) totalRating / trainData.get(userId).keySet().size();
			result.put(userId, avgRating);
		}
		return result;
	}
	
	public HashMap<Integer, Double> computePartial() {
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		for (int movieId: movieUser.keySet()) {
			Double partial = 0.0;
			for (int userId: movieUser.get(movieId).keySet()) {
				Double userAvg = userAverage.get(userId);
				int rating = movieUser.get(movieId).get(userId);
				partial += (rating - userAvg);
			}
			result.put(movieId, partial);
		}
		return result;
	}
}
