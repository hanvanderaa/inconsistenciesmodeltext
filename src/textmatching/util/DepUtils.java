package textmatching.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import textmatching.textualdescription.Sentence;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.TypedDependency;

public class DepUtils {

	
	public static Boolean hasDependency(Sentence sentence, String shortname) {
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname)) {
				return true;
			}
		}
		return false;
	}
	

	public static Boolean hasDependencyWithDep(Sentence sentence, String shortname, IndexedWord dependent) {
		
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname) && dependency.dep().equals(dependent)) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean hasDependencyWithGov(Sentence sentence, String shortname, IndexedWord governor) {
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname) && dependency.gov().equals(governor)) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean hasDependencyWithGov(Sentence sentence, String shortname, IndexedWord governor, String tag) {
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname) && dependency.gov().equals(governor) && dependency.dep().tag().equals(tag)) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean hasDependencyWithGovLong(Sentence sentence, String longname, IndexedWord governor) {
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRelLong(dependency, longname) && dependency.gov().equals(governor)) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean hasDependency(Sentence sentence, String shortname, IndexedWord governor, IndexedWord dependent ) {
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname) &&
					dependency.dep().equals(dependent) && dependency.gov().equals(governor)) {
				return true;
			}
		}
		return false;
	}
	
	public static IndexedWord getGovernor(Sentence sentence, String shortname, IndexedWord dependent) {
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname) && dependency.dep().equals(dependent)) {
				return dependency.gov();
			}
		}
		return null;
	}
	
	public static IndexedWord getDependent(Sentence sentence, String shortname, IndexedWord governor) {
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname) && dependency.gov().equals(governor)) {
				return dependency.dep();
			}
		}
		return null;
	}
	
	public static List<IndexedWord> getDependents(Sentence sentence, String shortname, IndexedWord governor) {
		List<IndexedWord> result = new ArrayList<IndexedWord>();
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (isRel(dependency, shortname) && dependency.gov().equals(governor)) {
				result.add(dependency.dep());
			}
		}
		return result;
	}
	

	
	public static Boolean isRel(SemanticGraphEdge dependency, String shortname) {
		return dependency.getRelation().getShortName().equals(shortname);
	}
	
	public static Boolean isRel(TypedDependency dependency, String shortname) {
		return dependency.reln().getShortName().equals(shortname);
	}
	
	public static Boolean isRelLong(SemanticGraphEdge dependency, String longname) {
		return dependency.getRelation().getLongName().equals(longname);
	}
	
	public static Boolean isRelLong(TypedDependency dependency, String longname) {
		return dependency.reln().getShortName().equals(longname);
	}
	
	public static String shortName(SemanticGraphEdge dependency) {
		return dependency.getRelation().getShortName();
	}
	
	public static String shortName(TypedDependency dependency) {
		return dependency.reln().getShortName();
	}
	
	public static List<IndexedWord> getVerbs(Sentence sentence) {
		
		//TODO: for now this is the most elegant I could come up with, i.e. not at all
		// perhaps add a comprehensive list and a single parse over the terms.
		Set<IndexedWord> verbs = new HashSet<IndexedWord>();
		
		for (TypedDependency dependency : sentence.getDependencies()) {
			if (dependency.dep().tag().startsWith("VB")) {
				verbs.add(dependency.dep());
			}
			if (!isRel(dependency, "root") && dependency.gov().tag().startsWith("VB")) {
				verbs.add(dependency.gov());
			}
		}
		
		List<IndexedWord> sorted = new ArrayList<IndexedWord>(verbs);
		Collections.sort(sorted);
		
		return sorted;
	}
	
	
}
