package textmatching.evaluation;

import java.util.HashMap;
import java.util.Map;

public class PrecisionRecallStats {

	Map<Double, Double> recallToPrecision;
	
	public PrecisionRecallStats() {
		recallToPrecision = new HashMap<Double, Double>();
	}
	
	public void add(double precision, double recall) {
		if (!recallToPrecision.containsKey(recall) || recallToPrecision.get(recall) < precision) {
			recallToPrecision.put(recall, precision);
		}
	}
	
	public double getPrecisionAt(double recallTarget, double jump) {
		double bestPrecision = 0.0;
		for (double recall : recallToPrecision.keySet()) {
			if ((recall) <= recallTarget + 0.5 * jump && (recall >=  recallTarget - 0.5 * jump) 
					&& recallToPrecision.get(recall) > bestPrecision) {
				bestPrecision = recallToPrecision.get(recall);
			}
		}
		return bestPrecision;
	}
	
}
