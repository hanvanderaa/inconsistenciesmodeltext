package textmatching.alignmentverification;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import textmatching.matching.config.MatchingConfig;
import textmatching.processmodel.Activity;


public class VerifiedAlignment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8397636607731562501L;
	Map<Activity, ActivityVerification> verificationMap;
	String name;
	MatchingConfig config;
	
	
	public VerifiedAlignment(String name, MatchingConfig config) {
		this.name = name;
		this.config = config;
		verificationMap = new HashMap<Activity, ActivityVerification>();
	}
	
	public void addVerification(ActivityVerification verification) {
		verificationMap.put(verification.getActivity(), verification);
	}
	
	public Set<Activity> getActivities() {
		return verificationMap.keySet();
	}
	
	public ActivityVerification getVerification(Activity activity) {
		return verificationMap.get(activity);
	}
	
	public int getActivityCount() {
		return verificationMap.size();
	}
	
	public int countStatus(VerificationStatus status) {
		int n = 0;
		for (ActivityVerification verif : verificationMap.values()) {
			if (verif.getStatus().equals(status)) {
				n++;
			}
		}
		return n;
	}
	
	public int getAlignedActivitiesCount() {
		return countStatus(VerificationStatus.CORRECTMATCH) + 
				countStatus(VerificationStatus.FALSEMATCH) +
				countStatus(VerificationStatus.WRONGSENTENCE);
	}
	
	
	public int correctAligned() {
		return countStatus(VerificationStatus.CORRECTMATCH);
	}
	
	public int totalAligned() {
		return getAlignedActivitiesCount();
	}
	
	public int shouldbeAligned() {
		return correctAligned() + 
				countStatus(VerificationStatus.WRONGSENTENCE) + 
				countStatus(VerificationStatus.MISSEDMATCH);
	}
	
	public double getPrecision() {
		if (totalAligned() > 0) {
			return 1.0 * correctAligned() / totalAligned();
		}
		return 0.0;
	}
	
	public double getRecall() {
		if (shouldbeAligned() > 0) {
			return 1.0 * correctAligned()/ shouldbeAligned();
		}
		return 0.0;
	}

	public double getFmeasure() {
		double precision = getPrecision();
		double recall = getRecall();
		if (precision > 0.0 && recall > 0.0) {
			return 2 * (precision * recall) / (precision + recall);
		}
		return 0.0;
		
	}
	
	public String getName() {
		return name;
	}
	
	public MatchingConfig getConfig() {
		return config;
	}
	
}
