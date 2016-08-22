package textmatching.matching.similarity;

import java.util.List;

public interface TermSimilarityScorer {


	
//	public double getSimilarity(List<String> lemmas1, List<String> lemmas2);

	
	public double getSimilarity(String w1, String w2);
	
}
