package textmatching.alignmentverification;

import java.io.Serializable;

public class GTMatch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8877838438032628589L;
	private String activity;
	private String sentence;
	private int sentenceid;
	private GTErrorCode errorCode;
	
	public GTMatch(String activity, String sentence, int sentenceid) {
		this(activity, sentence, sentenceid, "");
	}
	
	public GTMatch(String activity, String sentence, int sentenceid, String errorString) {
		super();
		this.activity = activity;
		this.sentence = sentence;
		this.sentenceid = sentenceid;
		this.errorCode = determineCode(errorString);
	}
	
	
	public GTErrorCode getCode() {
		return errorCode;
	}
	
	private GTErrorCode determineCode(String errorString) {
		if (errorString.equals("m")) {
			return GTErrorCode.NOTFOUND;
		}
		if (errorString.equals("w")) {
			return GTErrorCode.WRONGORDER;
		}
		return GTErrorCode.PROPERMATCH;
	}
	

	public String getActivity() {
		return activity;
	}

	public String getSentence() {
		return sentence;
	}
	
	public int getSentenceId() {
		return sentenceid;
	}
	
	public String toString() {
		return activity + " - " + sentence;
	}
	
	
	
	
}
