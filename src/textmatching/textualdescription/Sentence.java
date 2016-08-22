package textmatching.textualdescription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import textmatching.constructs.Document;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class Sentence extends Document implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3324656631418357509L;
	Tree tree;
	CoreMap coreMap;
	Set<String> objects;
	List<String> activeClause;

	public Sentence(int id, String original, CoreMap coreMap, Tree tree)  {
		super(id, original);
		this.tree = tree;
		this.coreMap = coreMap;
		this.objects = new HashSet<String>();
	}
	
	public Sentence(int id, String original) {
		this(id, original, null, null);
	}
	
	public Sentence(Sentence original) {
		this(original.getId(), original.getLabel(), original.getCoreMap(), original.getTree());
	}
	
	public Tree getTree() {
		return tree;
	}
	
	public CoreMap getCoreMap() {
		return coreMap;
	}
	
	public Set<String> getObjects() {
		return objects;
	}
	
	public void addObject(String object) {
		objects.add(object);
	}
	
	public void addObjects(Set<String> newObjects) {
		objects.addAll(newObjects);
	}

	public List<String> getActiveLemmas() {
		Set<String> active = new HashSet<String>(lemmas);
		if (activeClause != null) {
			active = new HashSet<String>(activeClause);
		}
		active.addAll(objects);
		return new ArrayList<String>(active);
	}
	

	
	public void setActiveClause(List<String> activeLemmas) {
		this.activeClause = activeLemmas;
	}

	public Collection<TypedDependency> getDependencies() {
		return coreMap.get(CollapsedDependenciesAnnotation.class).typedDependencies();
	}

}
