package textmatching.matchers.naivematcher;

import textmatching.alignment.Alignment;
import textmatching.alignment.Match;
import textmatching.matchers.Matcher;
import textmatching.matching.config.MatchingConfig;
import textmatching.matching.similarity.Scorer;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;

public class NaiveMatcher extends Matcher{

	public NaiveMatcher(MatchingConfig config, Scorer scorer,
			ProcessDescription description, ProcessModel model) {
		super(config, scorer, description, model);
		computeMatchingScores();
	}

	
	@Override
	public Alignment createAlignment() {
		Alignment alignment = new Alignment(model.getName(), config, model.getActivities(), model.getScoreMap());
		for (Activity activity : model.getActivities()) {
			Match bestMatch = getBestActivityMatch(activity);
			if (bestMatch != null && bestMatch.getScore() > 0) {
				alignment.addMatch(bestMatch);
			}
		}
		return alignment;
	}
	

}
