package textmatching.alignment;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import textmatching.matchers.ActivityMatches;
import textmatching.matching.config.MatchingConfig;
import textmatching.processmodel.Activity;
import textmatching.textualdescription.Sentence;

public class Alignment implements Serializable {

	private static final long serialVersionUID = 1900599104543709502L;
	private String name;
	private MatchingConfig config;
	
	private List<Activity> activities;
	private Set<Sentence> sentences;
	private Map<Activity, Match> matches;
	private Map<Activity, ActivityMatches> scoreMap;
	
	
	
	public Alignment(String name, MatchingConfig config, List<Activity> activities, Map<Activity, ActivityMatches> scoreMap) {
		this.name = name;
		this.config = config;
		this.activities = activities;
		this.scoreMap = scoreMap;
		sentences = null;
		matches = new HashMap<Activity, Match>();
	}
	
	public List<Activity> getActivities() {
		return activities;
	}

	public Match getMatch(Activity activity) {
		return matches.containsKey(activity) ? matches.get(activity) : null;
	}
	
	public double getMatchingScore(Activity activity) {
		if (getMatch(activity) == null) {
			return 0.0;
		}
		return getMatch(activity).getScore();
	}

	
	public void addMatch(Match match) {
		matches.put(match.getActivity(), match);
	}
	
	public int size() {
		return matches.size();
	}

	public Collection<Match> getMatches() {
		return matches.values();
	}
	
	public String getName() {
		return name;
	}
	
	public MatchingConfig getConfig() {
		return config;
	}

	public boolean hasUnaligned() {
		return activities.size() > matches.size();
	}
	
	public boolean hasMatch(Activity activity) {
		return matches.containsKey(activity);
	}
	
	public Map<Activity, ActivityMatches> getScoreMap() {
		return scoreMap;
	}
	
	public Collection<Match> getMatches(Activity activity) {
		return scoreMap.get(activity).getMatches();
	}
	
	public ActivityMatches getActivityMatches(Activity activity) {
		return scoreMap.get(activity);
	}

	public Collection<Sentence> getSentences() {
		if (sentences == null) {
			sentences = new HashSet<Sentence>();
			for (Activity activity : activities) {
				for (Match match : getMatches(activity)) {
					sentences.add(match.getSentence());
				}
			}
		}
		return sentences;
	}
	
}
