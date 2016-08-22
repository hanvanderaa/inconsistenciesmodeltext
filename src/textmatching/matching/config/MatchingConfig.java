package textmatching.matching.config;

import java.io.Serializable;

public class MatchingConfig implements Serializable {


	private static final long serialVersionUID = -5979602179656446113L;
	public static final String DISCO_CACHE_PATH = "cache/disco/";
	public static final String LIN_CACHE_PATH = "cache/lin/";
	public static final String DISCO_PATH =  "/Users/han/vusvn/model2textalignment/data/enwiki-20130403-sim-lemma-mwl-lc";
	
	
	public final static int BASE_LINE = -1;
	public final static int MODE_DISCO = 1;
	public final static int MODE_LIN = 2;
	
	public final static int BAG_OF_WORDS = 1;
	public final static int MIHALCEA_SIM = 2;
	public final static int MIHALCEA_REMOVAL = 3;
	
	public final static int NAIVE_MATCHER = 1;
	public final static int STRICT_ASSUMPTION = 2;
	public final static int STRUCTURE_EXTRACTION = 3;
	public final static int STRUCTURE_EXTRACTION_ASSUMED = 4;
	public final static int BFSEARCHER = 5;
	
	public final static int NO_PREPROCESSING = 0;
	public final static int ANAPHORA_RESOLUTION = 1;
	public final static int ACTION_ANALYSIS = 2;
	public final static int CLAUSE_ANALYSIS = 4;
	public final static int FULL_PREPROCESSING = 7;
	
	public int termSimilarity;
	public int sentenceSimilarity;
	public int matchingStrategy;
	public int preprocessorMode;
	public boolean reserialize;
	
	public MatchingConfig(int preprocessorMode, int termSimilarity, 
			int sentenceSimilarity,  int matchingStrategy, boolean reserialize) {
		this.termSimilarity = termSimilarity;
		this.sentenceSimilarity = sentenceSimilarity;
		this.matchingStrategy = matchingStrategy;
		this.preprocessorMode = preprocessorMode;
		this.reserialize = reserialize;
	}
	
	
	public String toString() {
		return "preprocessing: " + preprocessorToString() + 
				" sim. measure: " + similarityModeToString() + 
				" strategy: " + strategyToString();
	}
	
	
	public String preprocessorToString() {
		if (preprocessorMode == NO_PREPROCESSING) {
			return "None";
		}
		if (preprocessorMode == ANAPHORA_RESOLUTION) {
			return "Anaphoras";
		}
		if (preprocessorMode == ACTION_ANALYSIS) {
			return "Action analysis";
		}
		if (preprocessorMode == CLAUSE_ANALYSIS) {
			return "Clause analysis";
		}
		if (preprocessorMode == FULL_PREPROCESSING) {
			return "Full";
		}
		return "Unknown preprocessing mode";
	}
	
	public String similarityModeToString() {
		String term = "";
		if (termSimilarity == MODE_DISCO) {
			term =  "Disco";
		}
		if (termSimilarity == MODE_LIN) {
			term = "Lin";
		}
		if (termSimilarity == BASE_LINE) {
			term = "tf-idf";
		}
		String sentence = "";
		if (sentenceSimilarity == BASE_LINE) {
			sentence = "cosine";
		}
		if (sentenceSimilarity == BAG_OF_WORDS) {
			sentence = "bag-of-words";
		}
		if (sentenceSimilarity == MIHALCEA_SIM) {
			sentence = "Mihalcea";
		}
		if (sentenceSimilarity == MIHALCEA_REMOVAL) {
			sentence = "Mihalcea removal";
		}
		return term + " - " + sentence;
	}
	
	public String strategyToString() {
		if (matchingStrategy == NAIVE_MATCHER) {
			return "naive";
		}
		if (matchingStrategy == STRICT_ASSUMPTION) {
			return "strict assumption";
		}
		if (matchingStrategy == STRUCTURE_EXTRACTION) {
			return "full structure extraction";
		}
		if (matchingStrategy == STRUCTURE_EXTRACTION_ASSUMED) {
			return "structure extraction with assumption";
		}
		if (matchingStrategy == BFSEARCHER) {
			return "bfsearcher";
		}
		return "unknown matching strategy";
	}
	
	public String strategyToShortString() {
		if (matchingStrategy == NAIVE_MATCHER) {
			return "naive";
		}
		if (matchingStrategy == STRICT_ASSUMPTION) {
			return "strict";
		}
		if (matchingStrategy == STRUCTURE_EXTRACTION) {
			return "fullextract";
		}
		if (matchingStrategy == STRUCTURE_EXTRACTION_ASSUMED) {
			return "assumedextract";
		}
		return "unknown matching strategy";
	}
	
	public String folderName() {
		return (preprocessorToString() + similarityModeToString() + +sentenceSimilarity + strategyToShortString()).replaceAll("\\s","") + "/";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + matchingStrategy;
		result = prime * result + preprocessorMode;
		result = prime * result + (reserialize ? 1231 : 1237);
		result = prime * result + sentenceSimilarity;
		result = prime * result + termSimilarity;
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
		MatchingConfig other = (MatchingConfig) obj;
		if (matchingStrategy != other.matchingStrategy)
			return false;
		if (preprocessorMode != other.preprocessorMode)
			return false;
		if (reserialize != other.reserialize)
			return false;
		if (sentenceSimilarity != other.sentenceSimilarity)
			return false;
		if (termSimilarity != other.termSimilarity)
			return false;
		return true;
	}


	
	
	
}
