package textmatching.inconsistencycheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum ProcessPredictor {
	MINSIMSCORE,COVARALIGNED, DIFF_S, DIFF_ALIGNED, MAX_CONSTRAINED, TOTAL_CONSTRAINED; 
	

	public static Collection<ProcessPredictor> missingActivities() {
		List<ProcessPredictor> result = new ArrayList<ProcessPredictor>();
		result.add(MINSIMSCORE);
//		result.add(COVARALIGNED);
		result.add(DIFF_ALIGNED);
		result.add(DIFF_S);
		
		return result;
	}
	
	public static Collection<ProcessPredictor> wrongOrder() {
		List<ProcessPredictor> result = new ArrayList<ProcessPredictor>();
		result.add(MAX_CONSTRAINED);
		result.add(TOTAL_CONSTRAINED);
		return result;
	}
	
	
	public String toString() {
		switch (this) {
		case MINSIMSCORE: return "min-sim-score";
		case COVARALIGNED: return "covar-aligned";
		case DIFF_S: return "diff-avg-s";
		case DIFF_ALIGNED: return "diff-aligned";
		case MAX_CONSTRAINED: return "max-constrained";
		case TOTAL_CONSTRAINED: return "total-constrained";
		
		default: return "";
		}
	}
	
}
