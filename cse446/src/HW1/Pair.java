package HW1;

import java.util.List;

/**
 * A pair of label and feature of a value
 * @author Dongchang
 *
 */
public class Pair {
	public int label; // the label of a value
	public List<Integer> feature;  // the feature of a vlue
	
	public Pair(int label, List<Integer> feature) {
		this.label = label;
		this.feature = feature;
	}
}
