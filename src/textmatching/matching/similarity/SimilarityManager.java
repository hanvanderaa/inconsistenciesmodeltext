package textmatching.matching.similarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import de.linguatools.disco.Compositionality;
import de.linguatools.disco.CorruptConfigFileException;
import de.linguatools.disco.DISCO;
import de.linguatools.disco.WrongWordspaceTypeException;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Lin;
import textmatching.matching.config.MatchingConfig;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class SimilarityManager {
	
	private String source;
	private HashMap<String, Double> similarityMap;

	// LEVENSHTEIN
	private Levenshtein ls;
	
	// LIN
	private RelatednessCalculator rc;
	
	// DISCO
	private DISCO disco;
	private Compositionality discoComp;
	
	private int mode;
	private String cache_path = null;
	

	public SimilarityManager(String source, int mode) {
		ls = new Levenshtein();
		this.source = source;
		this.mode = mode;
		similarityMap = new HashMap<String, Double>();
		
		if (mode == MatchingConfig.MODE_LIN) {
			cache_path = MatchingConfig.LIN_CACHE_PATH; 
		}
		if (mode == MatchingConfig.MODE_DISCO) {
			cache_path = MatchingConfig.DISCO_CACHE_PATH;
		}
		File cache =  new File(cache_path + source);;
		if (cache.exists()) {
			loadLinSimilarities();
		}	
		if (mode == MatchingConfig.MODE_LIN) {
			NictWordNet db = new NictWordNet();
			RelatednessCalculator[] rcs = {new Lin(db)};
			rc = rcs[0];
		}
		if (mode == MatchingConfig.MODE_DISCO) {
			try {
				disco = new DISCO(MatchingConfig.DISCO_PATH, false);
				discoComp = new Compositionality();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CorruptConfigFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public double getRelatedness(String w1, String w2) {
		if (w1.isEmpty() || w2.isEmpty()) {
			return 0;
		}
		String key1 = w1 + "-" + w2;
		String key2 = w2 + "-" + w1;
		if (similarityMap.containsKey(key1)) {
			return similarityMap.get(key1);
		}
		if (similarityMap.containsKey(key2)) {
			return similarityMap.get(key2);
		}
		double sim = 0;
		if (mode == MatchingConfig.MODE_LIN) {
			sim = rc.calcRelatednessOfWords(w1, w2);
			
			if (!w1.equals(w2) && sim > 1.0) {
				System.out.println(w1 + " - " + w2 + " sim:" + sim);
			}
			if (sim > 1.01) {
				sim = 1.0;
			}
		}
		if (mode == MatchingConfig.MODE_DISCO) {
			try {
				if (!w1.contains(" ") && !w2.contains(" ")) {
					sim = disco.secondOrderSimilarity(w1, w2);
					if (sim > 1 && sim < 1.01) {
						sim = 1;
					}
					if (sim > 1.01) {
						sim = 0.5;
					}
					
				} else {
//					sim = discoComp.compositionalSemanticSimilarity(w1, w2, Compositionality.VectorCompositionMethod.MULTIPLICATION, 
//							Compositionality.SimilarityMeasures.KOLB, disco, null, null, null, null);
					if (sim > 1 && sim < 1.01) {
						sim = 1;
					}
					if (sim > 1.01) {
						sim = 0.5;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WrongWordspaceTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sim < 0) {
				sim = ls.getSimilarity(w1, w2);
			}
		}
		
		try {
			File cache = new File(cache_path + source);
			if (cache.exists()) {
				FileWriter fw = new FileWriter(cache, true);
				fw.append(key1 + "\t" + sim + "\n");
				fw.close();
			} else {
				FileWriter fw = new FileWriter(cache);
				fw.write(key1 + "\t" + sim + "\n");
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sim;
	}

	private void loadLinSimilarities() {
		similarityMap = new HashMap<String, Double>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(cache_path + source)));
			String line = "";
			while ((line = input.readLine()) != null) {
				String[] split = line.split("\t");
				similarityMap.put(split[0], Double.valueOf(split[1]));
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
