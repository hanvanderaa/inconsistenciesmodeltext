package textmatching.matching.similarity;

public class TFIDFScorer implements TermSimilarityScorer {

	public double getSimilarity(String w1, String w2) {
		if (w1.equals(w2)) {
			return 1.0;
		} else {
			return 0.0;
		}
	}
	
	
}
