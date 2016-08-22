package textmatching.modeltextpair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import textmatching.alignment.Alignment;
import textmatching.alignmentverification.GroundTruth;
import textmatching.alignmentverification.MTPAssertion;
import textmatching.alignmentverification.VerifiedAlignment;
import textmatching.matching.config.MatchingConfig;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;

public class ModelTextPair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6005360844854261500L;
	String name;
	ProcessModel model;
	ProcessDescription originalDescription;
	GroundTruth groundTruth;
	Map<MatchingConfig, Alignment> alignments;
	Map<MatchingConfig, VerifiedAlignment> verifications;
	Map<MatchingConfig, ProcessDescription> processedDescriptions;
	Map<MatchingConfig, ProcessModel> analyzedModels;
	MTPAssertion mtpAssertion;
	
	public ModelTextPair(String name, ProcessModel model, ProcessDescription description,
			GroundTruth groundTruth, MTPAssertion mtpAssertion) {
		super();
		this.name = name;
		this.model = model;
		this.originalDescription = description;
		this.groundTruth = groundTruth;
		this.mtpAssertion = mtpAssertion;
		analyzedModels = new HashMap<MatchingConfig, ProcessModel>();
		processedDescriptions = new HashMap<MatchingConfig, ProcessDescription>();
		alignments = new HashMap<MatchingConfig, Alignment>();
		verifications = new HashMap<MatchingConfig, VerifiedAlignment>();
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public int getActivityCount() {
		return model.getActivities().size();
	}
	
	public int getSentenceCount() {
		return originalDescription.getSentenceCount();
	}
	
	public ProcessModel getModel() {
		return model;
	}
	
	public ProcessDescription getDescription() {
		return originalDescription;
	}
	
	public ProcessDescription getProcessedDescription(MatchingConfig config) {
		return processedDescriptions.get(config);
	}
	
	public GroundTruth getGroundTruth() {
		return groundTruth;
	}

	public ProcessModel getAnalyzedModel(MatchingConfig config) {
		return analyzedModels.get(config);
	}
	
	public Alignment getAlignment(MatchingConfig config) {
		return alignments.get(config);
	}
	
	public VerifiedAlignment getVerification(MatchingConfig config) {
		return verifications.get(config);
	}
	
	public double getPrecision(MatchingConfig config) {
		return verifications.get(config).getPrecision();
	}
	
	public double getRecall(MatchingConfig config) {
		return verifications.get(config).getRecall();
	}
	
	public void addAnalyzedModel(MatchingConfig config, ProcessModel anModel) {
		analyzedModels.put(config, anModel);
	}
	
	public void addProcessedDescription(MatchingConfig config, ProcessDescription processedDescription) {
		processedDescriptions.put(config, processedDescription);
	}
	
	public void addAlignment(MatchingConfig config, Alignment alignment) {
		alignments.put(config, alignment);
	}
	
	public void addVerification(MatchingConfig config, VerifiedAlignment verification) {
		verifications.put(config, verification);
	}
	
	public List<MatchingConfig> getConfigs() {
		return new ArrayList<MatchingConfig>(alignments.keySet());
	}
	
	public ProcessDescription getHighestProcessedDescription() {
		int max = -1;
		MatchingConfig best = null;
		for (MatchingConfig config : getConfigs()) {
			if (config.preprocessorMode > max) {
				max = config.preprocessorMode;
				best = config;
			}
		}
		return processedDescriptions.get(best);
	}
	
	public MTPAssertion getMTPAssertion() {
		return mtpAssertion;
	}
	
	public boolean isMissing(Activity activity) {
		return groundTruth.isMissing(activity);
	}
	
	public boolean hasMissingActivities() {
		return groundTruth.hasMissingActivities();
	}
	
	public boolean gtHasInconsistencies() {
		return groundTruth.hasInconsistencies();
	}
	
	public boolean hasAlignment(MatchingConfig config) {
		return alignments.containsKey(config);
	}
	
	public String toString() {
		return name;
	}


	public boolean hasWrongOrder() {
		return groundTruth.hasWrongOrder();
	}
	
}
