package HW2;

import java.io.*;
import java.util.*;

public class DataParser {
	public static int parseData(HashMap<Integer, HashMap<Integer, Float>> trainData, 
			HashMap<Integer, HashMap<Integer, Float>> testData, int[] userList, int[] userTrainList) {
		File file1 = new File("TrainingRatings.txt");
		File file2 = new File("TestingRatings.txt");
		int count = 0;
		try {
			Scanner input1 = new Scanner(file1);
			Scanner input2 = new Scanner(file2);
			parseOneFile(input1, trainData);
			count = parseOneFile(input2, testData);
			int index = 0;
			for (int user: testData.keySet()) {
				userList[index] = user;
				index++;
			}
			int index1 = 0;
			System.out.println(trainData.keySet().size());
			for (int user: trainData.keySet())  {
				userTrainList[index1] = user;
				index1++;
			}
			input1.close();
			input2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	private static int parseOneFile(Scanner input, HashMap<Integer, HashMap<Integer, Float>> userMovieRating) {
		int count = 0;
		while (input.hasNextLine()) {
			count++;
			String line = input.nextLine();
			String[] lineInput = line.split(",");
			int movie = Integer.parseInt(lineInput[0]);
			int user = Integer.parseInt(lineInput[1]);
			float rating = Float.parseFloat(lineInput[2]);
			if (!userMovieRating.containsKey(user)) {
				userMovieRating.put(user, new HashMap<Integer, Float>());
			}
			userMovieRating.get(user).put(movie, rating);
		}
		return count;
	}
}
