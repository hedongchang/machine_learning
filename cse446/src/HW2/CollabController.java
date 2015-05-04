package HW2;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class CollabController {
	
    public static ForkJoinPool fjPool = new ForkJoinPool();
    public static final int USER_NUM = 27555;

	
	public static void main(String[] args) {
		System.out.println("parse data...");
		HashMap<Integer, HashMap<Integer, Float>> trainData = new HashMap<Integer, HashMap<Integer, Float>>();
		HashMap<Integer, HashMap<Integer, Float>> testData = new HashMap<Integer, HashMap<Integer, Float>>();
		int[] userList = new int[USER_NUM];
		int count = DataParser.parseData(trainData, testData, userList);
		System.out.println("parse complete " + count);
		CollabFilter filter = new CollabFilter(trainData, userList);
		//double sumAbs = 0.0;
		//double sumSqr = 0.0;
		ComputeTask computeTask = new ComputeTask(0, USER_NUM, testData, userList, filter);
		Pair<Float, Float> pair = fjPool.invoke(computeTask);
		//count = computeTask.count;
		/*for (int userId: testData.keySet()) {
			count += testData.get(userId).size();
			for (MovieInstance movie : testData.get(userId)) {
				double predict = filter.predictRating(movie.movie, userId);
				if (count % 100 == 0) {
					System.out.println(count);
				}
				sumAbs += Math.abs(movie.rating - predict);
				sumSqr += Math.pow(movie.rating - predict, 2);
			}
		}*/
		System.out.println("the absolute error " + pair.a / count);
		System.out.println("the square mean error " + Math.sqrt(pair.b / count));
	}
}
