package textmatching.matchers.markov.run;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;


public class Evaluator {

	
	public static int matches = 0;
	public static int totalComputed = 0;
	public static int totalGold = 0;
	public static DecimalFormat df = new DecimalFormat("#,##0.0000");
	
	public static void evaluateResult(String[] modelIDs) throws IOException {
		
		matches = 0;
		totalComputed = 0;
		totalGold = 0;
		
		for (String id: modelIDs) {
			evaluate(id);
		}
		
		double prec = matches*1.0 / totalComputed;
		double rec = matches*1.0 / totalGold;
		System.out.println();
		System.out.println("TOTAL:" +
					"\tRecall: " +  df.format(rec) +
					"\tPrecision: " +  df.format(prec) +
					"\tF-Measure: " + df.format(2* (rec*prec) / (prec+rec)));
	}	
	
	
	private static void evaluate (String modelId) throws IOException {
		String file = modelId + ".txt";
		String goldStandardFile = modelId + ".txt";
		HashMap<String,String> goldStandard = new HashMap<String, String>();
		HashMap<String,String> computedSolution = new HashMap<String, String>();
		String activity;
		String sentence;
		String gsActivity;
		String gsSentence;
		
		int local_matches = 0;
		int local_totalComputed = 0;
		int local_totalGold = 0;
		
		// Load computed results
		BufferedReader bf = new BufferedReader(new FileReader(MarkovConfig.PATH_MARKOV_OUTPUT+file));
		String line;
		String[] lineSplit;
		while ((line = bf.readLine()) != null) {
			activity = line.substring(5, line.indexOf("\","));
			sentence = line.substring(line.indexOf(",")+3, line.length()-2);
			computedSolution.put(activity, sentence);
			local_totalComputed++;
		}
		
		// Load gold standard
		bf = new BufferedReader(new FileReader("Gold Standard/"+goldStandardFile));
		while ((line = bf.readLine()) != null) {

			gsActivity = line.substring(4, line.indexOf("\","));
			gsSentence = line.substring(line.indexOf(",")+3, line.length()-2);
			local_totalGold++;

			// Compare results
			String computedSentence = computedSolution.get(gsActivity);
			if (computedSentence != null) {
				if (computedSentence.equals(gsSentence)) local_matches++;
			}
		}	
		double local_prec = local_matches*1.0 / local_totalComputed;
		double local_rec = local_matches*1.0 / local_totalGold;
		double local_f1 = 2* (local_rec*local_prec) / (local_prec+local_rec);
		System.out.println(modelId + "\tRecall " + ": " + df.format(local_rec) + "\tPrecision: " +  df.format(local_prec) + "\tF-Measure: " +  df.format(local_f1));
		matches += local_matches;
		totalComputed += local_totalComputed;
		totalGold += local_totalGold;
	}
	
	private static boolean isCorrectMatch(String computedCode, String goldStandardCode, int level) {
		if (computedCode == null || goldStandardCode == null) {
			return false;
		}
		String[] computedCodeSplit = computedCode.split("\\.");
		String[] goldStandardSplit = goldStandardCode.split("\\.");
		
		if (computedCodeSplit.length > level && goldStandardSplit.length > level) {
			return (computedCodeSplit[level].equals(goldStandardSplit[level]));
		} else {
			return false;
		}
	}
}
