package textmatching.alignmentverification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;
import textmatching.util.CleanupUtils;

public class MTPAssertion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3697419693329043681L;
	List<String> sentencesMissedInGT;
	List<String> sentencesMissedInTXT;
	List<String> activitiesMissedInGT;
	List<String> activitiesMissedInJSON;
	
	
	public MTPAssertion(ProcessModel model, ProcessDescription description, GroundTruth gt) {
		activitiesMissedInGT = findActivitiesMissingGT(model, gt);
		activitiesMissedInJSON = findActivitiesMissingJSON(model, gt);
		sentencesMissedInGT = findSentencesMissingGT(description, gt);
		sentencesMissedInTXT = findSentencesMissingTXT(description, gt);
	}
	
	
	public String toString() {
		String result = "";
//		if (!activitiesMissedInJSON.isEmpty()) {
//			result += activitiesMissedInJSON.toString();
//		}
		if (!activitiesMissedInGT.isEmpty()) {
			result += activitiesMissedInGT.toString();
		}
		if (!sentencesMissedInTXT.isEmpty()) {
			result += sentencesMissedInTXT.toString();
		}
////		if (!sentencesMissedInGT.isEmpty()) {
////			result += sentencesMissedInGT.toString();
////		}

		return result;
	}
	
	
	private List<String> findSentencesMissingGT(ProcessDescription description, GroundTruth gt) {
		List<String> result = new ArrayList<String>();
		for (Sentence sentence : description.getSentences()) {
			if (!containsSentence(gt.getSentences(), sentence)) {
				result.add(sentence.getOriginal());
			}
		}
		return result;
	}
	
	private List<String> findSentencesMissingTXT(ProcessDescription description, GroundTruth gt) {
		List<String> result = new ArrayList<String>();
		for (Sentence sentence : gt.getSentences()) {
			if (!containsSentence(description.getSentences(), sentence)) {
				result.add(sentence.getOriginal());
			}
		}
		return result;
	}
	
	
	
	private List<String> findActivitiesMissingGT(ProcessModel model, GroundTruth gt) {
		List<String> result = new ArrayList<String>();
		for (Activity activity : model.getActivities()) {
			if (!gt.gtContainsActivityLabel(CleanupUtils.cleanActivityLabel(activity.getLabel()))) {
				result.add(activity.getLabel());
			}
		}
		return result;
	}
	
	private List<String> findActivitiesMissingJSON(ProcessModel model, GroundTruth gt) {
		List<String> result = new ArrayList<String>();
		for (String activitylabel : gt.getActivityLabels()) {
			if (!containsLabel(model.getActivities(), activitylabel)) {
				result.add(activitylabel);
			}
		}
		return result;
	}
	
	private Boolean containsLabel(Collection<Activity> activities, String label) {
		for (Activity activity : activities) {
			if (CleanupUtils.cleanActivityLabel(activity.getLabel()).equals(label)) {
				return true;
			}
		}
		return false;
	}
	
	private Boolean containsSentence(List<Sentence> sentences, Sentence sentence) {
		return sentences.contains(sentence);
	}
	
	private Boolean containsSentenceId(List<Sentence> sentences, Sentence sentence) {
		if (sentences.contains(sentence)) {
			return true;
		}
		for (Sentence sentence2 : sentences) {
			if (String.valueOf(sentence2.getId()).equals(sentence.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public int getActivityScoring() {
		return activitiesMissedInGT.size() + activitiesMissedInJSON.size();
	}
	
}
