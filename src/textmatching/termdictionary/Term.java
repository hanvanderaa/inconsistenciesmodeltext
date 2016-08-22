package textmatching.termdictionary;

import java.util.Set;

public class Term {

	private String lemma;
	private Set<String> synonyms;
	private int count;
	private int synCount;
	private double termIDF;
	private double synIDF;
	
	public Term(String lemma, Set<String> synonyms) {
		super();
		this.lemma = lemma;
		this.synonyms = synonyms;
		count = 1;
	}
	
	public String getLemma() {
		return lemma;
	}
	
	public void incrementCount() {
		count++;
	}
	
	public int getCount() {
		return count;
	}
	
	public void incrementSynCount() {
		synCount++;
	}
	
	public int getSynCount() {
		return synCount;
	}
	
	public Set<String> getSynonyms() {
		return synonyms;
	}
	
	public Boolean matchesTerm(String lemma) {
		return this.lemma.equals(lemma);
	}
	
	public Boolean isSynonymOf(String lemma) {
		return synonyms.contains(lemma);
	}

	public double getTermIDF() {
		return termIDF;
	}

	public void setTermIDF(double termIDF) {
		this.termIDF = termIDF;
	}

	public double getSynIDF() {
		return synIDF;
	}

	public void setSynIDF(double synIDF) {
		this.synIDF = synIDF;
	}
	
	
	
}
