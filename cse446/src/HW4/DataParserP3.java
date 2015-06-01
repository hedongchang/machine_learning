package HW4;

import java.io.*;

import HW1.*;
import java.util.*;

/**
 * Parse the given data files
 * @author Dongchang
 *
 */
public class DataParserP3 {	
	/**
	 * @param trainData the training data
	 * @param testData the test data
	 * @return a list that stores all the values
	 */
	public static LinkedList<Pair> ParseData(HashMap<Integer, List<Features>> trainData,
			HashMap<Integer, List<Features>> testData) {
		File file1 = new File("SPECTtr.txt");
		File file2 = new File("SPECTt.txt");
		LinkedList<Pair> retVal = new LinkedList<Pair>();
		try {
			Scanner input1 = new Scanner(file1);
			Scanner input2 = new Scanner(file2);
			while (input1.hasNextLine()) {
				String line = input1.nextLine();
				String[] input = line.split(",");
				int label = Integer.parseInt(input[0]);
				ArrayList<Integer> feature = new ArrayList<Integer>();
				for (int i = 1; i < input.length; i++) {
					feature.add(Integer.parseInt(input[i]));
				}
				// parse the training data
				if (!trainData.containsKey(label)) {
					trainData.put(label, new ArrayList<Features>());
				}
				retVal.add(new Pair(label, feature));
				trainData.get(label).add(new Features(feature));
			}			
			while (input2.hasNextLine()) {
				String line = input2.nextLine();
				String[] input = line.split(",");
				int label = Integer.parseInt(input[0]);
				//System.out.println(label);
				ArrayList<Integer> feature = new ArrayList<Integer>();
				for (int i = 1; i < input.length; i++) {
					feature.add(Integer.parseInt(input[i]));
				}
				// parse the training data
				if (!testData.containsKey(label)) {
					testData.put(label, new ArrayList<Features>());
				}
				retVal.add(new Pair(label, feature));
				testData.get(label).add(new Features(feature));
			}
			input1.close();
			input2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
