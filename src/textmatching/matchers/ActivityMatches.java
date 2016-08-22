package textmatching.matchers;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import textmatching.alignment.Match;
import textmatching.processmodel.Activity;
import textmatching.textualdescription.Sentence;

public class ActivityMatches implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6817074923770094354L;
	private Activity activity;
	private Map<Integer, Match> matches;
	private Map<Integer, Match> bestMatches;
	
	
	public ActivityMatches(Activity activity, List<Match> matchesList) {
		this.activity = activity;
		populateMap(matchesList);
		populateBestMap();
	}
	
	public int size() {
		return matches.size();
	}
	
	public double getScore(int sentenceId) {
		return matches.containsKey(sentenceId) ? matches.get(sentenceId).getScore() : 0.0;
	}
	
	public double getScore(Sentence sentence) {
		return getScore(sentence.getId());
	}
	
	public Match getMatch(Sentence sentence) {
		return getMatch(sentence.getId());
	}
	
	public Match getMatch(int sentenceId) { 
		return matches.get(sentenceId);
	}
	
	public Match getBestMatch() {
		return getBestMatch(0);
	}
	
	public Match getBestMatch(int lastSentence) {
		return bestMatches.get(lastSentence);
	}
	
	private void populateMap(List<Match> matchesList) {
		matches = new HashMap<Integer, Match>();
		for (Match match : matchesList) {
			matches.put(match.getSentenceId(), match);
		}
	}
	
	private void populateBestMap() {
		bestMatches = new HashMap<Integer, Match>();
		Match bestMatch = matches.get(matches.size() - 1);
		for (int i = matches.size() - 1; i >= 0; i--) {
			Match match = matches.get(i);
			if (match.getScore() > bestMatch.getScore()) {
				bestMatch = match;
			}
			bestMatches.put(i, bestMatch);
		}
	}
	
	public Collection<Match> getMatches() {
		return matches.values();
	}
	
	public double getAverage() {
		double total = 0.0;
		for (Match match : matches.values()) {
			total += match.getScore();
		}
		return total / size();
	}
	
}
