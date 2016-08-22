package textmatching.alignmentverification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import textmatching.processmodel.Activity;
import textmatching.textualdescription.Sentence;

public class GroundTruth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3463292285387076115L;
	String name;
	Map<Integer, GTMatch> matches;
	List<String> activityLabels;
	List<Sentence> sentences;
	
	
	public GroundTruth(String name) {
		this.name = name;
		matches = new HashMap<Integer, GTMatch>();
		activityLabels = new ArrayList<String>();
		sentences = new ArrayList<Sentence>();
	}
	
	public void addMatch(String activityLabel, int activityid, String sentence, int sentenceId) {
		activityLabels.add(activityLabel);
		sentences.add(new Sentence(sentenceId, sentence));
		matches.put(activityid, new GTMatch(activityLabel, sentence, sentenceId));
	}
	
	public void addMatch(String activityLabel, int activityid, String sentence, int sentenceId, String errorString) {
		activityLabels.add(activityLabel);
		sentences.add(new Sentence(sentenceId, sentence));
		matches.put(activityid, new GTMatch(activityLabel, sentence, sentenceId, errorString));
	}

	public int getSentenceID(Activity activity) {
		return getSentenceID(activity.getId());
	}

	public int getSentenceID(int activityID) {
		return matches.containsKey(activityID) ? matches.get(activityID).getSentenceId() : -1;
	}
	
	public String getSentenceString(Activity activity) {
		return matches.containsKey(activity.getId()) ? matches.get(activity.getId()).getSentence() : "";
	}
	

	public int getAlignedCount() {
		int count = 0;
		for (int activityid : matches.keySet()) {
			if (getSentenceID(activityid) != -1) {
				count++;
			}
		}
		return count;
	}
	
	public boolean isMissing(Activity activity) {
		return (getSentenceID(activity) == -1);
	}
	
	public List<Integer> getUnalignedActivityIDs() {
		List<Integer> result = new ArrayList<Integer>();
		
		for (int activityid: matches.keySet()) {
			if (getSentenceID(activityid) == -1) {
				result.add(activityid);
			}
		}
		return result;
	
	}
	
	public Boolean gtContainsActivityLabel(String label) {
		return activityLabels.contains(label);
	}
	
	public List<Sentence> getSentences() {
		return sentences;
	}
	
	public List<String> getActivityLabels() {
		return activityLabels;
	}
	
	public String toString() {
		return "GT of: " + name;
	}
	
	public int wrongOrderedCount() {
		int count = 0;
		for (GTMatch match : matches.values()) {
			if (match.getCode() == GTErrorCode.WRONGORDER) {
				count++;
			}
		}
		return count;
	}
	
	public boolean hasWrongOrder() {
		return wrongOrderedCount() > 0;
	}
	
	public boolean hasMissingActivities() {
		return missingActivityCount() > 0;
	}
	
	public int missingActivityCount() {
		return getUnalignedActivityIDs().size();
	}
	
	public int inconsistencyCount() {
		return wrongOrderedCount() + missingActivityCount();
	}
	
	public boolean hasInconsistencies() {
		return inconsistencyCount() > 0;
	}
	
}
