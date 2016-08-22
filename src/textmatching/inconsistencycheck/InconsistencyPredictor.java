package textmatching.inconsistencycheck;

import textmatching.alignment.Alignment;
import textmatching.alignment.Match;
import textmatching.processmodel.Activity;
import textmatching.textualdescription.Sentence;

public class InconsistencyPredictor {

	
	public static double inconsistencyScore(ProcessPredictor mode, Alignment alignment) {
		switch(mode) {
		case MINSIMSCORE : return 1.0 - lowestValue(alignment);
		case COVARALIGNED: return coVar(alignment);
		case DIFF_ALIGNED: return 1.0 - minDistanceFromAverageAlignments(alignment);
		case DIFF_S: return 1.0 - distfromavgSentence(alignment);
		case MAX_CONSTRAINED: return maxDistanceFromBest(alignment);
		case TOTAL_CONSTRAINED: return totalDistanceFromBest(alignment);
		default: return 1.0;
		}
	}
	
	public static double inconsistencyScore(ActivityPredictor mode, Alignment alignment, Activity activity) {
		switch(mode) {
		case VAL: return 1.0 - value(alignment, activity);
		case DISTANCEFROMAVGSENTENCES : return 1.0 - distanceFromAverageSentences(alignment, activity);
		case DISTANCEFROMAVGALIGNMENTS : return 1.0 - distanceFromAverageAlignments(alignment, activity);
		default: return 1.0;
		}
	}



	private static double lowestValue(Alignment alignment) {
		if (alignment.hasUnaligned()) {
			return 0.0;
		}
		double min = Double.MAX_VALUE;
		for (Match match : alignment.getMatches()) {
			if (match.getScore() < min) {
				min = match.getScore();
			}
		}
		return min;
	}
	
	private static double value(Alignment alignment, Activity activity) {
		return alignment.getMatchingScore(activity);
	}
	
	
	private static double average(Alignment alignment) {
		double total = 0.0;
		for (Match match : alignment.getMatches()) {
			total += match.getScore();
		}
		return total / alignment.size();
	}
	
	private static double average(Alignment alignment, Activity activity) {
		double total = 0.0;
		for (Match match : alignment.getMatches(activity)) {
			total += match.getScore();
		}
		return total / alignment.size();
	}
	
	private static double coVar(Alignment alignment) {
		double avg = average(alignment);
		double sd = 0.0;
		for (Match match : alignment.getMatches()) {
			sd = sd + Math.pow(match.getScore() - avg, 2);
		}
		return Math.sqrt(sd / alignment.size()) / avg;
	}
	
	
	private static double distfromavgSentence(Alignment alignment) {
		if (alignment.hasUnaligned()) {
			return -3.0;
		}
		double total = 0.0;
		for (Activity activity : alignment.getActivities()) {
			total += distanceFromAverageSentences(alignment, activity);
		}
		return total / alignment.size();
	}
	
	
	private static double coVar(Alignment alignment, Activity activity) {
		double avg = average(alignment, activity);
		double sd = 0.0;
		for (Match match : alignment.getMatches(activity)) {
			sd = sd + Math.pow(match.getScore() - avg, 2);
		}
		return Math.sqrt(sd / alignment.getMatches(activity).size()) / avg;
	}
	
	
	private static double distanceFromAverageAlignments(Alignment alignment, Activity activity) {
		if (alignment.getMatchingScore(activity) == 0.0) {
			return -10.00;
		}
		return (alignment.getMatchingScore(activity) - average(alignment));
	}
	
	private static double minDistanceFromAverageAlignments(Alignment alignment) {
		double min = Double.MAX_VALUE;
		for (Match match : alignment.getMatches()) {
			double distA = distanceFromAverageAlignments(alignment, match.getActivity());
			if (distA < min) {
				min = distA;
			}
		}
		return min;
	}
	
	
	private static double distanceFromAverageSentences(Alignment alignment, Activity activity) {
		if (alignment.getMatchingScore(activity) == 0.0) {
			return -10.00;
		}
		return (value(alignment, activity) - average(alignment, activity)) / average(alignment, activity);
	}
	
	private static double minDistanceFromAverageSentences(Alignment alignment) {
		if (alignment.hasUnaligned()) {
			return -3.0;
		}
		double min = Double.MAX_VALUE;
		for (Match match : alignment.getMatches()) {
			double distS = distanceFromAverageSentences(alignment, match.getActivity());
			if (distS < min) {
				min = distS;
			}
		}
		return min;
	}
	
	private static double maxDistanceFromBest(Alignment alignment) {
		double max = 0.0;
		for (Activity activity : alignment.getActivities()) {
			double score1 = alignment.getMatchingScore(activity);
			double score2 = alignment.getActivityMatches(activity).getBestMatch().getScore();
			if ((score2 - score1) > max) {
				max = score2 - score1;
			}
		}
		return max;
	}
	
	private static double totalDistanceFromBest(Alignment alignment) {
		double total = 0.0;
		for (Activity activity : alignment.getActivities()) {
			double score1 = alignment.getMatchingScore(activity);
			double score2 = alignment.getActivityMatches(activity).getBestMatch().getScore();
			total = (score2 - score1);
		}
		return total;
	}
	
	private static double dominates(Alignment alignment) {
		int dominating = 0;
		int size = alignment.getActivities().size() * alignment.getSentences().size();
		for (Activity activity : alignment.getActivities()) {
			for (Sentence sentence : alignment.getSentences()) {
				if (isDominating(alignment, activity, sentence)) {
					dominating++;
				}
			}
		}
		return dominating * 1.0 / size;
	}
	
	private static double dominates(Alignment alignment, Activity activity) {
		if (isDominating(alignment, activity)) {
			return 1.0 + alignment.getMatchingScore(activity);
		}
		return alignment.getMatchingScore(activity);
	}
	
	private static boolean isDominating(Alignment alignment, Activity activity) {
		return alignment.getMatch(activity) == alignment.getActivityMatches(activity).getBestMatch();
	}
	
	private static boolean isDominating(Alignment alignment, Activity activity, Sentence sentence) {
		double score = alignment.getActivityMatches(activity).getScore(sentence);
		for (Match match : alignment.getMatches(activity)) {
			if (match.getScore() > score) {
				return false;
			}
		}
		for (Activity activity2 : alignment.getScoreMap().keySet()) {
			double score2 = alignment.getActivityMatches(activity2).getScore(sentence);
			if (score < score2) {
				return false;
			}
		}
		return true;
	}
}
