package HW2;

import java.util.HashMap;
import java.util.concurrent.*;

public class AllPredicts extends RecursiveTask<Pair<Float, Float>> {
	private static final long serialVersionUID = 1L;
	public int count;
	
	public int hi;
	public int lo;
	public HashMap<Integer, HashMap<Integer, Float>> data;
	int[] userTestList;
	CollabFilter filter;
	
	public AllPredicts(int lo, int hi, HashMap<Integer, HashMap<Integer, Float>> data, 
			int[] userTestList, CollabFilter filter) {
		this.lo = lo;
		this.hi = hi;
		this.data = data;
		this.userTestList = userTestList;
		this.filter = filter;
	}
	
	@Override
	protected synchronized Pair<Float, Float> compute() {
		if (hi - lo <= /*userList.length*/1000 / 8) {
			float sumAbs = 0;
			float sumSqr = 0;
			for (int i = lo; i < hi; i++)  {
				int userId = userTestList[i];
				HashMap<Integer, Float> movieRating = data.get(userId);
				for (int movieId : movieRating.keySet()) {
					float predict = filter.predictRating(movieId, userId);
					if (count % 100 == 0) {
						System.out.println(count + " " + predict);
					}
					count++;
					float rating = movieRating.get(movieId);
					if (!Float.isNaN(predict) && !Float.isNaN(rating)) {
						sumAbs += Math.abs(rating - predict);
						//System.out.println(rating - predict);
						sumSqr += Math.pow(rating - predict, 2);
					}
				}
			}
			return new Pair<Float, Float>(sumAbs, sumSqr);
		} else {
			AllPredicts left = new AllPredicts(lo, (hi + lo) / 2, data, userTestList, filter);
			AllPredicts right = new AllPredicts((hi + lo) / 2, hi, data, userTestList, filter);
			left.fork();
			Pair<Float, Float> rightResult = right.compute();
			Pair<Float, Float> leftResult = left.join();
			float newAbs = leftResult.a + rightResult.a;
			float newSqr = leftResult.b + rightResult.b;
			return new Pair<Float, Float>(newAbs, newSqr);
		}
	}

}
