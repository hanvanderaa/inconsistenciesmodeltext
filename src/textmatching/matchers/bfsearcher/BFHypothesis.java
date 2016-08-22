package textmatching.matchers.bfsearcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import textmatching.alignment.Match;
import textmatching.processmodel.Activity;
import textmatching.util.SortUtils;

public class BFHypothesis {

	private Map<Activity, Match> matches;
	private double currentScore;
	private double futureScore;
	private int lastIndex;
	private Set<Activity> forbiddenActivities;
	
	public BFHypothesis() {
		this.matches = new HashMap<Activity, Match>();
		this.forbiddenActivities = new HashSet<Activity>();
	}
	
	public BFHypothesis(Map<Activity, Match> matches, double currentScore,
			double futureScore) {
		super();
		this.matches = matches;
		this.currentScore = currentScore;
		this.futureScore = futureScore;
		this.forbiddenActivities = new HashSet<Activity>(matches.keySet());

	}

	public void addForbiddenActivities(Collection<Activity> newActivities) {
		forbiddenActivities.addAll(newActivities);
	}
	
	public Set<Activity> getForbiddenActivities() {
		return forbiddenActivities;
	}
	
	public void addMatch(Match match) {
		matches.put(match.getActivity(), match);
		lastIndex = match.getSentenceId();
	}
	
	public void addMatches(Map<Activity, Match> newMatches) {
		for (Match match : newMatches.values()) {
			addMatch(match);
		}
		forbiddenActivities.addAll(newMatches.keySet());
	}
	
	public void setCurrentScore(double score) {
		this.currentScore = score;
	}
	
	public void setFutureScore(double score) {
		this.futureScore = score;
	}
	
	
	public Map<Activity, Match> getMatches() {
		return matches;
	}
	
	public double getPossibleScore() {
		return currentScore + futureScore;
	}
	
	public double getActualScore() {
		return currentScore;
	}
	
	public Set<Activity> getMatchedActivities() {
		return matches.keySet();
	}
	
	public int size() {
		return matches.size();
	}
	
	public void setLastIndex(int index) {
		this.lastIndex = index;
	}
	
	public int lastSentenceIndex() {
		return lastIndex;
	}
	
	
	public String toString() {
		return "size: " + matches.size() + " possible score: " + this.getPossibleScore() +" " + matches.toString();
	}
	
	public List<Activity> getOrdering() {
		return SortUtils.sortBySentenceId(matches);
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matches == null) ? 0 : matches.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BFHypothesis other = (BFHypothesis) obj;
		if (matches == null) {
			if (other.matches != null)
				return false;
		} else if (!matches.equals(other.matches))
			return false;
		return true;
	}

	public int getUniquePhrases() {
		return matches.size();
	}


	
	
	
}
