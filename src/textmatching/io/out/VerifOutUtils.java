package textmatching.io.out;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import textmatching.evaluation.PrecisionRecallStats;
import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;

public class VerifOutUtils {

	public static final String[] CONFIG_CSV_HEADER = 
			new String[]{"id", "preprocessing mode", "similarity mode", "solver strategy"};
	
	public static String[] CONFIG_CSV(int id, MatchingConfig config) {
			return new String[]{"config" + dts(id), config.preprocessorToString(), config.similarityModeToString(), config.strategyToString()};
	}
	

	public static String[] CHECK_SUMMARY_CSV_HEADER = 
			new String[]{"# mtps", "with errors", "without errors"};
	
	
	private static final String dts(int i) {
		return String.valueOf(i);
	}
	
	private static final String dts(double d) {
		String result = String.format("%.3f", d);
		return result.replace('.', ',');
	}



	public static String[] THRESHOLD_SUMMARY_HEADER1(
			List<MatchingConfig> configs) {
		String[] result = new String[]{"threshold"};
		for (int i = 0; i < configs.size(); i++) {
			result = ArrayUtils.addAll(result, new String[]{"config" + dts(i), "", "", "", ""});
		}
		return result;
	}
	
	
	public static final String[] THRESHOLD_SUMMARY_HEADER2(List<MatchingConfig> configs) {
		String[] result = new String[1];
		for (int i = 0; i < configs.size(); i++) {
			result = ArrayUtils.addAll(result, new String[]{"# marked", "#correct", "precision", "recall", "fmeasure", ""});
		}
		return result;
	}
	

	
	public static final String[] GRAPH_SUMMARY(double recall, double jump, List<MatchingConfig> configs,
		Map<MatchingConfig, PrecisionRecallStats> statsMap) {
		String[] result = new String[]{dts(recall)};
		for (MatchingConfig config : configs) {
			result = ArrayUtils.addAll(result, new String[]{dts(statsMap.get(config).getPrecisionAt(recall, jump))});
		}
		return result;	
	}
	
	public static final String[] SUMMARY_FOR_MTP(List<MatchingConfig> configs, ModelTextPair mtp) {
		String[] result = new String[]{mtp.getName(), dts(mtp.getGroundTruth().inconsistencyCount())};
		for (MatchingConfig config : configs) {
//			result = ArrayUtils.add(result, dts(mtp.getAlignment(config).getPriorityScore()));
		}
		return result;
	}
	
}
