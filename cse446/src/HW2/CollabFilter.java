package HW2;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class CollabFilter {
	
    public static ForkJoinPool fjPool = new ForkJoinPool();

	HashMap<Integer, HashMap<Integer, Float>> data;
	int[] userList;
	float[][] weightCache;
			
	public CollabFilter(HashMap<Integer, HashMap<Integer, Float>> data, int[] userList) {
		this.data = data;
		this.userList = userList;
		//this.weightCache = new float[data.size()][data.size()];
	}
	
	
	public synchronized float predictRating(int movie, int user) {
		float result = 0;
		if (data.containsKey(user)) {
			float partialResult = 0;
			float sumWeight = 0;
			float userAvg = computeAverage(data.get(user));
			for (int i = 0; i < userList.length; i++) {
				int collabUser = userList[i];
				float rating = findRating(collabUser, movie);
				if (collabUser != user && rating != -1) {
					float collabUserAvg = computeAverage(data.get(collabUser));
					float weight = computeWeight(user, collabUser, userAvg, collabUserAvg);
					partialResult += weight * (rating - collabUserAvg);
					sumWeight = sumWeight + weight;
				}
			}
			/*float userAvg = computeAverage(data.get(user));
			PredictTask predictTask = new PredictTask(0, userList.length, data, userList, user, movie, userAvg);
			Pair<Float, Float> pair = fjPool.invoke(predictTask);*/
			//if (pair.a != 0 && !pair.a.isNaN() && !pair.b.isNaN()) {
				result = userAvg + (1 / sumWeight) * partialResult;
			//}
			//System.out.println(result);
		}
		return result;
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
		HashMap<Integer, Float> movies = this.data.get(user);
		if (!movies.containsKey(movie)) {
			return -1;
		}
		return movies.get(movie);
	}
	
	private synchronized float computeWeight(int user1, int user2, float user1Avg, float user2Avg) {
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
		if (denomLeft != 0 && denomRight != 0 && !Float.isNaN(result)) {
			result = nom / (float) Math.sqrt(denomLeft * denomRight);
		}
		//weightCache[user1][user2] = result;
		return result;
	}
	
	
}
