package cse446;

import java.util.ArrayList;

public class Features {
	public ArrayList<Integer> features;
	
	public Features(ArrayList<Integer> features) {
		this.features = features;
	}
	
	public int getFeature(int attribute) {
		return features.get(attribute);
	}
	
	public String toString() {
		String s = "" + features.get(0);
		for (int i = 1; i < features.size(); i++) {
			s += " " + features.get(i);
		}
		return s;
	}
}
