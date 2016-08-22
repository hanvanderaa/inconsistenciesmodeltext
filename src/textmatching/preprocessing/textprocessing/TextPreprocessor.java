package textmatching.preprocessing.textprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import textmatching.matching.config.MatchingConfig;
import textmatching.preprocessing.Preprocessor;
import textmatching.preprocessing.textprocessing.structureextraction.StructureExtractor;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;
import textmatching.util.DepUtils;
import textmatching.util.PrintUtils;
import textmatching.util.TextUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;



public class TextPreprocessor extends Preprocessor {
	MatchingConfig config;
	
	public TextPreprocessor(Preprocessor preprocessor) {
		super(preprocessor);
	}
	
	
	
	public TextPreprocessor(Preprocessor preprocessor, MatchingConfig config) {
		super(preprocessor);
		this.config = config;
	}
	
	public ProcessDescription readDescription(String processText) {
		ProcessDescription description = new ProcessDescription();
		Annotation annotation = new Annotation(processText);
		pipeline.annotate(annotation);
		
		List<CoreMap> sentenceMaps = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		
		if (sentenceMaps != null) {
			int id = 0;
			for (CoreMap coreMap : sentenceMaps) {
				Tree tree = coreMap.get(TreeCoreAnnotations.TreeAnnotation.class);
				Sentence sentence = new Sentence(id, PrintUtils.treeToString(tree),
						coreMap, tree);
				description.addSentence(sentence);
				id++;
			}
		}		
		return description;
	}

	public ProcessDescription preprocessDescription(ProcessDescription originalDescription) {
		ProcessDescription processedDescription = lemmatizeDescription(originalDescription);
		if (config.preprocessorMode == MatchingConfig.ANAPHORA_RESOLUTION || config.preprocessorMode == MatchingConfig.FULL_PREPROCESSING) {
			resolveAnaphoras(processedDescription);
		}
		if (config.preprocessorMode == MatchingConfig.CLAUSE_ANALYSIS || config.preprocessorMode == MatchingConfig.FULL_PREPROCESSING) {
			analyzeClauses(processedDescription);
		}
		// extract structure
		StructureExtractor extractor = new StructureExtractor(config.matchingStrategy);
		extractor.extractStructure(processedDescription);
		
		return processedDescription;
	}
	
	



	public ProcessDescription lemmatizeDescription(ProcessDescription originalDescription) {
		ProcessDescription description = new ProcessDescription();
		for (Sentence originalSentence : originalDescription.getSentences()) {
			Sentence newSentence = parseSentence(originalSentence);
			description.addSentence(newSentence);
		}
		return description;
	}
	
	private Sentence parseSentence(Sentence original) {
		Sentence newSentence = new Sentence(original);
		if (!(config.preprocessorMode == MatchingConfig.BASE_LINE)) {
			newSentence.setLemmas(lemmatizeSentence(original));
		} else {
			newSentence.setLemmas(splitSentenceToWords(original));
		}
		return newSentence;
	}
	
	
	private void resolveAnaphoras(ProcessDescription processDescription) {
		resolveAnaphoras(processDescription.getSentence(0), null);
		for (int i = 1; i < processDescription.getSentenceCount(); i++) {
			Sentence current = processDescription.getSentence(i);
			Sentence previous = processDescription.getSentence(i - 1);
			resolveAnaphoras(current, previous);
		}
	}
	
	private void resolveAnaphoras(Sentence current, Sentence previous) {
		Set<String> objects = extractObjects(current, current.getCoreMap().get(CollapsedDependenciesAnnotation.class));
		if (previous != null && containsOnlyPronouns(objects)) {
			objects = previous.getObjects();
		}
		current.addObjects(objects);
	}
	
	
	private List<String> lemmatizeSentence(Sentence sentence) {
		return lemmatize(sentence.getOriginal());
	}

	private List<String> splitSentenceToWords(Sentence sentence) {
		Annotation annotation = new Annotation(sentence.getOriginal());
		pipeline.annotate(annotation);
		List<String> words = new ArrayList<String>();
		for (CoreLabel token : annotation.get(TokensAnnotation.class)) {
			if (!isSpecialCharacter(token.lemma())) {
				words.add(token.value());
			}
		}
		return words;
	}

	

	private Set<String> extractObjects(Sentence sentence, SemanticGraph dependencyGraph) {
		Set<String> objects = new HashSet<String>();
		for (SemanticGraphEdge dependency : dependencyGraph.edgeListSorted()) {
			String shortname = dependency.getRelation().getShortName();
			if (shortname.equals("dobj") || shortname.equals("nsubjpass") || shortname.equals("nsubj")) {
				String object = extractNoun(sentence, dependency.getDependent());
				objects.add(object);
			}
		}
		return objects;
	}
	
	private Boolean containsOnlyPronouns(Collection<String> lemmas) {
		for (String lemma : lemmas) {
			if (!TextUtils.isPronoun(lemma)) {
				return false;
			}
		}
		return true;
	}
	
	
	private void analyzeClauses(ProcessDescription processedDescription) {
		for (Sentence sentence : processedDescription.getSentences()) {
			if (hasCondition(sentence)) {
				Tree remaining = removeConditionalPhrase(sentence.getTree());
				List<String> remainingLemmas = lemmasFromTree(remaining);
				if (!remainingLemmas.isEmpty()) {
					sentence.setActiveClause(remainingLemmas);
				}
			}
		}
	}

	private List<String> lemmasFromTree(Tree tree) {
		List<String> lemmas = new ArrayList<String>();
		
		for (Tree t : tree.getLeaves()) {
//			Label label = t.label();
			CoreLabel token = (CoreLabel) t.label();
			if (!TextUtils.isClosedClass(token.lemma()) && !isSpecialCharacter(token.lemma())) {
				lemmas.add(lemmatizeSingleTerm(token.lemma()));
			}
		}
		return lemmas;
		
	}
	
	private Boolean hasCondition(Sentence sentence) {
		for (Tree t : sentence.getTree()) {
			if (t.value().equals("SBAR") && 
					(t.firstChild().firstChild().value().toLowerCase().equals("if") 
//							|| t.firstChild().firstChild().value().toLowerCase().equals("once"))
			)){
					return true;
				}
		}
		return false;
	}
	
	private Tree removeConditionalPhrase(Tree tree) {
		List<Tree> toRemove = new ArrayList<Tree>();
		for (Tree subtree : tree) {
			if (subtree.value().equals("SBAR") && 
				(subtree.firstChild().firstChild().value().toLowerCase().equals("if"))) {
//				 subtree.firstChild().firstChild().value().toLowerCase().equals("once"))) {
				toRemove.add(subtree);
			}
		}
		for (Tree subtree : toRemove) {
			tree = tree.prune(new TreeFilterer(subtree));
		}
		return tree;
	}
	
	private String extractNoun(Sentence sentence, IndexedWord nounBase) {
		String noun = nounBase.lemma();

		// add determiner
		if (DepUtils.getDependent(sentence, "det", nounBase) != null) {
			noun = noun + " " + DepUtils.getDependent(sentence, "det", nounBase).lemma();
		}
		
		
		// add possessive stuff
		if (DepUtils.hasDependencyWithGov(sentence, "nmod:poss", nounBase)) {
			IndexedWord possessor = DepUtils.getDependent(sentence, "nmod:poss", nounBase);
			noun = noun + " " + extractNoun(sentence, possessor);
		}
		
		// add compound(s)
		if (DepUtils.getDependent(sentence, "compound", nounBase) != null) {
			noun += DepUtils.getDependent(sentence, "compound", nounBase).lemma();
		}

		// check for "of" nouns, e.g. percent of orders
		if (DepUtils.hasDependencyWithGovLong(sentence, "nmod", nounBase)) {
			noun = noun + " " + extractNoun(sentence, DepUtils.getDependent(sentence, "nmod", nounBase));
		}
		
		return noun;
	}
}
