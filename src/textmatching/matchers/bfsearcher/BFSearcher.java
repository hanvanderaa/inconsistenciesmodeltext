package textmatching.matchers.bfsearcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import textmatching.alignment.Alignment;
import textmatching.alignment.Match;
import textmatching.matchers.Matcher;
import textmatching.matching.config.MatchingConfig;
import textmatching.matching.similarity.Scorer;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.processmodel.PartialOrdering;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;

public class BFSearcher extends Matcher {


//	private ModelTextPair mtp;
	private MatchingConfig config;
	private List<Activity> activities;
	private ProcessDescription description;
	private List<BFHypothesis> queue;
	private Set<BFHypothesis> searched;
	private List<BFHypothesis> bestHyps;
	private int iteration;
	
	public BFSearcher(MatchingConfig config, Scorer scorer,
			ProcessDescription description, ProcessModel model) {
		super(config, scorer, description, model);
//		computeMatchingScores();
		this.config = config;
		this.activities = model.getActivities();
		this.description = description;
		this.queue = new ArrayList<BFHypothesis>();
		this.searched = new HashSet<BFHypothesis>();
		this.bestHyps = new ArrayList<BFHypothesis>();
	}
	
	@Override
	public Alignment createAlignment() {
		initialize();
		int max = 1;
		int maxiters = 8000;
		while (iteration < maxiters && queue.size() > 0 && queue.get(0).getPossibleScore() >= bestHyps.get(0).getActualScore()) {
			double oldScore = bestHyps.get(0).getActualScore();
			updateSolutions();

			doIteration();
			
			double newScore = bestHyps.get(0).getActualScore();
			if (newScore > oldScore) {
				filterQueue(queue, newScore);
			}
			
			// just for logging purposes
			if (queue.size() > max) {
				max = queue.size();
			}
			iteration++;
			if (iteration % 1000 == 0) {
				System.out.println("iteration " + iteration + " done. queue size: " + queue.size() + " solutions: " + bestHyps.size() +
				" actual score: " + bestHyps.get(0).getActualScore() + " max possible score: " + queue.get(0).getPossibleScore());
			}
		}
		System.out.println("Completed in " + iteration + " iterations. Max queue size: " + max + ". solution: " + bestHyps.get(0).getActualScore());
		return createAlignment(bestHyps.get(0));
	}
	
	private void initialize() {
		iteration = 0;
		BFHypothesis initHyp = new BFHypothesis();
		initHyp.setCurrentScore(0);
		initHyp.setFutureScore(getFutureScore(initHyp));
		queue.add(initHyp);
		bestHyps.add(initHyp);

	}
	
	private Alignment createAlignment(BFHypothesis bestHyp) {
		Alignment alignment = new Alignment(model.getName(), config, model.getActivities(), model.getScoreMap());
		for (Match match : bestHyp.getMatches().values()) {
			alignment.addMatch(match);
		}
		return alignment;
	}
	
	private void updateSolutions() {
		//TODO: how about checking here which solution is better and actually only keeping 1? saves memory, but not iterations
		BFHypothesis bestHyp = queue.get(0);
		if (bestHyp.getActualScore() > bestHyps.get(0).getActualScore()) {
			bestHyps.clear();
			bestHyps.add(bestHyp);
		}
		if (Double.compare(bestHyp.getActualScore(), bestHyps.get(0).getActualScore()) == 0) {
			bestHyps.add(bestHyp);	
		}
	}
	
	private void doIteration() {
		BFHypothesis best = queue.get(0);
		if (Double.compare(best.getActualScore(), best.getPossibleScore()) != 0) {
			addNonZeroHyps(best, best.lastSentenceIndex());
			if (best.lastSentenceIndex() + 1 < description.getSentenceCount()) {
				addNonZeroHyps(best, best.lastSentenceIndex() + 1);
				addNullHyp(best, best.lastSentenceIndex() + 1);
			}
			searched.add(best);
			queue.remove(0);
			Collections.sort(queue, new HypothesisComparator());
		} else {
			searched.add(best);
			queue.remove(0);
		}
	}
	
	private void filterQueue(List<BFHypothesis> queue, double minScore) {
		Iterator<BFHypothesis> itr = queue.iterator();
		while (itr.hasNext()) {
			BFHypothesis hyp = itr.next();
			if (hyp.getPossibleScore() < minScore) {
				itr.remove();
			}
		}
	}
	
	private void addNonZeroHyps(BFHypothesis current, int sentenceID) {
		Sentence sentence = description.getSentence(sentenceID);
		for (Activity activity : activities) {
			Match match = getMatch(activity, sentence);
			if (!current.getForbiddenActivities().contains(activity)
					&& (match.getScore() > 0) ) {
				BFHypothesis newHyp = getNewBFHyp(current, match);
				if (newHyp.getPossibleScore() >= bestHyps.get(0).getActualScore() && !searched.contains(newHyp)
						&& !queue.contains(newHyp)) {
					queue.add(newHyp);
				}
			}
		}
	}
	
	private void addNullHyp(BFHypothesis current, int sentenceID) {
		BFHypothesis result = new BFHypothesis();
		result.addMatches(current.getMatches());
		result.addForbiddenActivities(getForbiddenActivities(result));
		result.setLastIndex(sentenceID);
		result.setCurrentScore(current.getActualScore());
		result.setFutureScore(getFutureScore(result));
		queue.add(result);
	}
	
	private BFHypothesis getNewBFHyp(BFHypothesis current, Match match) {
		BFHypothesis result = new BFHypothesis();
		result.addMatches(current.getMatches());
		result.addMatch(match);
		result.addForbiddenActivities(getForbiddenActivities(result));
		result.setCurrentScore(getCurrentScore(result.getMatches().values()));
		result.setFutureScore(getFutureScore(result));
		return result;
	}
	
	private Set<Activity> getForbiddenActivities(BFHypothesis hyp) {
		Set<Activity> result = new HashSet<Activity>();
		for (Activity activity : hyp.getMatchedActivities()) {
			result.addAll(model.getAllPredecessors(activity));
		}
		return result;
	}
	
	private double getCurrentScore(Collection<Match> matches) {
		double total = 0.0;
		for (Match match : matches) {
			total += match.getScore();
		}
		return total;
	}
	
	private double getFutureScore(BFHypothesis hyp) {
		double total = 0.0;
		for (Activity activity : activities) {
			if (!hyp.getForbiddenActivities().contains(activity)) {
				total += matches.get(activity).getBestMatch(hyp.lastSentenceIndex()).getScore();
			}
		}
		return total;
	}
	

	
	
}
