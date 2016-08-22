package textmatching.util;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class TextUtils {

	public static String[] PREPOSITIONS = 
			new String[]{"on", "in", "at", "since", "for", "ago", "to", "by", "under", "from"};

	public static String[] CONJUNCTIONS = 
			new String[]{"for", "and", "nor", "but", "or", "yet", "so"};

	public static String[] DETERMINERS = 
			new String[]{"the", "a", "an", "'s"};

	public static String[] PRONOUNS = 
			new String[]{"she", "her", "he", "him", "it", "they", "them", "this", "these", "that", "those"};

	
	public static String[] CONDITIONAL_INDICATORS = 
			new String[]{"if", "whether", "in case of", "in the case of", "in case",
				"for the case", "whereas", "otherwise", "optionally", "else"};
	
	public static String[] PARALLEL_INDICATORS = 
			new String[]{"while", "meanwhile", "in parallel", "concurrently",
				"meantime", "in the meantime", "also"};
	
	public static String[] SEQUENCE_INDICATORS = 
			new String[]{"then", "after", "afterward", "afterwards", "subsequently",
				"based on this", "thus", "finally"};
	
	public static String[] LOOP_INDICATORS = 
			new String[]{"next", "back", "again"};
	
	public static String[] TRIGGER_INDICATORS = 
			new String[]{"once", "when", "whenever"};
	


	public static Boolean isPreposition(String s) {
		return arrayContains(s, PREPOSITIONS);
	}
	
	public static Boolean isConjunction(String s) {
		return arrayContains(s, CONJUNCTIONS);
	}
	
	public static Boolean isDeterminer(String s) {
		return arrayContains(s, CONJUNCTIONS);
	}
	
	public static Boolean isClosedClass(String s) {
		return (isPreposition(s) || isConjunction(s) || isDeterminer(s));
	}
	
	public static Boolean isPronoun(String s) {
		return arrayContains(s, PRONOUNS);
	}
	
	public static String[] FLOW_INDICATORS() {
		String[] result = ArrayUtils.addAll(CONDITIONAL_INDICATORS, PARALLEL_INDICATORS);
		result = ArrayUtils.addAll(result, SEQUENCE_INDICATORS);
		result = ArrayUtils.addAll(result, LOOP_INDICATORS);
		return result;
	}
	
	public static Boolean isParallelIndicator(String s) {
		return arrayContains(s, PARALLEL_INDICATORS);
	}
	
	public static Boolean isConditional(String s) {
		return arrayContains(s, CONDITIONAL_INDICATORS);
	}
	
	private static boolean arrayContains(String s, String[] array) {
		return Arrays.asList(array).contains(s);
	}
	
	public static Boolean isSequential(String s) {
		return arrayContains(s, SEQUENCE_INDICATORS);
	}
	
	public static Boolean isTrigger(String s) {
		return arrayContains(s, TRIGGER_INDICATORS);
	}
}
