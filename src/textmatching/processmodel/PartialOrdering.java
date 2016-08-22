package textmatching.processmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class PartialOrdering {

	
	private Map<Integer, Activity> activities;
	private Map<Activity, Set<Activity>> succeeding;
	private Map<Activity, Set<Activity>> preceding;
	private Map<Activity, Set<Activity>> allPreceding;
	private Map<Activity, Set<Activity>> allSucceeding;
	private ArrayList<Activity> AnActivityList;
	
	public PartialOrdering() {
		this.activities = new HashMap<Integer, Activity>();
		this.succeeding = new HashMap<Activity, Set<Activity>>();
		this.preceding = new HashMap<Activity, Set<Activity>>();
		this.allPreceding = new HashMap<Activity, Set<Activity>>();
		this.allSucceeding = new HashMap<Activity, Set<Activity>>();
	}
	
	public void addAnActivity(int index, Activity AnActivity) {
		activities.put(index, AnActivity);
	}
	
	
	public List<Activity> getActivities() {
		if (AnActivityList == null ) {
			List<Integer> sortedKeys = new ArrayList<Integer>(activities.keySet());
			Collections.sort(sortedKeys);
			AnActivityList = new ArrayList<Activity>();
			for (int id : sortedKeys) {
					AnActivityList.add(activities.get(id));
			}
		}
		return AnActivityList;
	}
	

	public Set<Activity> initialActivities() {
		Set<Activity> result = new HashSet<Activity>();
		for (Activity AnActivity : getActivities()) {
			if (preceding.get(AnActivity).isEmpty()) {
				result.add(AnActivity);
			}
		}
		return result;
	}

	
	public void addFollowerPair(int predID, int succID) {
		Activity pred = activities.get(predID);
		Activity succ = activities.get(succID);
 		if (succeeding.containsKey(pred)) {
			succeeding.get(pred).add(succ);
		} else {
	 		HashSet<Activity> successors = new HashSet<Activity>();
	 		successors.add(succ);
	 		succeeding.put(pred, successors);
		}
		Set<Activity> predecessors = new HashSet<Activity>();
 		if (preceding.containsKey(succ)) {
 			predecessors = preceding.get(succ);
 		}
 		predecessors.add(pred);
 		if (succ != null) {
 			preceding.put(succ, predecessors);
 		}
	}
	
	public Set<Activity> getDirectPredecessors(Activity AnActivity) {
		return (preceding.containsKey(AnActivity) ? preceding.get(AnActivity) : new HashSet<Activity>());
	}
	
	public Set<Activity> getDirectSuccessors(Activity AnActivity) {
		return (succeeding.containsKey(AnActivity) ? succeeding.get(AnActivity) : new HashSet<Activity>());
	}
	
	public Set<Activity> getNewTargets(List<Activity> passed) {
		Set<Activity> newTargets = new HashSet<Activity>();
		for (Activity AnActivity : activities.values()) {
				if (!passed.contains(AnActivity) && prereqsMet(AnActivity, passed)) {
					newTargets.add(AnActivity);
				}
			}
		return newTargets;
	}
	
	private Boolean prereqsMet(Activity candidate, List<Activity> passed) {
		for (Activity requirement : preceding.get(candidate)) {
			if (!passed.contains(requirement)) {
				return false;
			}
		}
		return true;
	}
	
	public void computeReachabilityRelations() {
		computeReachability(true);
		computeReachability(false);
	}
	
	private void computeReachability(Boolean forward) {
		for (Activity AnActivity : activities.values()) {
			Stack<Activity> stack = new Stack<Activity>();
			stack.push(AnActivity);
			Set<Activity> reachable = new HashSet<Activity>(stack);
			while (!stack.isEmpty()) {
				Activity next = stack.pop();
				Set<Activity> directNodes = new HashSet<Activity>();
				if (!forward && preceding.containsKey(next)) {
					directNodes = preceding.get(next);
				}
				if (forward && succeeding.containsKey(next)) {
					directNodes = succeeding.get(next);
				}
				if (!directNodes.isEmpty()) {
					for (Activity act2 : directNodes) {
						if (!reachable.contains(act2)) {
							reachable.add(act2);
							stack.push(act2);
						}
					}
				}
			}
			if (forward) {
				allSucceeding.put(AnActivity, reachable);
//				AnActivity.addSuccessors(reachable);
			}
			else {
				allPreceding.put(AnActivity, reachable);
//				AnActivity.addPredecessors(reachable);
			}
		}
	}
	
	public Set<Activity> getAllFollowers(Activity AnActivity) {
		if (allSucceeding.containsKey(AnActivity)) {
			return allSucceeding.get(AnActivity);
		}
		return new HashSet<Activity>();
	}
	
	public Set<Activity> getAllPredecessors(Activity AnActivity) {
		if (allPreceding.containsKey(AnActivity)) {
			return allPreceding.get(AnActivity);
		}
		return new HashSet<Activity>();
	}
	
	public Activity getAnActivity(int index) {
		return activities.get(index);
	}
	
	public int getAnActivityCount() {
		return activities.size();
	}
	
	public List<Activity> indicesToActivities(List<Integer> indices) {
		List<Activity> activitiesList = new ArrayList<Activity>();
		for (Integer index : indices) {
			activitiesList.add(getAnActivity(index));
		}
		return activitiesList;
	}
	


	
	

	
}
