package textmatching.preprocessing.textprocessing.structureextraction;

import java.util.ArrayList;
import java.util.List;

import textmatching.textualdescription.Sentence;

public class DiscourseBlock {

	
	
//	private String type;
	private DiscourseBlock predecessor;
	private DiscourseBlock successor;
	private List<Sentence> containedSentences;
	
	public DiscourseBlock() {
//		this.type = type;
		this.containedSentences = new ArrayList<Sentence>();
	}
	
	public void add(Sentence sentence) {
		containedSentences.add(sentence);
	}
	
	public void remove(Sentence sentence) {
		containedSentences.remove(sentence);
	}
	
	public void setPredecessor(DiscourseBlock pred) {
		predecessor = pred;
	}
	
	public void setSuccessor(DiscourseBlock succ) {
		successor = succ;
	}

	public DiscourseBlock getPredecessor() {
		return predecessor;
	}

	public DiscourseBlock getSuccessor() {
		return successor;
	}

	public List<Sentence> getSentences() {
		return containedSentences;
	}
	
	
	public Boolean isEmpty() {
		return containedSentences.isEmpty();
	}
	
	public String toString() {
		return containedSentences.toString();
	}
	

}
