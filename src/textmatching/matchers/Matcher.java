package textmatching.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import textmatching.alignment.Alignment;
import textmatching.alignment.Match;
import textmatching.matchers.awesomematcher.AwesomeMatcher;
import textmatching.matchers.bfsearcher.BFSearcher;
import textmatching.matchers.markov.run.MarkovMatcher;
import textmatching.matchers.naivematcher.NaiveMatcher;
import textmatching.matching.config.MatchingConfig;
import textmatching.matching.similarity.Scorer;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;

public class Matcher {
	
	protected ProcessDescription description;
	protected ProcessModel model;
	
	private Scorer scorer;
	protected MatchingConfig config;
	
	protected Map<Activity, ActivityMatches> matches;
	
	public Matcher(MatchingConfig config, Scorer scorer,
			ProcessDescription description, ProcessModel model) {
		this.description = description;
		this.model = model;
		this.scorer = scorer;
		this.config = config;
		computeMatchingScores();
	}
	
	public Alignment createAlignment() {
		return null;
	}
	
//	public Alignment runMatcher() {
//		MarkovMatcher markovMatcher = new MarkovMatcher(config, scorer, description, model);
//		return markovMatcher.runMatcher();
		
//		if (config.matchingStrategy == MatchingConfig.NAIVE_MATCHER) {
//			return runNaiveMatcher();
//		}
//		if (config.matchingStrategy == MatchingConfig.STRICT_ASSUMPTION) {
//			BFSearcher searcher = new BFSearcher(config, scorer, description, model);
//			return searcher.performBFSearch();
//		}
//		if (config.matchingStrategy > MatchingConfig.STRICT_ASSUMPTION) {
//			AwesomeMatcher matcher = new AwesomeMatcher(config, scorer, description, model);
//			return matcher.createAlignment();
//		}
		
//		return null;
//	}
	
	
//	private Alignment runBFMatcher() {
//		System.out.println("Starting BF searcher");
////		BFSearcher bfSearcher = new BFSearcher(model, description);
////		Alignment alignment = bfSearcher.performBFSearch();
//		System.out.println("BF solution found.");
//		return alignment;
//	}
	
	private Alignment runNaiveMatcher() {
		System.out.println("Starting naive matcher");
		NaiveMatcher naiveMatcher = new NaiveMatcher(config, scorer, description, model);
		Alignment alignment = naiveMatcher.createAlignment();
		System.out.println("Naive alignment created");
		return alignment;
	}
	

	protected void computeMatchingScores() {
		matches = new HashMap<Activity, ActivityMatches>();
		for (Activity activity : model.getActivities()) {
			List<Match> matchesList = new ArrayList<Match>();
			for (Sentence sentence : description.getSentences()) {
				double similarity = scorer.getSimilarity(activity, sentence);
				Match match = new Match(activity, sentence, similarity);
				matchesList.add(match);
			}
			matches.put(activity, new ActivityMatches(activity, matchesList));
		}
		model.setScoreMap(matches);
	}
	
	protected Match getMatch(Activity activity, Sentence sentence) {
		return matches.get(activity).getMatch(sentence);
	}
	
	protected Match getBestActivityMatch(Activity activity) {
		return matches.get(activity).getBestMatch();
	}
	

	
}
