package hw2_version1;

import java.util.*;
import java.io.*;

public class CollabMain {
	
	public static final int TRAIN_USER_NUM = 28978;
	public static final int TEST_NUM = 1000;
	
	public static void main(String[] args) {
		System.out.println("parse data...");
		HashMap<Integer, HashMap<Integer, Integer>> trainData = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, HashMap<Integer, Integer>> movieUser = new HashMap<Integer, HashMap<Integer, Integer>>();
		DataParser.parseData(trainData, movieUser);
		System.out.println("parse complete");
		int[] trainUser = new int[TRAIN_USER_NUM];
		
		int index = 0;
		for (int userId: trainData.keySet()) {
			trainUser[index] = userId;
			index++;
		}
		CollabFilter filter = new CollabFilter(trainData, movieUser, trainUser);
		try {
			Scanner input = new Scanner(new File("TestingRatings.txt"));
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			writer.println("haha");
			Double sumAbs = 0.0;
			Double sumSqr = 0.0;
			int count = 0;
			HashSet<Integer> testUserSet = new HashSet<Integer>();
			int testUser = 0;
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] features = line.split(",");
				int movieId = Integer.parseInt(features[0]);
				int userId = Integer.parseInt(features[1]);
				int rating = (int) Double.parseDouble(features[2]);
				if (!testUserSet.contains(userId)) {
					testUserSet.add(userId);
					testUser++;
				}
				double predict = filter.predict(userId, movieId, testUser);
				sumAbs += Math.abs(predict - rating);
				sumSqr += Math.pow(predict - rating, 2);
				count++;
			}
			writer.println("absolute " + sumAbs / count);
			writer.println("square " + Math.sqrt(sumSqr / count));
			System.out.println("absolute " + sumAbs / count);
			System.out.println("square " + Math.sqrt(sumSqr / count));
			writer.close();
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}