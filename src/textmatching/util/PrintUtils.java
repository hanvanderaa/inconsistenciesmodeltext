package textmatching.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import textmatching.alignment.Alignment;
import textmatching.alignment.Match;
import textmatching.alignmentverification.VerificationSummary;
import textmatching.alignmentverification.VerifiedAlignment;
import textmatching.io.out.CSVOutUtils;
import textmatching.matching.config.MatchingConfig;
import textmatching.processmodel.Activity;
import edu.stanford.nlp.trees.Tree;

public class PrintUtils {

	
	public static String nodesToString(List<Tree> nodes) {
		List<String> _result = new ArrayList<String>(nodes.size());
		String resultstr = "";
		for(Tree t:nodes) {
			for(Tree leaf: t.getLeaves()) {
				_result.add(leaf.value());
				resultstr = resultstr + leaf.value() + " ";
			}
		}
		return CleanupUtils.cleanActivityLabel(resultstr);
	}
	
	public static String treeToString(Tree t) {
		List<String> punctuation = Arrays.asList(new String[]{",", ".", ";", "!", "?"});
		String result = "";
		for (Tree leaf : t.getLeaves()) {
			if (!result.equals("") && !punctuation.contains(leaf.toString())) {
				result += " ";
			}
			result += leaf.toString();
		}
		return result;
	}
	
	public static void outputAlignment(Alignment alignment) {
		for (Match match : alignment.getMatches()) {
			System.out.println(match.toString());
		}
	}
	
	public static void printVerication(VerifiedAlignment verification) {
		System.out.println("Verification results for :" + verification);
		for (Activity activity : verification.getActivities()) {
			System.out.println(verification.getVerification(activity));
		}
		
	}
	
	
	public static void outputSummary(VerificationSummary summary) {
		System.out.println(Arrays.toString(CSVOutUtils.SUMMARY_HEADER));
		System.out.println(Arrays.toString(CSVOutUtils.SUMMARY(summary)));
		
		System.out.println(Arrays.toString(CSVOutUtils.SUMMARY_FOR_CONFIG_HEADER));
		for (MatchingConfig config : summary.getConfigs()) {
			System.out.println(Arrays.toString(CSVOutUtils.SUMMARY_FOR_CONFIG(config, summary)));
		}
	}

	
	public static final String dts(double d) {
		String result = String.format("%.3f", d);
		return result.replace('.', ',');
	}
	
	public static final String dts2(double d) {
		return dts(d).replace(',', '.');
	}
	
	public static final String createPredicate(String predicate, Object[] entries) {
		String output = predicate + "(";
		for (int i = 0; i < entries.length; i++) {
			Object entry = entries[i];
			String entrystr = "";
			if (entry instanceof String) {
				entrystr = "\"" + entry + "\"";
				entrystr = entrystr.replaceAll(",", "");
			}
			if (entry instanceof Double){
				entrystr = dts2((Double) entry);
			}
			
			if (i != (entries.length - 1)) {
				entrystr = entrystr + ", ";
			}
			output += entrystr;
		}
		output = output + ")\n";
		return output;
	}
	
	public static double fMeasure(double prec, double rec) {
		if (rec == 0.0 || prec == 0.0) {
			return 0.0;
		}
		return 2 * (prec * rec) / (prec + rec);
	}
	
}
