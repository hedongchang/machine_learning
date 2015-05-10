package hw2_duplicate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.*;

public class ComputeWeight extends RecursiveTask<Pair<Double, Double>> {
	public static final int TRAIN_USER_NUM = 28978;
	public static double[][] cache = new double[TRAIN_USER_NUM][TRAIN_USER_NUM];

	public int lo;
	public int hi;
	public int userId;
	public int testUser;
	public int movieId;
	public int[] trainUser;
	public HashMap<Integer, Double> userAverage;
	public HashMap<Integer, HashMap<Integer, Integer>> trainData;
	
	public static boolean defaultVoting = false;
	
	private static final long serialVersionUID = 1L;
	
	public ComputeWeight(int lo, int hi, int userId, int testUser, int movieId, int[] trainUser,
			HashMap<Integer, Double> userAverage, HashMap<Integer, HashMap<Integer, Integer>> trainData) {
		this.lo=lo;
		this.hi=hi;
		this.userId=userId;
		this.testUser=testUser;
		this.movieId=movieId;
		this.trainUser= trainUser;
		this.userAverage=userAverage;
		this.trainData = trainData;
	}

	@Override
	protected Pair<Double, Double> compute() {
		if (hi - lo <= 2000) {
			double partial = 0;
			double sumWeight = 0;
			for (int i = lo; i < hi; i++) {
				double weight = 1;
				int user2Id = trainUser[i];
				if (trainData.get(user2Id).containsKey(movieId) && user2Id != userId) {
					if (cache[testUser][i] == 0) {
						double nom = 0, denomLeft = 0, denomRight = 0;
						if (!defaultVoting) {
							for (int movie: trainData.get(userId).keySet()) {
								if (trainData.get(user2Id).containsKey(movie)) {
									double user1Rating = trainData.get(userId).get(movie);
									double user2Rating = trainData.get(user2Id).get(movie);
									double user1Avg = userAverage.get(userId);
									double user2Avg = userAverage.get(user2Id);
									nom += ((user1Rating - user1Avg) * (user2Rating - user2Avg));
									denomLeft += Math.pow(user1Rating - user1Avg, 2);
									denomRight += Math.pow(user2Rating - user2Avg, 2);	
								}
							}
						} else {
							HashSet<Integer> unionMovie = new HashSet<Integer>();
							unionMovie.addAll(trainData.get(userId).keySet());
							unionMovie.addAll(trainData.get(user2Id).keySet());
							for (int movie: unionMovie) {
								double user1Rating = 0;
								double user2Rating = userAverage.get(user2Id);
								if (trainData.get(userId).containsKey(movie)) {
									user1Rating = trainData.get(userId).get(movie);
								} else {
									user1Rating = userAverage.get(userId);
								}
								if (trainData.get(user2Id).containsKey(movie)) {
									user2Rating = trainData.get(user2Id).get(movie);
								} else {
									user2Rating = userAverage.get(user2Id);
								}
								double user1Avg = userAverage.get(userId);
								double user2Avg = userAverage.get(user2Id);
								nom += ((user1Rating - user1Avg) * (user1Rating - user1Avg));
								denomLeft += Math.pow(user1Rating - user1Avg, 2);
								denomRight += Math.pow(user2Rating - user2Avg, 2);
							}
						}
						if (denomLeft * denomRight > 0) {
							weight = nom / Math.sqrt((denomLeft * denomRight));
						}
						synchronized(cache) {
							cache[testUser][i] = weight;
						}
					} else {
						weight = cache[testUser][i];
					}
					if (!Double.isNaN(partial) && !Double.isNaN((double) weight)&& !Double.isNaN(sumWeight)) {
						partial += weight * (trainData.get(user2Id).get(movieId) - userAverage.get(user2Id));
						sumWeight += Math.abs(weight);
					}
				}
			}
			return new Pair<Double, Double>(partial, sumWeight);
		} else {
			int mid = (hi + lo) / 2;
			ComputeWeight left = new ComputeWeight(lo, mid, userId, testUser, movieId, trainUser, userAverage, trainData);
			ComputeWeight right = new ComputeWeight(mid, hi, userId, testUser, movieId, trainUser, userAverage, trainData);
			left.fork();
			Pair<Double, Double> rightResult = right.compute();
			Pair<Double, Double> leftResult = left.join();
			return new Pair<Double, Double>(leftResult.a + rightResult.a, leftResult.b + rightResult.b);
		}
	}
}
