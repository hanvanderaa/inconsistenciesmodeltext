package textmatching.alignmentverification;

import textmatching.alignment.Alignment;
import textmatching.alignment.Match;
import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;

public class AlignmentVerifier {


	public AlignmentVerifier() {
	}
	

	public VerifiedAlignment verifySolution(ModelTextPair mtp, MatchingConfig config) {
		Alignment alignment = mtp.getAlignment(config);
		GroundTruth groundTruth = mtp.getGroundTruth();
		ProcessModel model = mtp.getAnalyzedModel(config);
		VerifiedAlignment verifiedAlignment = new VerifiedAlignment(alignment.getName(), alignment.getConfig());
		
		for (Activity activity : alignment.getActivities()) {
			Match match = alignment.getMatch(activity);
			String gtSentence = groundTruth.getSentenceString(activity);
			int gtSentenceId = groundTruth.getSentenceID(activity);
			double gtScore = model.getScore(activity, gtSentenceId);
			verifiedAlignment.addVerification(verifyActivity(activity, match, gtSentence, gtSentenceId, gtScore));
		}
		return verifiedAlignment;
	}
	
	
	private ActivityVerification verifyActivity(Activity activity, Match match,
			String gtSentence, int gtSentenceId, double gtScore ) {
		if (match != null) {
			return new ActivityVerification(match, gtSentence, gtSentenceId, gtScore, getStatus(match, gtSentence, gtSentenceId));
		} 
		return new ActivityVerification(activity, gtSentence, gtSentenceId,  getStatus(match, gtSentence, gtSentenceId));
	}
	
	private VerificationStatus getStatus(Match match, String gtSentence, int gtSentenceId) {
		boolean alignmentMatch = (match != null);
		boolean gtMatch = (gtSentence != null);
		if (alignmentMatch && gtMatch) {
			if (match.getSentence().getOriginal().equals(gtSentence) || 
					match.getSentenceId() == gtSentenceId) {
				return VerificationStatus.CORRECTMATCH;
			}
			return VerificationStatus.WRONGSENTENCE;
		}
		if (!alignmentMatch && !gtMatch) {
			return VerificationStatus.CORRECTNONMATCH;
		}
		if (alignmentMatch && !gtMatch) {
			return VerificationStatus.FALSEMATCH;
		}
		if (!alignmentMatch && gtMatch) {
			return VerificationStatus.MISSEDMATCH;
		}
		return null;
	}
	

}
