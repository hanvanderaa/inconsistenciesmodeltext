package textmatching.matching.similarity;

import textmatching.matching.config.MatchingConfig;
import textmatching.util.CleanupUtils;

public class DiscoScorer implements TermSimilarityScorer{

	private SimilarityManager similarityManager;
	
	public DiscoScorer(String name) {
		similarityManager = new SimilarityManager(name, MatchingConfig.MODE_DISCO);
	}
	
	
	public double getSimilarity(String w1, String w2) {
		return similarityManager.getRelatedness(CleanupUtils.removeCharacters(w1), CleanupUtils.removeCharacters(w2));
	}
	
//	public double getSimilarity(List<String> lemmas1, List<String> lemmas2) {
//		return getBagOfWordsSimilarity(lemmas1, lemmas2);
//	}
	
//	private double getBagOfWordsSimilarity(List<String> lemmas1, List<String> lemmas2) {
//		double sim = 0.0;
//		// Copy lists
//    	ArrayList<String> lemmas1Copy = new ArrayList<String>(lemmas1);
//		ArrayList<String> lemmas2Copy = new ArrayList<String>(lemmas2);
//				
//		// Determine best string match constellation
//		while (lemmas1Copy.size() > 0 && lemmas2Copy.size() > 0) {
//			sim = sim + computeBestMatch(lemmas1Copy, lemmas2Copy);
//		}
//		return (sim / Math.max(lemmas1.size(), lemmas2.size()));
//	}
//	
//	private double computeBestMatch(ArrayList<String> bag1Copy, ArrayList<String> bag2Copy) {
//		int max_i = 0;
//		int max_j = 0;
//		double max_sim = 0;
//		double sim = 0;
//		for (int i = 0; i<bag1Copy.size();i++) {
//			for (int j = 0; j<bag2Copy.size();j++) {
//				String c1 = bag1Copy.get(i).toLowerCase();
//				String c2 = bag2Copy.get(j).toLowerCase();
//				sim = 0;
//				if (!c1.equals("") && !c2.equals("")) {
//					sim = similarityManager.getRelatedness(c1, c2);
//				} 
//				if (sim > max_sim) {
//					max_sim = sim;
//					max_i = i;
//					max_j = j;
//				}
//			}
//		}
//		bag1Copy.remove(max_i);
//		bag2Copy.remove(max_j);
//		return max_sim;
//	}
		
}
