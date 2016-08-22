package textmatching.termdictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;

public class TermDictionary {

	private Map<String, Integer> termMap;
	private int documentCount;
	
	public TermDictionary(ProcessModel model, ProcessDescription description) {
		setDocumentCount(model, description);
		populateTermMap(model, description);
	}
	
	public int getTermFrequency(String lemma) {
		return termMap.containsKey(lemma) ? termMap.get(lemma) : 0;
	}
	
	public double getIDF(String lemma) {
		int tf = getTermFrequency(lemma);
		if (tf == 0) {
			return 0;
		}
		return Math.log10((documentCount) * 1.0 / tf);
	}
	
	private void setDocumentCount(ProcessModel model, ProcessDescription description) {
		documentCount = model.getActivityCount() + description.getSentenceCount();
	}
	
	private void populateTermMap(ProcessModel model, ProcessDescription description) {
		termMap = new HashMap<String, Integer>();
		for (Activity activity : model.getActivities()) {
			addTerms(activity.getLemmas());
		}
		for (Sentence sentence : description.getSentences()) {
			addTerms(sentence.getLemmas());
		}
	}
	
	private void addTerms(List<String> lemmas) {
		if (lemmas != null) {
			for (String lemma : new HashSet<String>(lemmas)) {
				addTerm(lemma);
			}
		}
	}
	
	
	private void addTerm(String lemma) {
		int count = 0;
		if (termMap.containsKey(lemma)) {
			count = termMap.get(lemma);
		}
		count += 1;
		termMap.put(lemma, count);
	}
	
	
	
}
