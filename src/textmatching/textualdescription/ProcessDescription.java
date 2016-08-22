package textmatching.textualdescription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import textmatching.processmodel.PartialOrdering;
import textmatching.processmodel.PartialOrderingGeneric;

public class ProcessDescription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5877199106013599423L;
	List<Sentence> sentences;
	PartialOrderingGeneric<Sentence> ordering;
	
	public ProcessDescription() {
		sentences = new ArrayList<Sentence>();
	}
	
	public void addSentence(Sentence sentence) {
		sentences.add(sentence);
	}
	
	public Sentence getSentence(int index) {
		if (index >= 0 && index < sentences.size()) {
			return sentences.get(index);
		}
		return null;
	}
	
	public List<Sentence> getSentences() {
		return sentences;
	}
	
	public int getSentenceCount() {
		return sentences.size();
	}
	
	public void addPartialOrdering(PartialOrderingGeneric<Sentence> ordering) {
		this.ordering = ordering;
	}
	

	
	public Set<Sentence> getAllPredecessors(Sentence sentence) {
		return ordering.getAllPredecessors(sentence);
	}
	
	public Sentence findSentenceOfMarkovString(String markovSentence) {
		for (Sentence sentence : sentences) {
			String cleaned = sentence.getOriginal().replaceAll(",", "");
			if (cleaned.equals(markovSentence)) {
				return sentence;
			}
		}
		return null;
	}
	
}
