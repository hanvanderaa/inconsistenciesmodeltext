package textmatching.constructs;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Document implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1303662176406809882L;
	private int id;
	protected String original;
	protected List<String> lemmas;
	
	public Document(int id, String original) {
		super();
		this.id = id;
		this.original = original;
	}
	
	
	public void setLemmas(List<String> lemmas) {
		this.lemmas = lemmas;
	}
	
	public int getId() {
		return id;
	}
	
	public String getOriginal() {
		return original;
	}
	
	public String getLabel() {
		return original;
	}
	
	public List<String> getLemmas() {
		return lemmas;
	}
		
	public Boolean containsLemma(String lemma) {
		return lemmas.contains(lemma);
	}
	
	public Boolean containsAnyLemma(Set<String> lemmas) {
		for (String lemma : lemmas) {
			if (containsLemma(lemma)) { 
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return original;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((original == null) ? 0 : original.hashCode());
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
		Document other = (Document) obj;
		if (id != other.id)
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		return true;
	}

	


	
	
	
}
