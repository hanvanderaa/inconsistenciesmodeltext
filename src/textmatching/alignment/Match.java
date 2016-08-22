package textmatching.alignment;

import java.io.Serializable;

import textmatching.processmodel.Activity;
import textmatching.textualdescription.Sentence;

public class Match implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -22831715337996290L;
	private Activity activity;
	private Sentence sentence;
	private Double score;
	
	public Match(Activity activity, Sentence sentence, Double score)  {
		this.activity = activity;
		this.sentence = sentence;
		this.score = score;
	}
	
	public Match(Activity activity, Sentence sentence) {
		this(activity, sentence,  null);
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public int getSentenceId() {
		return sentence.getId();
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public Double getScore() {
		return score;
	}
	
	public String toString() {
		return activity + " - " + sentence + " score: " + score;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activity == null) ? 0 : activity.hashCode());
		result = prime * result
				+ ((sentence == null) ? 0 : sentence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Match other = (Match) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		return true;
	}

	

	
	
	
}
