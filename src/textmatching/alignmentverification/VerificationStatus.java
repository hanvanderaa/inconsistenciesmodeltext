package textmatching.alignmentverification;

public enum VerificationStatus {

	
	CORRECTMATCH (0, "correct match"), // alignment and gt same sentence
	CORRECTNONMATCH (1, "correct non match"), // alignment and gt both no sentence
	MISSEDMATCH (2, "incorrect non match"),// alignment not matched, gt has match
	WRONGSENTENCE (3, "wrong sentence match"), // alignment and gt different sentences
	FALSEMATCH (4, "incorrect match"); // alignment has match, gt has no match
	
	public final int code;
	public final String description;
	
	private VerificationStatus(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public String toString() {
		return this.description;
	}
}
