package textmatching.io.out;


import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import textmatching.alignment.Match;
import textmatching.alignmentverification.ActivityVerification;
import textmatching.alignmentverification.GroundTruth;
import textmatching.alignmentverification.VerificationSummary;
import textmatching.alignmentverification.VerifiedAlignment;
import textmatching.matchers.ActivityMatches;
import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.processmodel.Activity;
import textmatching.textualdescription.Sentence;
import textmatching.util.PrintUtils;

public class CSVOutUtils {

	public static final char separator = ';';
	
	public static final String[] MTP_CSV_OVERVIEW_HEADER = 
			new String[]{"modelname", "# activities", "# sentences"};
	
	
	public static String[] MTP_CSV_OVERVIEW(ModelTextPair mtp) {
		return new String[]{mtp.getName(), 
				dts(mtp.getActivityCount()), dts(mtp.getSentenceCount())};
	}
	
	public static final String[] MTP_CSV_GROUNDTRUTH_HEADER = 
			new String[]{"Gold standard", "# aligned activities", "# unaligned activities"};
	
	public static String[] MTP_CSV_GROUNDTRUTH(GroundTruth gt) {
		return new String[]{"", dts(gt.getAlignedCount()), dts(gt.missingActivityCount())};
	}
	
	public static final String[] CONFIG_CSV_HEADER = 
			new String[]{"preprocessing mode", "similarity mode", "solver strategy"};
	
	public static String[] CONFIG_CSV(MatchingConfig config) {
			return new String[]{config.preprocessorToString(), config.similarityModeToString(), config.strategyToString()};
	}
	
	public static final String[] MTP_CSV_RESULT_SUMMARY_HEADER = 
			ArrayUtils.addAll(CONFIG_CSV_HEADER, new String[]{"# aligned activities", "precision", "recall", "fmeasure"});
					
	private static String[] MTP_CSV_RESULT_SUMMARY(VerifiedAlignment verification) {
		return new String[]{
				dts(verification.getAlignedActivitiesCount()),
				dts(verification.getPrecision()),
				dts(verification.getRecall()),
				dts(verification.getFmeasure())};
	}
	
	public static String[] MTP_CSV_RESULT_SUMMARY(MatchingConfig config, VerifiedAlignment verification) {
		return ArrayUtils.addAll(CONFIG_CSV(config), MTP_CSV_RESULT_SUMMARY(verification));
	}
	
	public static final String[][] MTP_CSV_RESULT_FOR_CONFIG(MatchingConfig config, VerifiedAlignment verification) {
		String[][] result = new String[verification.getActivityCount() + 3][MTP_CSV_RESULT_FOR_CONFIG_HEADER2.length];
		result[0] = MTP_CSV_RESULTS_FOR_CONFIG_HEADER;
		result[1] = CONFIG_CSV(config);
		result[2] = MTP_CSV_RESULT_FOR_CONFIG_HEADER2;
		
		int i = 3;
		for (Activity activity : verification.getActivities()) {
			result[i] = MTP_CSV_ACTIVITYVERIFICATION(verification.getVerification(activity));
			i++;
		}
		return result;
	}

	private static final String[] MTP_CSV_RESULTS_FOR_CONFIG_HEADER = 
			new String[]{"Results for configuration"};
	
	private static final String[] MTP_CSV_RESULT_FOR_CONFIG_HEADER2 = 
			new String[]{"activity", "verification", "score", "aligned sentence", "score", "gt sentence"};
	
	private static final String[] MTP_CSV_ACTIVITYVERIFICATION(ActivityVerification verif) {
		return new String[]{verif.getActivityLabel(), verif.getStatus().toString(), dts(verif.getScore()),
				verif.getAlignmentSentence(), dts(verif.getGtScore()), verif.getGtSentence()};
		}
	
	public static final String[] MTP_CSV_DESCRIPTION_OVERVIEW_HEADER = 
			new String[]{"id", "sentence", "actions"};
	
	public static final String[] MTP_CSV_DESCRIPTION_OVERVIEW(Sentence sentence) {
		return new String[]{dts(sentence.getId()), sentence.getOriginal()};
	}
	
	public static final String[] MTP_CSV_SCORES_HEADER(int sentenceCount) {
		String[] result = new String[sentenceCount + 2];
		result[0] = "activity";
		for (int i = 0; i < sentenceCount; i++) {
			result[i + 2] = dts(i);
		}
		return result;
	}
	
	public static final String[] MTP_CSV_SCORES(Activity activity, ActivityMatches activityMatches) {
		String[] result = new String[activityMatches.size() + 2];
		result[0] = String.valueOf(activity.getId());
		result[1] = activity.getLabel();
		for (int i = 0; i < result.length - 2; i++) {
			result[i + 2] = dts(activityMatches.getMatch(i).getScore());
		}
		return result;
	}
	
	
	
	//
	//
	//
	// all stuff for summaries
	
	public static final String[] SUMMARY_HEADER = 
			new String[]{"# of configs", "# of model-texts", "# of activities", "# of sentences" };
	
	
	public static final String[] SUMMARY(VerificationSummary summary) {
		return new String[]{String.valueOf(summary.getConfigCount()), dts(summary.getMtpCount()),
				dts(summary.totalActivities()), dts(summary.totalSentences())};
	}
	
	public static final String[] SUMMARY_FOR_CONFIG_HEADER =
			MTP_CSV_RESULT_SUMMARY_HEADER;
	
	public static final String[] SUMMARY_FOR_CONFIG(MatchingConfig config, VerificationSummary summary) {
		return ArrayUtils.addAll(CONFIG_CSV(config), new String[]{
			dts(summary.totalAligned(config)), 
			dts(summary.getPrecision(config)), dts(summary.getRecall(config)), dts(summary.getFMeasure(config))
		});
	}
	
	public static final String[] SUMMARY_FOR_MTP_HEADER1(List<MatchingConfig> configs) {
		String[] result = new String[]{"name", "# activities", "# sentences", "# gt aligned activities", "mtp errors"};
		for (MatchingConfig config : configs) {
			result = ArrayUtils.addAll(result, new String[]{config.toString(), "", "", ""});
		}
		return result;
	}
	
	public static final String[] SUMMARY_FOR_MTP_HEADER2(List<MatchingConfig> configs) {
		String[] result = new String[5];
		for (int i = 0; i < configs.size(); i++) {
			result = ArrayUtils.addAll(result, new String[]{"# aligned", "precision", "recall", "fmeasure"});
		}
		return result;
	}
	
	public static final String[] SUMMARY_FOR_MTP(List<MatchingConfig> configs, ModelTextPair mtp) {
		String[] result = MTP_CSV_OVERVIEW(mtp);
		result = ArrayUtils.addAll(result, new String[]{
				String.valueOf(mtp.getGroundTruth().getAlignedCount()), mtp.getMTPAssertion().toString()});
		return result;
	}
	
	private static final String dts(int i) {
		return String.valueOf(i);
	}
	
	private static final String dts(double d) {
		String result = String.format("%.3f", d);
		return result.replace('.', ',');
	}
	
}
