package HW1;

import java.util.*;

/**
 * ValueSize stores values of data and its size
 * @author Dongchang
 *
 */
public class ValueSize {
	int size; // the number of values in values
	HashMap<Integer, List<Features>> values; // the values of data
	
	public ValueSize(int size, HashMap<Integer, List<Features>> values) {
		this.size = size;
		this.values = values;
	}
}
