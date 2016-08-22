package textmatching.matchers.bfsearcher;

import java.util.Comparator;

public class HypothesisComparator implements Comparator<BFHypothesis> {

	
	public int compare(BFHypothesis hyp1, BFHypothesis hyp2) {
		if (Double.compare(hyp2.getPossibleScore(), hyp1.getPossibleScore()) == 0) {
			if (Double.compare(hyp1.getActualScore(), hyp2.getActualScore()) == 0) {
				return Integer.compare(hyp2.size(), hyp1.size());
			}
			return Double.compare(hyp1.getActualScore(), hyp2.getActualScore());
		}
		return Double.compare(hyp2.getPossibleScore(), hyp1.getPossibleScore());
	}

	
	
	
	
}
