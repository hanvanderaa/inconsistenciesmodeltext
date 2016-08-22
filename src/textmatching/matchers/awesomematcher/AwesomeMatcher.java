package textmatching.matchers.awesomematcher;

import textmatching.alignment.Alignment;
import textmatching.matchers.Matcher;
import textmatching.matching.config.MatchingConfig;
import textmatching.matching.similarity.Scorer;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;

public class AwesomeMatcher extends Matcher {

//	private BPAnalyzer bpAnalyzer;
	
	public AwesomeMatcher(MatchingConfig config, Scorer scorer,
			ProcessDescription description, ProcessModel model) {
		super(config, scorer, description, model);
		computeMatchingScores();
//		bpAnalyzer = new BPAnalyzer(model);
	}
	
	public Alignment createAlignment() {
		return new Alignment(model.getName(), config, model.getActivities(), model.getScoreMap());
	}
	
	
	
	
	
}
