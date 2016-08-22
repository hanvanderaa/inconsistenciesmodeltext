package textmatching.alignmentverification;

import java.io.Serializable;

import textmatching.alignment.Match;
import textmatching.processmodel.Activity;

public class ActivityVerification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1734395701486754763L;
	private Activity activity;
	private String alignmentSentence;
	private String gtSentence;
	private int gtSentenceId;
	private double alignmentScore;
	private double gtScore;
	private VerificationStatus status;
	
	
	public ActivityVerification(Match match, String gtSentence, int gtSentenceId, double gtScore, VerificationStatus status) {
			this(match.getActivity(), match.getSentence().getOriginal(), match.getScore(),
					gtSentence, gtSentenceId, gtScore, status);
	}
	
	public ActivityVerification(Activity activity,
			String alignmentSentence, double alignmentScore, String gtSentence, int gtSentenceid, double gtScore,
			VerificationStatus status) {
		this.activity = activity;
		this.alignmentSentence = alignmentSentence;
		this.alignmentScore = alignmentScore;
		this.gtSentence = gtSentence;
		this.gtSentenceId = gtSentenceId;
		this.gtScore = gtScore;
		this.status = status;
	}
	


	public ActivityVerification(Activity activity, String gtSentence, int gtSentenceId, VerificationStatus status) {
		this(activity, null, 0.0, gtSentence, gtSentenceId, 0.0, status );
	}

	public Activity getActivity() {
		return activity;
	}
	
	public String getActivityLabel() {
		return activity.getLabel();
	}
	
	public String getAlignmentSentence() {
		return alignmentSentence;
	}
	
	public String getGtSentence() {
		return gtSentence;
	}
	
	public int getGtSentenceId(){
		return gtSentenceId;
	}
	
	public double getGtScore() {
		return gtScore;
	}
	
	public double getScore() {
		return alignmentScore;
	}
	
	public VerificationStatus getStatus() {
		return status;
	}
	
	public String toString() {
		return activity.toString() + " --- " + alignmentSentence + " " + status;
	}
	
	
}
