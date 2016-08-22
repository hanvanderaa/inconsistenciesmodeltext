package textmatching.inconsistencycheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import textmatching.alignment.Alignment;
import textmatching.evaluation.PredictionEvaluator;
import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.processmodel.Activity;
import textmatching.util.PrintUtils;
import textmatching.util.SortUtils;

public class InconsistencyChecker {

	Collection<ModelTextPair> mtps;
	MatchingConfig config;
	
	public InconsistencyChecker(Collection<ModelTextPair> mtps, MatchingConfig config) {
		this.mtps = mtps;
		this.config = config;
	}
	
	public void evaluatePredictorPerformance() {
		System.out.println("\nPerformance for config: " + config);
		System.out.println("process predictors:");
		for (ProcessPredictor p : ProcessPredictor.missingActivities()) {
			List<Prediction> prioritized = prioritizeProcesses(p);
			PredictionEvaluator eval = new PredictionEvaluator(prioritized);
			System.out.println(p.toString() + " best prec.: " + PrintUtils.dts(eval.highestPrecision()) +
					" full rec.: " + eval.recallOneAt() + 
					" best f1: " + PrintUtils.dts(eval.highestFMeasure()));
		}
		
		if (config.matchingStrategy == MatchingConfig.BFSEARCHER) {
			System.out.println("\nprocess predictors (wrong order)");
			for (ProcessPredictor p : ProcessPredictor.wrongOrder()) {
				List<Prediction> prioritized = prioritizeProcesses(p);
				boolean wrongOrder = true;
				PredictionEvaluator eval = new PredictionEvaluator(prioritized, wrongOrder);
				System.out.println(p.toString() + 
						" best prec.: " + PrintUtils.dts(eval.highestPrecision()) +
						" full rec.: " + eval.recallOneAt() + 
						" best f1: " + PrintUtils.dts(eval.highestFMeasure()));
			}
		}
		
		System.out.println("\nmatch predictors");
		for (ActivityPredictor p : ActivityPredictor.missingActivities()) {
			List<Prediction> prioritized = prioritizeMatches(p);
			PredictionEvaluator eval = new PredictionEvaluator(prioritized);
			System.out.println(p.toString() + " best prec.: " + PrintUtils.dts(eval.highestPrecision()) +
					" full rec.: " + eval.recallOneAt() + 
					" best f1: " + PrintUtils.dts(eval.highestFMeasure()));
		}
	}
	
	public List<Prediction> prioritizeProcesses(ProcessPredictor mode) {
		List<Prediction> predicted = new ArrayList<Prediction>();
		for (ModelTextPair mtp : mtps) {
			double score = InconsistencyPredictor.inconsistencyScore(mode, mtp.getAlignment(config));
			boolean missing = mtp.hasMissingActivities();
			boolean wrongOrder = mtp.hasWrongOrder();
			predicted.add(new Prediction(mtp, score, missing, wrongOrder));
		}
		SortUtils.sortPredictions(predicted);
		return predicted;
	}
	

	public List<Prediction> prioritizeMatches(ActivityPredictor mode) {
		List<Prediction> predicted = new ArrayList<Prediction>();
		for (ModelTextPair mtp : mtps) {
			Alignment alignment = mtp.getAlignment(config);
			for (Activity activity : alignment.getActivities()) {
				double score = InconsistencyPredictor.inconsistencyScore(mode, alignment, activity);
				boolean missing = mtp.isMissing(activity);
				Prediction prediction = new Prediction(activity, score, missing);
				prediction.setParent(mtp);
				predicted.add(prediction);
			}
		}
		SortUtils.sortPredictions(predicted);
		return predicted;
	}
}
