package textmatching.alignmentverification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;

public class VerificationSummary {

	private List<MatchingConfig> configs;
	private List<ModelTextPair> mtps;
	
	
	public VerificationSummary(List<MatchingConfig> configs,
			Collection<ModelTextPair> mtps2) {
		this.configs = configs;
		this.mtps = new ArrayList<ModelTextPair>(mtps2);
	}
	
	public int getMtpCount() {
		return mtps.size();
	}
	
	
	public int getConfigCount() {
		return configs.size();
	}
	
	public int totalActivities() {
		int total = 0;
		for (ModelTextPair mtp : mtps) {
			total += mtp.getActivityCount();
		}
		return total;
	}
	
	public int totalSentences() {
		int total = 0;
		for (ModelTextPair mtp : mtps) {
			total += mtp.getSentenceCount();
		}
		return total;
	}
	
	
	public List<MatchingConfig> getConfigs() {
		return configs;
	}
	
	public int correctAligned(MatchingConfig config) {
		int correctAligned = 0;
		for (ModelTextPair mtp : mtps) {
			correctAligned += mtp.getVerification(config).correctAligned();
		}
		return correctAligned;
	}
	
	public int totalAligned(MatchingConfig config) {
		int totalAligned = 0;
		for (ModelTextPair mtp : mtps) {
			totalAligned += mtp.getVerification(config).totalAligned();
		}
		return totalAligned;
	}
	
	public int shouldbeAligned(MatchingConfig config) {
		int shouldbeAligned = 0;
		for (ModelTextPair mtp : mtps) {
			shouldbeAligned += mtp.getVerification(config).shouldbeAligned();
		}
		return shouldbeAligned;
	}
	
	public double getPrecision(MatchingConfig config) {
		return 1.0 * correctAligned(config) / totalAligned(config); 
	}
	
	public double getRecall(MatchingConfig config) {
		return 1.0 * correctAligned(config) / shouldbeAligned(config);
	}
	
	public double getFMeasure(MatchingConfig config) {
		double precision = getPrecision(config);
		double recall = getRecall(config);
		if (precision > 0.0 && recall > 0.0) {
			return 2 * (precision * recall) / (precision + recall);
		}
		return 0.0;
	}

	public List<ModelTextPair> getMtps() {
		return mtps;
	}

	
	
	
}
