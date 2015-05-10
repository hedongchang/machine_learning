package hw2_duplicate;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class CollabFilter {
	
	public static final int TRAIN_USER_NUM = 28978;
	
	public float[][] cache = new float[TRAIN_USER_NUM][TRAIN_USER_NUM];
		
	public HashMap<Integer, HashMap<Integer, Integer>> trainData;
	public HashMap<Integer, HashMap<Integer, Integer>> movieUser;
	public HashMap<Integer, Integer> userIndex;
	public int[] trainUser;
	public HashMap<Integer, Double> userAverage;
	
	public static ForkJoinPool fjPool = new ForkJoinPool();
	
	public CollabFilter(HashMap<Integer, HashMap<Integer, Integer>> trainData,
			HashMap<Integer, HashMap<Integer, Integer>> movieUser, int[] trainUser,
			HashMap<Integer, Integer> userIndex) {
		System.out.println("construct average...");
		this.trainUser = trainUser;
		this.trainData = trainData;
		this.movieUser = movieUser;
		this.userIndex = userIndex;
		this.userAverage = computeAverage();
		System.out.println("construct weight...");
		constructCache(this.cache);
		System.out.println("start predict...");
	}
	
	private void constructCache(float[][] cache) {
		fjPool.invoke(new ComputeCache(0, TRAIN_USER_NUM, cache, userAverage, trainData, trainUser));
	}
	
	public double predict(int userId, int movieId) {
		double userAvg = userAverage.get(userId);
		double sumWeight = 0;
		double partial = 0;
		double weight = 0;
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
