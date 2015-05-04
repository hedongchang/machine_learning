package HW2;

import java.util.HashMap;
import java.util.concurrent.*;

public class ComputeTask extends RecursiveTask<Pair<Float, Float>> {
	private static final long serialVersionUID = 1L;
	public int count;
	
	public int hi;
	public int lo;
	public HashMap<Integer, HashMap<Integer, Float>> data;
	int[] userList;
	CollabFilter filter;
	
	public ComputeTask(int lo, int hi, HashMap<Integer, HashMap<Integer, Float>> data, 
			int[] userList, CollabFilter filter) {
		this.lo = lo;
		this.hi = hi;
		this.data = data;
		this.userList = userList;
		this.filter = filter;
	}
	
	@Override
	protected Pair<Float, Float> compute() {
		if (hi - lo <= 30000) {
			float sumAbs = 0;
			float sumSqr = 0;
			for (int i = lo; i < hi; i++)  {
				int userId = userList[i];
				HashMap<Integer, Float> movieRating = data.get(userId);
				for (int movieId : movieRating.keySet()) {
					count++;
					if (count % 1000 == 0) {
						System.out.println(count);
					}
					float predict = filter.predictRating(movieId, userId);
					//count++;
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
			ComputeTask left = new ComputeTask(lo, (hi + lo) / 2, data, userList, filter);
			ComputeTask right = new ComputeTask((hi + lo) / 2, hi, data, userList, filter);
			left.fork();
			Pair<Float, Float> rightResult = right.compute();
			Pair<Float, Float> leftResult = left.join();
			float newAbs = leftResult.a + rightResult.a;
			float newSqr = leftResult.b + rightResult.b;
			return new Pair<Float, Float>(newAbs, newSqr);
		}
	}

}
