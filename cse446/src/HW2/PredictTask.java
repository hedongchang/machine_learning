package HW2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class PredictTask extends RecursiveTask<Pair<Float, Float>>{
	
	Map<Pair<Integer, Integer>, Float> cache = new HashMap<Pair<Integer, Integer>, Float>();
	
	private static final long serialVersionUID = 1L;
	
	public int lo;
	public int hi;
	public HashMap<Integer, HashMap<Integer, Float>> data;
	public int[] userList;
	public int user;
	public int movie;
	float userAvg;
	
	public PredictTask(int lo, int hi, HashMap<Integer, HashMap<Integer, Float>> data,
			int[] userList, int user, int movie, float userAvg) {
		this.lo = lo;
		this.hi = hi;
		this.data = data;
		this.userList = userList;
		this.user = user;
		this.movie = movie;
		this.userAvg = userAvg;
	}
	
	@Override
	protected Pair<Float, Float> compute() {
		if (hi - lo <= 2000)  {
			float partialResult = 0;
			float sumWeight = 0;
			float userAvg = computeAverage(data.get(user));
			for (int i = lo; i < hi; i++) {
				int collabUser = userList[i];
				float rating = findRating(collabUser, movie);
				if (collabUser != user && rating != -1) {
					float collabUserAvg = computeAverage(data.get(collabUser));
					Pair<Integer, Integer> pair = new Pair<Integer, Integer>(user, collabUser);
					float weight = 0;
					if (!cache.containsKey(pair)) {
						weight = computeWeight(user, collabUser, userAvg, collabUserAvg);
						cache.put(pair, weight);
					} else {
						weight = cache.get(pair);
					}
					partialResult += weight * (rating - collabUserAvg);
					sumWeight = sumWeight + weight;
				}
			}
			return new Pair<Float, Float>(partialResult, sumWeight);
		} else {
			PredictTask left = new PredictTask(lo, (hi + lo) / 2, data, userList, user, movie, userAvg);
			PredictTask right = new PredictTask((hi + lo) / 2, hi, data, userList, user, movie, userAvg);
			left.fork();
			Pair<Float, Float> rightResult = right.compute();
			Pair<Float, Float> leftResult = left.join();
			float partialResult = leftResult.a + rightResult.a;
			float sumWeight = leftResult.b + rightResult.b;
			return new Pair<Float, Float>(partialResult, sumWeight);
		}
	}
	
	private float computeAverage(HashMap<Integer, Float> movies) {
		float ratingSum = 0;
		for (int movie: movies.keySet()) {
			ratingSum += movies.get(movie);
		}
		if (movies.size() != 0) {
			return (float) ratingSum / movies.size();
		} else {
			return 0;
		}
	}
	
	private float findRating(int user, int movie) {
		HashMap<Integer, Float> movies = data.get(user);
		if (!movies.containsKey(movie)) {
			return -1;
		}
		return movies.get(movie);
	}
	
	private float computeWeight(int user1, int user2, float user1Avg, float user2Avg) {
		float nom = 0;
		float denomLeft = 0;
		float denomRight = 0;
		float result = 0;
		
		// find the intersection of two users' movies
		HashMap<Integer, Float> user1MovieRating = data.get(user1);
		HashMap<Integer, Float> user2MovieRating = data.get(user2);
		
		for (int movieId: user2MovieRating.keySet()) {
			if (user1MovieRating.containsKey(movieId)) {
				float user2Rating = user2MovieRating.get(movieId);
				float user1Rating = user1MovieRating.get(movieId);
				nom += (user1Rating - user1Avg) * (user2Rating - user2Avg);
				denomLeft += Math.pow(user1Rating - user1Avg, 2);
				denomRight += Math.pow(user2Rating - user2Avg, 2);
			}
		}
		if (denomLeft != 0 && denomRight != 0) {
			result = nom / (float) Math.sqrt(denomLeft * denomRight);
		}
		//weightCache[user1][user2] = result;
		return result;
	}

}
