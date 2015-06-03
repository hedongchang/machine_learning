package HW4;

public class DataUtility {
	public static final int MaxTestExs = 10000;
	public static final int MaxTrSets = 100;
	public static final int MaxClasses = 100;

	/**
	 * 
	 * @param classes
	 * @param preds z
	 * @param ntestexs the number of test examples
	 * @param ntrsets the number of training sets
	 * @return loss, bias, var, varp, varn, varc z
	 */
	public static double[] biasvar(int[] classes, int[][] preds, int ntestexs, int ntrsets) {
		double loss = 0.0, bias = 0.0, var = 0.0, varp = 0.0, varn = 0.0, varc = 0.0;
		int e;
		double[] result = new double[4];
		double lossx = 0.0, biasx = 0.0, varx = 0.0;
		for (e = 0; e < ntestexs; e++) {
			biasvarx(classes[e], preds[e], ntrsets, result);
			lossx = result[0];
			biasx = result[1];
			varx = result[2];
			loss += lossx;
			bias += biasx;
			if (biasx != 0.0) {
				varn += varx;
				varc += 1.0;
				varc -= lossx;
			} else {
				varp += varx;
			}
	  }
	  loss /= ntestexs;
	  bias /= ntestexs;
	  var = loss - bias;
	  varp /= ntestexs;
	  varn /= ntestexs;
	  varc /= ntestexs;
	  return new double[] {loss, bias, var, varp, varn, varc};
	}
	
	/**
	 * 
	 * @param classx
	 * @param predsx
	 * @param ntrsets
	 * @return lossx, biasx, varx
	 */
	private static void biasvarx(int classx, int[] predsx, int ntrsets, double[] result) {
		int c, t;
		int[] nclass = new int[MaxClasses];
		int majclass = -1, nmax = 0;
		for (c = 0; c < MaxClasses; c++)
			nclass[c] = 0;
			for (t = 0; t < ntrsets; t++)
				nclass[predsx[t]]++;
			for (c = 0; c < MaxClasses; c++)
				if (nclass[c] > nmax) {
					majclass = c;
					nmax = nclass[c];
				}
			result[0] = 1.0 - (float)nclass[classx] / ntrsets;
			if (majclass != classx) {
				result[1] = 1;
			} else {
				result[1] = 0;
			}
			result[2] = 1.0 - (float)nclass[majclass] / ntrsets;
		}
	}
