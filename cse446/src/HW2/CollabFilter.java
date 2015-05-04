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
	
	
	public float predictRating(int movie, int user) {
		float result = 0;
		if (data.containsKey(user)) {
			/*float partialResult = 0.0;
			float sumWeight = 0.0;
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
			}*/
			float userAvg = computeAverage(data.get(user));
			PredictTask predictTask = new PredictTask(0, userList.length, data, userList, user, movie, userAvg);
			Pair<Float, Float> pair = fjPool.invoke(predictTask);
			if (pair.a != 0 && !pair.a.isNaN() && !pair.b.isNaN()) {
				result = userAvg + (1 / pair.b) * pair.a;
			}
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
	
	/*private float computeWeight(int user1, int user2, float user1Avg, float user2Avg) {
		float nom = 0.0;
		float denomLeft = 0.0;
		float denomRight = 0.0;
		float result = 0.0;
		
		// find the intersection of two users' movies
		HashMap<Integer, float> user1MovieRating = new HashMap<Integer, float>();
		
		for (MovieInstance movie: data.get(user1)) {
			user1MovieRating.put(movie.movie, movie.rating);
		}
		for (MovieInstance movie: data.get(user2)) {
			int movieId = movie.movie;
			if (user1MovieRating.containsKey(movieId)) {
				float user1Rating = user1MovieRating.get(movieId);
				nom += (user1Rating - user1Avg) * (movie.rating - user2Avg);
				denomLeft += Math.pow(user1Rating - user1Avg, 2);
				denomRight += Math.pow(movie.rating - user2Avg, 2);
			}
		}
		if (denomLeft != 0 && denomRight != 0) {
			result = nom / Math.sqrt(denomLeft * denomRight);
		}
		//weightCache[user1][user2] = result;
		return result;
	}
	
	private float findRating(int user, int movie) {
		List<MovieInstance> movies = data.get(user);
		for (MovieInstance singleMovie: movies) {
			if (singleMovie.movie == movie) {
				return singleMovie.rating;
			}
		}
		return -1;
	}*/
}
