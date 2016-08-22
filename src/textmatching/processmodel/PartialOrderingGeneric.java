package textmatching.processmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class PartialOrderingGeneric<T> implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, T> entries;
	private Map<T, Set<T>> succeeding;
	private Map<T, Set<T>> preceding;
	private Map<T, Set<T>> allPreceding;
	private Map<T, Set<T>> allSucceeding;
	private ArrayList<T> TList;
	
	public PartialOrderingGeneric() {
		this.entries = new HashMap<Integer, T>();
		this.succeeding = new HashMap<T, Set<T>>();
		this.preceding = new HashMap<T, Set<T>>();
		this.allPreceding = new HashMap<T, Set<T>>();
		this.allSucceeding = new HashMap<T, Set<T>>();
	}
	
	public void addT(int index, T T) {
		entries.put(index, T);
	}
	
	public T getEntry(int id) {
		return entries.get(id);
	}
	
	public List<T> getEntries() {
		if (TList == null ) {
			List<Integer> sortedKeys = new ArrayList<Integer>(entries.keySet());
			Collections.sort(sortedKeys);
			TList = new ArrayList<T>();
			for (int id : sortedKeys) {
					TList.add(entries.get(id));
			}
		}
		return TList;
	}
	

	public Set<T> initialentries() {
		Set<T> result = new HashSet<T>();
		for (T T : getEntries()) {
			if (preceding.get(T).isEmpty()) {
				result.add(T);
			}
		}
		return result;
	}

	public void addFollowerPair(T pred, T succ) {
 		if (succeeding.containsKey(pred)) {
			succeeding.get(pred).add(succ);
		} else {
	 		HashSet<T> successors = new HashSet<T>();
	 		successors.add(succ);
	 		succeeding.put(pred, successors);
		}
		Set<T> predecessors = new HashSet<T>();
 		if (preceding.containsKey(succ)) {
 			predecessors = preceding.get(succ);
 		}
 		predecessors.add(pred);
 		if (succ != null) {
 			preceding.put(succ, predecessors);
 		}
	}
	
	
	public void addFollowerPair(int predID, int succID) {
		T pred = entries.get(predID);
		T succ = entries.get(succID);
		addFollowerPair(pred, succ);
	}
	
	public Set<T> getDirectPredecessors(T T) {
		return (preceding.containsKey(T) ? preceding.get(T) : new HashSet<T>());
	}
	
	public Set<T> getDirectSuccessors(T T) {
		return (succeeding.containsKey(T) ? succeeding.get(T) : new HashSet<T>());
	}
	
	public Set<T> getNewTargets(List<T> passed) {
		Set<T> newTargets = new HashSet<T>();
		for (T T : entries.values()) {
				if (!passed.contains(T) && prereqsMet(T, passed)) {
					newTargets.add(T);
				}
			}
		return newTargets;
	}
	
	private Boolean prereqsMet(T candidate, List<T> passed) {
		for (T requirement : preceding.get(candidate)) {
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
		for (T T : entries.values()) {
			Stack<T> stack = new Stack<T>();
			stack.push(T);
			Set<T> reachable = new HashSet<T>(stack);
			while (!stack.isEmpty()) {
				T next = stack.pop();
				Set<T> directNodes = new HashSet<T>();
				if (!forward && preceding.containsKey(next)) {
					directNodes = preceding.get(next);
				}
				if (forward && succeeding.containsKey(next)) {
					directNodes = succeeding.get(next);
				}
				if (!directNodes.isEmpty()) {
					for (T act2 : directNodes) {
						if (!reachable.contains(act2)) {
							reachable.add(act2);
							stack.push(act2);
						}
					}
				}
			}
			if (forward) {
				reachable.remove(T);
				allSucceeding.put(T, reachable);
//				T.addSuccessors(reachable);
			}
			else {
				reachable.remove(T);
				allPreceding.put(T, reachable);
//				T.addPredecessors(reachable);
			}
		}
	}
	
	public Set<T> getAllFollowers(T T) {
		if (allSucceeding.containsKey(T)) {
			return allSucceeding.get(T);
		}
		return new HashSet<T>();
	}
	
	public Set<T> getAllPredecessors(T T) {
		if (allPreceding.containsKey(T)) {
			return allPreceding.get(T);
		}
		return new HashSet<T>();
	}
	
	public T getT(int index) {
		return entries.get(index);
	}
	
	public int getTCount() {
		return entries.size();
	}
	
	public List<T> indicesToentries(List<Integer> indices) {
		List<T> entriesList = new ArrayList<T>();
		for (Integer index : indices) {
			entriesList.add(getT(index));
		}
		return entriesList;
	}
	

	
	

	
}
