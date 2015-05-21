package HW1;

import java.io.*;
import java.util.*;

/**
 * Parse the given data files
 * @author Dongchang
 *
 */
public class DataParser {
	
	public static final int COUNT = 12000;
	
	/**
	 * @param trainData the training data
	 * @param testData the test data
	 * @return a list that stores all the values
	 */
	public static LinkedList<Pair> ParseData(HashMap<Integer, List<Features>> trainData,
			HashMap<Integer, List<Features>> testData) {
		File file1 = new File("labels.txt");
		File file2 = new File("features.txt");
		LinkedList<Pair> retVal = new LinkedList<Pair>();
		try {
			Scanner input1 = new Scanner(file1);
			Scanner input2 = new Scanner(file2);
			
			LinkedList<Integer> labels = new LinkedList<Integer>();
			while (input1.hasNextLine()) {
				int label = Integer.parseInt(input1.nextLine());
				labels.add(label);
			}
			int count = 0;
			while (input2.hasNextLine()) {
				int label = labels.remove();
				String line = input2.nextLine();
				Scanner lineInput = new Scanner(line);
				ArrayList<Integer> feature = new ArrayList<Integer>();
				while (lineInput.hasNextInt()) {
					feature.add(lineInput.nextInt());
				}
				// parse the training data
				if (count < 12000) {
					if (!trainData.containsKey(label)) {
						trainData.put(label, new ArrayList<Features>());
					}
					retVal.add(new Pair(label, feature));
					trainData.get(label).add(new Features(feature));
					// parse the test data
				} else {
					if (!testData.containsKey(label)) {
						testData.put(label, new ArrayList<Features>());
					}
					testData.get(label).add(new Features(feature));
				}
				lineInput.close();
				count++;
			}
			input1.close();
			input2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
