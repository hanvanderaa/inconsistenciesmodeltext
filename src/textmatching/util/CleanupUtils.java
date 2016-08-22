package textmatching.util;

public class CleanupUtils {
	

	
	public static String cleanActivityLabel(String label) {
		String newline = System.getProperty("line.separator");
		label =label.replace(newline, " ");
		label = label.toLowerCase();
		label = label.trim();
		label = label.replaceAll("\\s+", " ");
		label = label.replaceAll(",", "");
		return label;
	}
	
	public static String cleanSentence(String sentence) {
		return sentence.replaceAll("[^A-Za-z0-9]", "");
	}
	
	
	public static String removeCharacters(String str) {
		if (str != null) {
			return str.replaceAll("[^A-Za-z0-9]", "");
		}
		return "";
	}

}
