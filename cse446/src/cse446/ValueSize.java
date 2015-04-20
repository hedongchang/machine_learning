package cse446;

import java.util.*;

public class ValueSize {
	int size;
	HashMap<Integer, List<Features>> values;
	
	public ValueSize(int size, HashMap<Integer, List<Features>> values) {
		this.size = size;
		this.values = values;
	}
}
