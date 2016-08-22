package textmatching.matching.similarity;

import textmatching.matching.config.MatchingConfig;

public class LinScorer implements TermSimilarityScorer {
	
	private SimilarityManager similarityManager;
	
	public LinScorer(String name) {
		similarityManager = new SimilarityManager(name, MatchingConfig.MODE_LIN);
	}
	
	
	public double getSimilarity(String w1, String w2) {
		try {
			return similarityManager.getRelatedness(w1, w2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
//	public double getSimilarity(List<String> lemmas1, List<String> lemmas2) {
//		return linSimilarity(lemmas1, lemmas2); 
//	}
//	
//	private double linSimilarity(List<String> lemmas1, List<String> lemmas2) {
//		double sim = 0.0;
//		// Copy lists
//    	ArrayList<String> lemmas1Copy = new ArrayList<String>(lemmas1);
//		ArrayList<String> lemmas2Copy = new ArrayList<String>(lemmas2);
//				
//		// Determine best string match constellation
//		while (lemmas1Copy.size() > 0 && lemmas2Copy.size() > 0) {
//			sim = sim + computeBestMatch(lemmas1Copy, lemmas2Copy);
//		}
//		
//		double score = (sim / Math.max(lemmas1.size(), lemmas2.size()));
//		return (sim / Math.max(lemmas1.size(), lemmas2.size()));
//	}
//	
//	
//	private double computeBestMatch(ArrayList<String> comps1Copy,ArrayList<String> comps2Copy) {
//		int max_i = 0;
//		int max_j = 0;
//		double max_sim = 0;
//		double sim = 0;
//		for (int i = 0; i<comps1Copy.size();i++) {
//			for (int j = 0; j<comps2Copy.size();j++) {
//				String c1 = comps1Copy.get(i).toLowerCase();
//				String c2 = comps2Copy.get(j).toLowerCase();
//				sim = 0;
//				if (!c1.equals("") && !c2.equals("")) {
//					try {
//						sim = similarityManager.getRelatedness(c1, c2);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				} 
//				if (sim > max_sim) {
//					max_sim = sim;
//					max_i = i;
//					max_j = j;
//				}
//			}
//		}
//		comps1Copy.remove(max_i);
//		comps2Copy.remove(max_j);
//		return max_sim;
//	}
}
