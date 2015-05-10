package hw2_duplicate;

import java.util.HashMap;
import java.util.concurrent.RecursiveAction;

public class ComputeCache extends RecursiveAction {
	
	private static final long serialVersionUID = 1L;
	
	public int lo;
	public int hi;
	public float[][] cache;
	public HashMap<Integer, Double> userAverage;
	public HashMap<Integer, HashMap<Integer, Integer>> trainData;
	public int[] trainUser;
	
	
	public ComputeCache(int lo, int hi, float[][] cache, HashMap<Integer, Double> userAverage,
			HashMap<Integer, HashMap<Integer, Integer>> trainData, int[] trainUser) {
		this.lo = lo;
		this.hi = hi;
		this.cache = cache;
		this.userAverage = userAverage;
		this.trainData = trainData;
		this.trainUser = trainUser;
	}


	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		if (hi - lo <= 2000) {
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
					for (int movie: trainData.get(user1Id).keySet()) {
						if (trainData.get(user2Id).containsKey(movie)) {
							float user1Rating = trainData.get(user1Id).get(movie);
							float user2Rating = trainData.get(user2Id).get(movie);
							nom = nom + (((double) user1Rating - user1Avg) * ((double) user2Rating - user2Avg));
							denomLeft = denomLeft + Math.pow((double) user1Rating - user1Avg, 2);
							denomRight = denomRight + Math.pow((double) user2Rating - user2Avg, 2);
						}
					}
					if (!Double.isNaN(nom) && (denomLeft * denomRight) > 0) {
						weight = (double) nom / Math.sqrt(denomLeft * denomRight);
					}
					cache[i][j] = (float) weight;
				}
			}
		} else {
			ComputeCache left = new ComputeCache(lo, (hi + lo) / 2, cache, userAverage, trainData, trainUser);
			ComputeCache right = new ComputeCache((hi + lo) / 2, hi, cache, userAverage, trainData, trainUser);
			left.fork();
			right.compute();
			left.join();
		}
		
	}
}
