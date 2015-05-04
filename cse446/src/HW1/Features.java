package HW1;

import java.util.List;

/**
 * Features contains different attributes and each attribute
 * has a corresponding value
 * @author Dongchang
 *
 */
public class Features {
	public List<Integer> features;  // the list of attribute values
	
	public Features(List<Integer> features) {
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
