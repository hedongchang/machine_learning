package HW3;

import java.util.Comparator;


public class PairComparator implements Comparator<Pair<String, Double>>{
	@Override
	public int compare(Pair<String, Double> pair1, Pair<String, Double> pair2) {
		if (pair1.b < pair2.b) {
			return -1;
		} else if (pair1.b > pair2.b) {
			return 1;
		} else {
			return 0;
		}
	}
}
