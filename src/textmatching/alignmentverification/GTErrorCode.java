package textmatching.alignmentverification;

public enum GTErrorCode {

	PROPERMATCH(1, "proper match"),
	NOTFOUND(2, "activity missing"),
	WRONGORDER(3, "wrong order");
	
	public final int code;
	public final String description;
	
	private GTErrorCode(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public String toString() {
		return this.description;
	}
}
