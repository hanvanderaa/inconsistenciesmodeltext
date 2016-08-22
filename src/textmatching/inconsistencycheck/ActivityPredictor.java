package textmatching.inconsistencycheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum ActivityPredictor {
	VAL,  DISTANCEFROMAVGSENTENCES, DISTANCEFROMAVGALIGNMENTS;
	
	
	public static Collection<ActivityPredictor> missingActivities() {
		List<ActivityPredictor> result = new ArrayList<ActivityPredictor>();
		result.add(VAL);
		result.add(DISTANCEFROMAVGSENTENCES);
		result.add(DISTANCEFROMAVGALIGNMENTS);
		return result;
	}
	
	
	public String toString() {
		switch (this) {
		case VAL: return "val - aligned score";
		case DISTANCEFROMAVGSENTENCES: return "dist-avg-S";
		case DISTANCEFROMAVGALIGNMENTS: return "dist-avg-A";
		default: return "";
		}
	}
}
