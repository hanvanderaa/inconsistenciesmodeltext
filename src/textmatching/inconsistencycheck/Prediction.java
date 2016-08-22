package textmatching.inconsistencycheck;

import textmatching.modeltextpair.ModelTextPair;


public class Prediction {

	public Object o;
	public double score;
	public boolean missing;
	public boolean wrongOrder;
	public ModelTextPair parent;
	
	public Prediction(Object o, double score, boolean missing) {
		this(o, score, missing, false);
	}
	
	public Prediction(Object o, double score, boolean missing, boolean wrongOrder) {
		this.o = o;
		this.score = score;
		this.missing = missing;
		this.wrongOrder = wrongOrder;
	}
	
	public Object getObject() {
		return o;
	}
	
	public void setParent(ModelTextPair mtp) {
		this.parent = mtp;
	}
	
	public ModelTextPair getParent() {
		return parent;
	}
	
	public boolean isMissing() {
		return missing;
	}
	
	public boolean isWrongOrder() {
		return wrongOrder;
	}
	
	public double getScore() {
		return score;
	}
	
	public String toString() {
		return o + " score: " + score + " missing: " + missing + " wrongorder: " + wrongOrder;
	}
	
}
