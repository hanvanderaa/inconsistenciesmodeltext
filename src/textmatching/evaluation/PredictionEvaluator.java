package textmatching.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import textmatching.inconsistencycheck.Prediction;
import textmatching.util.PrintUtils;

public class PredictionEvaluator {

	List<Prediction> predicted;
	Map<Double, Double> precRecMap;
	boolean wrongOrder;
	
	public PredictionEvaluator(List<Prediction> predicted) {
		this(predicted, false);
	}
	
	public PredictionEvaluator(List<Prediction> predicted, boolean wrongOrder) {
		this.predicted = predicted;
		this.wrongOrder = wrongOrder;
		setPrecisionRecallMap();
	}

	public double highestPrecision() {
		for (Double d : precRecMap.keySet()) {
			return d;
		}
		return 0.0;
	}
	
	public double highestRecall() {
		double max = 0.0;
		for (Double d :precRecMap.values()) {
			if (d > max) {
				max = d;
			}
		}
		return max;
	}
	
	public int recallOneAt() {
		int missing = totalInconsistencies();
		if (missing == 0) {
			return 0;
		}
		for (int n = missing; n < predicted.size(); n++) {
			if (truePositives(getConsideredPredictions(n)) == missing) {
				return n;
			}
		}
		return predicted.size();
	}
	
	public double highestFMeasure() {
		double max = 0.0;
		for (double prec :precRecMap.keySet()) {
			double f1 = PrintUtils.fMeasure(prec, precRecMap.get(prec)); 
			if (f1 > max) {
				max = f1;
			}
		}
		return max;
	}
	
	
	
	public void setPrecisionRecallMap() {
		precRecMap = new TreeMap<Double, Double>(Collections.reverseOrder());
		int missing = totalInconsistencies();
		for (int i = 1; i <= predicted.size(); i++) {
			List<Prediction> considered = getConsideredPredictions(i);
			if (considered.size() > 0) {
				int tp = truePositives(considered);
				double prec = tp * 1.0 / considered.size();
				double rec = tp * 1.0 / missing;
				precRecMap.put(prec, rec);
			}
		}
		
	}

	public double precisionAtN(int n) {
		List<Prediction> considered = getConsideredPredictions(n);
		int tp = truePositives(considered);
		double prec = tp * 1.0 / considered.size();
		return prec;
	}
	
	public double recallAtN(int n) {
		int missing = totalInconsistencies();
		List<Prediction> considered = getConsideredPredictions(n);
		int tp = truePositives(considered);
		double rec = tp * 1.0 / missing;
		return rec;
	}
	
	public double fMeasureAtN(int n) {
		return PrintUtils.fMeasure(precisionAtN(n), recallAtN(n));
	}
	
	private boolean isInconsistency(Prediction p ) {
		return ((!wrongOrder && p.isMissing()) || (wrongOrder && p.isWrongOrder()));
	}
	
	private int truePositives(List<Prediction> list) {
		int n = 0;
		for (Prediction p : list) {
			if (isInconsistency(p)) {
				n++;
			}
		}
		return n;
	}
	
	private int totalInconsistencies() {
		int n = 0;
		for (Prediction p: getConsideredPredictions(predicted.size())) {
			if (isInconsistency(p)) {
				n++;
			}
		}
		return n;
	}
	
	private List<Prediction> getConsideredPredictions(int n) {
		List<Prediction> result = new ArrayList<Prediction>();
		for (int i = 0; i < n; i++) {
			if (!wrongOrder) {
				result.add(predicted.get(i));
			} else if (!predicted.get(i).isMissing() || predicted.get(i).isWrongOrder()) {
				result.add(predicted.get(i));
			}
//			result.add(predicted.get(i));
		}
//		double lastScore = predicted.get(n - 1).getScore();
//		
//		while (n < predicted.size() && !(predicted.get(n - 1).getScore() < lastScore)) {
//			result.add(predicted.get(n));
//			n++;
//		}
		return result;
	}
	
	
}
