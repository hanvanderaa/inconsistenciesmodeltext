package textmatching.preprocessing.textprocessing;

import java.util.function.Predicate;

import edu.stanford.nlp.trees.Tree;

public class TreeFilterer implements  Predicate<Tree> {
	/**
	 * 
	 */

	private Tree cutOffPoint;
	
	public TreeFilterer(Tree cutOffPoint) {
		this.cutOffPoint = cutOffPoint;
	}
	

	public boolean accept(Tree t) {
		return !cutOffPoint.equals(t);
	}


	public boolean test(Tree t) {
		return !cutOffPoint.equals(t);
	}

	
}
