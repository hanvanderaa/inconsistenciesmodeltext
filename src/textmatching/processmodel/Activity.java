package textmatching.processmodel;

import java.io.Serializable;

import textmatching.constructs.Document;
import textmatching.util.CleanupUtils;

public class Activity extends Document implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7152219330507891015L;

	public Activity(int id, String label) {
		super(id, CleanupUtils.cleanActivityLabel(label));
	}
	
	public String getLabel() {
		return original;
	}
	

}
