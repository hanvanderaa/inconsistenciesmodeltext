package textmatching.matching.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.processmodel.Activity;
import textmatching.termdictionary.TermDictionary;
import textmatching.textualdescription.Sentence;



public class Scorer {

	private MatchingConfig config;
	private TermSimilarityScorer termScorer;
	private TermDictionary dictionary;
	
	public Scorer(MatchingConfig config, ModelTextPair mtp) {
		this.config = config;
		termScorer = loadTermScorer(mtp);
		
		dictionary = createDictionary(mtp);
		
	}
	
	
	public double getSimilarity(Activity activity, Sentence sentence) {
		double mainSim = getSimilarity(activity.getLemmas(), sentence.getActiveLemmas());
		return mainSim;
	}
	
	
	private double getSimilarity(List<String> lemmas1, List<String> lemmas2) {
		if (config.sentenceSimilarity == MatchingConfig.BAG_OF_WORDS) {
			return bagOfWords(lemmas1, lemmas2);
		}
		if (config.sentenceSimilarity == MatchingConfig.MIHALCEA_SIM ||
				config.sentenceSimilarity == MatchingConfig.MIHALCEA_REMOVAL) {
			return mihalceaSimilarity(lemmas1, lemmas2);
		}
		if (config.sentenceSimilarity == MatchingConfig.BASE_LINE) {
			return cosineSimilarity(lemmas1, lemmas2);
		}
		return 0.0;
	}
	
	private double bagOfWords(List<String> lemmas1, List<String> lemmas2) {
		double sim = 0.0;
		// Copy lists
    	ArrayList<String> lemmas1Copy = new ArrayList<String>(lemmas1);
		ArrayList<String> lemmas2Copy = new ArrayList<String>(lemmas2);
				
		// Determine best string match constellation
		while (lemmas1Copy.size() > 0 && lemmas2Copy.size() > 0) {
			sim = sim + computeBestMatch(lemmas1Copy, lemmas2Copy);
		}
		
//		double score = (sim / Math.max(lemmas1.size(), lemmas2.size()));
		return (sim / Math.max(lemmas1.size(), lemmas2.size()));
	}
	
	
	private double computeBestMatch(ArrayList<String> comps1Copy,ArrayList<String> comps2Copy) {
		int max_i = 0;
		int max_j = 0;
		double max_sim = 0;
		double sim = 0;
		for (int i = 0; i<comps1Copy.size();i++) {
			String best = findBestMatch(comps1Copy.get(i), comps2Copy);
			sim = termScorer.getSimilarity(comps1Copy.get(i), best);
			if (sim > max_sim) {
				max_sim = sim;
				max_i = i;
				max_j = comps2Copy.indexOf(best);
			}
		}
		comps1Copy.remove(max_i);
		if (max_j != -1) {
			comps2Copy.remove(max_j);
		}
		return max_sim;
	}
	
	private String findBestMatch(String lemma1, List<String> lemmas2) {
		double max_sim = 0;
		double sim = 0;
		String best = "";
		for (String lemma2 : lemmas2) {
			sim = termScorer.getSimilarity(lemma1, lemma2);
			if (sim > max_sim) {
				max_sim = sim;
				best = lemma2;
			}
		}
		return best;
	}
	
	private double mihalceaSimilarity(List<String> lemmas1, List<String> lemmas2) {
		List<String> lemmas1copy = new ArrayList<String>(lemmas1);
		List<String> lemmas2copy = new ArrayList<String>(lemmas2);
		double leftnum = 0.0;
		double leftdenom = 0.0;
		for (String lemma1 : lemmas1) {
			if (!lemmas2copy.isEmpty()) {
				String best2 = findBestMatch(lemma1, lemmas2copy);
				if (config.sentenceSimilarity == MatchingConfig.MIHALCEA_REMOVAL) {
					lemmas2copy.remove(best2);
				}
				double idf = dictionary.getIDF(lemma1);
				leftnum += termScorer.getSimilarity(lemma1, best2) * idf;
				leftdenom += idf;
			}
		}
		double rightnum = 0.0;
		double rightdenom = 0.0;
		for (String lemma2 : lemmas2) {
			if (!lemmas1copy.isEmpty()) {
				String best1 = findBestMatch(lemma2, lemmas1copy);
				if (config.sentenceSimilarity == MatchingConfig.MIHALCEA_REMOVAL) {
					lemmas1copy.remove(best1);
				}
				double idf = dictionary.getIDF(lemma2);
				rightnum += termScorer.getSimilarity(lemma2, best1) * idf;
				rightdenom += idf;
			}
		}
		return 0.5 * ((leftnum / leftdenom) + (rightnum / rightdenom));
	}
	
	private TermDictionary createDictionary(ModelTextPair mtp) {
		return new TermDictionary(mtp.getAnalyzedModel(config),
				mtp.getProcessedDescription(config));
	}
	
	private TermSimilarityScorer loadTermScorer(ModelTextPair mtp) {
	if (config.termSimilarity == MatchingConfig.BASE_LINE) {
		return new TFIDFScorer();
	}
	if (config.termSimilarity == MatchingConfig.MODE_LIN) {
		return new LinScorer(mtp.getName());
	}
	if (config.termSimilarity == MatchingConfig.MODE_DISCO) {
		return new DiscoScorer(mtp.getName());
	}
	return null;
	}
	

	
	
	private double cosineSimilarity(List<String> lemmas1, List<String> lemmas2) {
		Set<String> uniqueLemmas1 = new HashSet<String>(lemmas1);
		Set<String> uniqueLemmas2 = new HashSet<String>(lemmas2);
		double num = 0.0;
		double denomA = 0.0;
		double denomB = 0.0;
		for (String lemma : lemmas1) {
			double idf = dictionary.getIDF(lemma);
			double tfidf1 = count(lemmas1, lemma) * idf;
			double tfidf2 = count(lemmas2, lemma) * idf;
			denomA += tfidf1 * tfidf1;
			num +=  tfidf1 * tfidf2;
		}
		for (String lemma : lemmas2) {
			double idf = dictionary.getIDF(lemma);
			double tfidf2 = count(lemmas2, lemma) * idf;
			denomB += tfidf2 * tfidf2;
		}
		return num / (Math.sqrt(denomA) * Math.sqrt(denomB));
	}
	
	private int count(List<String> list, String lemma) {
		return Collections.frequency(list, lemma);
	}
	
}
