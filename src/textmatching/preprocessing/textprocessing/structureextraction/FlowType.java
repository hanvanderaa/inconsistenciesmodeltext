package textmatching.preprocessing.textprocessing.structureextraction;

public enum FlowType {

	NONE (0, "unknown"),
	SEQUENCE (1, "sequence"),
	AND (2, "AND-block"),
	XOR (3, "XOR-block");
	
	public final int code;
	public final String description;
	
	private FlowType(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public String toString() {
		return this.description;
	}
}
