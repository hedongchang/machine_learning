package HW2;

import java.util.*;
import java.util.concurrent.RecursiveAction;

/**
 * The class helps to compute the cache which stores weights between all users
 * @author Dongchang
 */
public class ComputeCache extends RecursiveAction {
	
	private static final long serialVersionUID = 1L;
	
	public static final boolean DEFAULT_VOTING = false; // whether to default vote
	public static int THRESHOLD = 2000; // the sequential cutoff
	
	public int lo; // the lower bound
	public int hi; // the higher bound
	public float[][] cache; // cache to store weights between users
	public HashMap<Integer, Double> userAverage;
	// a map from user id to the user's average movie ratings
	public HashMap<Integer, HashMap<Integer, Integer>> trainData;
	// a map from user id to a map from movie id to rating
	public int[] trainUser;
	// an array of training users
	
	/**
	 * Constructs a ComputeCache
	 * @param lo the lower bound
	 * @param hi the higher bound
	 * @param cache the two-dimensional array to store weights between different users
	 * @param userAverage a map from user id to its average ratings of all movies
	 * @param trainData a map from user id to a map from movie id to rating
	 * @param trainUser a map from movie id to a map from user id to rating
	 */
	public ComputeCache(int lo, int hi, float[][] cache, HashMap<Integer, Double> userAverage,
			HashMap<Integer, HashMap<Integer, Integer>> trainData, int[] trainUser) {
		this.lo = lo;
		this.hi = hi;
		this.cache = cache;
		this.userAverage = userAverage;
		this.trainData = trainData;
		this.trainUser = trainUser;
	}

	/**
	 * Compute weights between users in a certain range
	 */
	@Override
	protected void compute() {
		if (hi - lo <= THRESHOLD) {
			// if the range is less than threshold, compute in sequence
			for (int i = lo; i < hi; i++) {
				int user1Id = trainUser[i];
				double user1Avg = userAverage.get(user1Id);
				for (int j = 0; j < trainUser.length; j++) {
					double weight = 0;
					int user2Id = trainUser[j];
					double user2Avg = userAverage.get(user2Id);
					double nom = 0.0;
					double denomLeft = 0.0;
					double denomRight = 0.0;
					if (!DEFAULT_VOTING) {
						// if it is not default voting, calculate weights according to intersection of
						// two users' movies
						for (int movie: trainData.get(user1Id).keySet()) {
							if (trainData.get(user2Id).containsKey(movie)) {
								float user1Rating = trainData.get(user1Id).get(movie);
								float user2Rating = trainData.get(user2Id).get(movie);
								nom = nom + (((double) user1Rating - user1Avg) * ((double) user2Rating - user2Avg));
								denomLeft = denomLeft + Math.pow((double) user1Rating - user1Avg, 2);
								denomRight = denomRight + Math.pow((double) user2Rating - user2Avg, 2);
							}
						}
					} else {
						// if it is not default voting, calculate weights according to union of
						// two users' movies
						Set<Integer> unionSet = new HashSet<Integer>();
						unionSet.addAll(trainData.get(user1Id).keySet());
						unionSet.addAll(trainData.get(user2Id).keySet());
						for (int movie: unionSet) {
							double user1Rating = userAverage.get(user1Id);
							double user2Rating = userAverage.get(user2Id);
							if (trainData.get(user1Id).containsKey(movie)) {
								user1Rating = trainData.get(user1Id).get(movie);
							}
							if (trainData.get(user2Id).containsKey(movie)) {
								user2Rating = trainData.get(user2Id).get(movie);
							}
							nom = nom + (((double) user1Rating - user1Avg) * ((double) user2Rating - user2Avg));
							denomLeft = denomLeft + Math.pow((double) user1Rating - user1Avg, 2);
							denomRight = denomRight + Math.pow((double) user2Rating - user2Avg, 2);
						}
					}
					if (!Double.isNaN(nom) && (denomLeft * denomRight) > 0) {
						weight = (double) nom / Math.sqrt(denomLeft * denomRight);
					}
					// populate the weight in cache
					cache[i][j] = (float) weight;
				}
			}
		} else {
			// compute in parallel
			ComputeCache left = new ComputeCache(lo, (hi + lo) / 2, cache, userAverage, trainData, trainUser);
			ComputeCache right = new ComputeCache((hi + lo) / 2, hi, cache, userAverage, trainData, trainUser);
			left.fork();
			right.compute();
			left.join();
		}
	}
}
