package textmatching.processmodel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import textmatching.alignment.Match;
import textmatching.matchers.ActivityMatches;
import textmatching.textualdescription.Sentence;

public class ProcessModel extends PartialOrderingGeneric<Activity> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	Map<Activity, ActivityMatches> scoreMap;
	
	public ProcessModel(String name) {
		this.name = name;

	}
	
	
	public ProcessModel(ProcessModel original) {
		this.name = original.getName();
		for (Activity act : original.getActivities()) {
			Activity newAct = new Activity(act.getId(), act.getLabel());
			this.addActivity(newAct);
		}
		for (Activity act1 : original.getActivities()) {
			for (Activity act2 : original.getAllPredecessors(act1)) {
				if (act1 != null && act2 != null) {
					this.addFollowerPair(act2.getId(), act1.getId());
				}
			}
		}
		computeReachabilityRelations();

	}

	

	
	
	public String getName() {
		return name;
	}
	

	
	public List<Activity> getActivities() {
		return getEntries();
	}
	
	
	public Activity getActivity(String label) {
		for (Activity activity : getActivities()) {
			if (activity.getLabel().equals(label)) {
				return activity;
			}
		}
		return null;
	}
	
	public int getActivityCount() {
		return getTCount();
	}
	
	public void setScoreMap(Map<Activity, ActivityMatches> scoreMap) {
		this.scoreMap = scoreMap;
	}
	
	public Map<Activity, ActivityMatches> getScoreMap() {
		return scoreMap;
	}
	
	public ActivityMatches getScores(Activity activity) {
		return scoreMap.get(activity);
	}
	
	public double getScore(Activity activity, int sentenceId) {
		return scoreMap.get(activity).getScore(sentenceId);
	}
	

	public Match getMatch(Activity activity, Sentence sentence) {
		return scoreMap.get(activity).getMatch(sentence);
	}
	
	public void addActivity(Activity activity) {
		addT(activity.getId(), activity);
	}
}
