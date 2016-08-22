package textmatching.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import textmatching.util.TextUtils;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Preprocessor {

	protected StanfordCoreNLP pipeline;
	protected LexicalizedParser parser;
//	WordNetDatabase wordNetDB;
	
	
	public Preprocessor() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
//		props.put("parse.model", "edu/stanford/nlp/models/lexparser/englishFactored.ser.gz");
//		props.put("parse.maxlen", )
		pipeline = new StanfordCoreNLP(props);
//		wordNetDB = WordNetDatabase.getFileInstance();
		System.setProperty("wordnet.database.dir", "/usr/local/WordNet-3.0/dict" );
	}
	
	public Preprocessor(Preprocessor preprocessor) {
		this.pipeline = preprocessor.pipeline;
//		this.wordNetDB = preprocessor.wordNetDB;
		System.setProperty("wordnet.database.dir", "/usr/local/WordNet-3.0/dict" );
	}
	
	protected String lemmatizeSingleTerm(String lemma) {
		if (lemma == null) {
			return "";
		}
		if (!isComposite(lemma)) {
			return lemma.toLowerCase();
		}
		return lemmatizeComposite(lemma.toLowerCase());
	}
	
	private Boolean isComposite(String word) {
		if (word != null) {
			return word.contains("-");
		}
		return false;
	}
	
	private String lemmatizeComposite(String compositeTerm) {
		String[] parts = compositeTerm.split("-");
		String splitForm = "";
		String lemma = "";
		for (String part : parts) {
			splitForm += " " + part; 
		}
		Annotation annotation = new Annotation(splitForm);
		pipeline.annotate(annotation);
		for (CoreLabel token : annotation.get(TokensAnnotation.class)) {
			if (!TextUtils.isClosedClass(token.lemma()) && !isSpecialCharacter(token.lemma())) {
				lemma += token.lemma().toLowerCase();
			}
		}
		return lemma;
	}
	
	
	protected Boolean isSpecialCharacter(String lemma) {
		//TODO: add more special chars
		return Arrays.asList(new String[]{"-lrb-", "-rrb-"}).contains(lemma);
	}
	
	protected List<String> lemmatize(String original) {
		Annotation annotation = new Annotation(original);
		pipeline.annotate(annotation);
		List<String> lemmas = new ArrayList<String>();
		for (CoreLabel token : annotation.get(TokensAnnotation.class)) {
			if (!TextUtils.isClosedClass(token.lemma()) && !isSpecialCharacter(token.lemma())) {
				lemmas.add(lemmatizeSingleTerm(token.lemma()));
			}
		}
		return lemmas;
	}
	
//	public TermDictionary createTermDictionary(
//			ProcessModel ordering, ProcessDescription description) {
//		TermDictionary dictionary = new TermDictionary();
//		Set<Document> documents = new HashSet<Document>(ordering.getActivities());
//		documents.addAll(description.getSentences());
//		for (Document document : documents) {
//			for (String lemma : document.getLemmas()) {
//				//TODO: clean termDictionary methods
//				if (!dictionary.containsTerm(lemma)) {
//					Term term = new Term(lemma, getSynonyms(lemma));
//					dictionary.addTerm(term);
//				} else {
//					dictionary.getTerm(lemma).incrementCount();
//				}
//			}
//		}
//		return dictionary;
//	}
//	
//	public Set<String> getSynonyms(String lemma) {
//		Set<String> synonyms = new HashSet<String>();
//		Synset[] synsets = wordNetDB.getSynsets(lemma);
//
//		for (Synset synset : synsets) {
//			synonyms.addAll(Arrays.asList(synset.getWordForms()));
//		}
//		return synonyms;
//	}
//	
	
}
