package cse446;

import java.io.*;
import java.util.*;

public class DataParser {
	public static void ParseData(HashMap<Integer, List<Features>> trainData,
			HashMap<Integer, List<Features>> testData) {
		File file1 = new File("labels.txt");
		File file2 = new File("features.txt");
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
				if (count < 12000) {
					if (!trainData.containsKey(label)) {
						trainData.put(label, new ArrayList<Features>());
					}
					trainData.get(label).add(new Features(feature));
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
	}
}
