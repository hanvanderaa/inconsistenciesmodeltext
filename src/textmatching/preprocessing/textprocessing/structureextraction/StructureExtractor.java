package textmatching.preprocessing.textprocessing.structureextraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import textmatching.matching.config.MatchingConfig;
import textmatching.processmodel.PartialOrderingGeneric;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;
import textmatching.util.TextUtils;

public class StructureExtractor  {

	int mode;
	
	public StructureExtractor(int mode) {
//		super(preprocessor);
		this.mode = mode;
	}
	
	public void extractStructure(ProcessDescription description) {
		if (mode == MatchingConfig.STRUCTURE_EXTRACTION_ASSUMED) {
			extractStructureAssumption(description);
		}
		else if (mode == MatchingConfig.STRUCTURE_EXTRACTION) {
			extractStructureNoAssumption(description);
		} else {
			initializeStrictOrdering(description);
		}
	}
	
	
	
	
	private void extractStructureAssumption(ProcessDescription description) {
		PartialOrderingGeneric<Sentence> ordering = new PartialOrderingGeneric<Sentence>();
		List<Sentence> sentences = description.getSentences();
		for (Sentence sentence : sentences) {
			ordering.addT(sentence.getId(), sentence);
		}
		
		// this is for debugging
		List<List<Integer>> idlist = new ArrayList<List<Integer>>();
		
		Sentence andBlockPred = null;
		Sentence xorBlockPred = null;
		Set<Sentence> andBlock = new HashSet<Sentence>();
		Set<Sentence> xorBlock = new HashSet<Sentence>();
		for (int i = 1; i < sentences.size(); i++) {
			Sentence sentence = sentences.get(i);
			if (hasParallelism(sentence)) {
				if (andBlock.isEmpty()) {
					// AND-block started
					if (i > 1) {
						andBlockPred = sentences.get(i - 2);
					} else {
						andBlockPred = sentences.get(i - 1);
					}
					andBlock.add(sentences.get(i - 1));
				}
				andBlock.add(sentence);
				xorBlock = new HashSet<Sentence>();
			} else {
				// AND-block ended
				for (Sentence s2 : andBlock) {
					idlist.add(Arrays.asList(new Integer[]{andBlockPred.getId(), s2.getId()}));
					ordering.addFollowerPair(andBlockPred.getId(), s2.getId());
				}
				for (Sentence s2 : andBlock) {
					idlist.add(Arrays.asList(new Integer[]{s2.getId(), sentence.getId()}));
					ordering.addFollowerPair(s2.getId(), sentence.getId());
				}
				andBlock = new HashSet<Sentence>();
			}
			if (hasCondition(sentence)) {
				if (xorBlock.isEmpty()) {
					// XOR-block started
					xorBlockPred = sentences.get(i - 1);
				}
				xorBlock.add(sentence);
			} else {
				// XOR-block ended
				for (Sentence s2 : xorBlock) {
					idlist.add(Arrays.asList(new Integer[]{xorBlockPred.getId(), s2.getId()}));
					ordering.addFollowerPair(xorBlockPred.getId(), s2.getId());
				}
				for (Sentence s2 : xorBlock) {
					idlist.add(Arrays.asList(new Integer[]{s2.getId(), sentence.getId()}));
					ordering.addFollowerPair(s2.getId(), sentence.getId());
				}
				xorBlock = new HashSet<Sentence>();
			}
			if (!hasParallelism(sentence) && !hasCondition(sentence)) {
				idlist.add(Arrays.asList(new Integer[]{i -1, i}));
				ordering.addFollowerPair(i-1, i);
			}
			
		}
		for (Sentence s2 : andBlock) {
			idlist.add(Arrays.asList(new Integer[]{andBlockPred.getId(), s2.getId()}));
			ordering.addFollowerPair(andBlockPred.getId(), s2.getId());
		}
		for (Sentence s2 : xorBlock) {
			idlist.add(Arrays.asList(new Integer[]{xorBlockPred.getId(), s2.getId()}));
			ordering.addFollowerPair(xorBlockPred.getId(), s2.getId());
		}
		ordering.computeReachabilityRelations();
		description.addPartialOrdering(ordering);
	}
	

	private void extractStructureNoAssumptionOld(ProcessDescription description) {
		PartialOrderingGeneric<Sentence> ordering = new PartialOrderingGeneric<Sentence>();
		List<Sentence> sentences = description.getSentences();
		for (Sentence sentence : sentences) {
			ordering.addT(sentence.getId(), sentence);
		}
		Map<Sentence, DiscourseBlock> blockMap = new HashMap<Sentence, DiscourseBlock>();
		// this is for debugging
		List<List<Integer>> idlist = new ArrayList<List<Integer>>();
		
		
		Sentence predecessor = null;
		Set<Sentence> block = new HashSet<Sentence>();
		FlowType activeType = FlowType.NONE; 
		
		for (int i = 1; i < sentences.size(); i++) {
			Sentence current = sentences.get(i);
			FlowType type = getFlowType(current);
			if (type != FlowType.NONE && type != activeType) {
				// a block has ended
				if (type == FlowType.AND) {
					block.remove(sentences.get(i - 1));
				}
				for (Sentence blocked : block) {
					ordering.addFollowerPair(predecessor, blocked);
					idlist.add(Arrays.asList(new Integer[]{predecessor.getId(), blocked.getId()}));
					ordering.addFollowerPair(blocked, current);
					idlist.add(Arrays.asList(new Integer[]{blocked.getId(), current.getId()}));
				}
				block = new HashSet<Sentence>();
				activeType = type;
				if (type == FlowType.AND) {
					block.add(sentences.get(i - 1));
					if (i > 1) {
						predecessor = sentences.get(i - 2);
					} else {
						predecessor = sentences.get(i - 1);
					}
				} else {
					predecessor = sentences.get(i - 1);
				}
			}
			block.add(current);
		}
		for (Sentence blocked : block) {
			ordering.addFollowerPair(predecessor, blocked);
			idlist.add(Arrays.asList(new Integer[]{predecessor.getId(), blocked.getId()}));
		}
		ordering.computeReachabilityRelations();
		description.addPartialOrdering(ordering);
	}
	
	
	private void extractStructureNoAssumption(ProcessDescription description) {
		PartialOrderingGeneric<Sentence> ordering = new PartialOrderingGeneric<Sentence>();
		List<Sentence> sentences = description.getSentences();
		for (Sentence sentence : sentences) {
			ordering.addT(sentence.getId(), sentence);
		}
		Map<Integer, DiscourseBlock> blockMap = new HashMap<Integer, DiscourseBlock>();
		// this is for debugging
		List<List<Integer>> idlist = new ArrayList<List<Integer>>();
		
		
		DiscourseBlock block = new DiscourseBlock();
		FlowType activeType = FlowType.NONE; 
		
		for (int i = 0; i < sentences.size(); i++) {
			Sentence current = sentences.get(i);
			FlowType type = getFlowType(current);
			
			// check if a new block should be constructed
			if ((type != FlowType.NONE && type != activeType) || type == FlowType.SEQUENCE) {
				if (type == FlowType.AND) {
					block.remove(sentences.get(i - 1));
					blockMap.remove(i - 1);
				}
				block = new DiscourseBlock();
				activeType = type;
				if (type == FlowType.AND && i > 1) {
					block.add(sentences.get(i - 1));
					blockMap.put(i - 1, block);
					block.setPredecessor(blockMap.get(i -2));
				} else {
					block.setPredecessor(blockMap.get(i - 1));
				}
			}
			
			block.add(current);
			blockMap.put(i, block);
		}
		for (DiscourseBlock block2 : blockMap.values()) {
			for (Sentence succ : block2.getSentences()) {
				if (block2.getPredecessor() != null) {
					for (Sentence pred : block2.getPredecessor().getSentences()) {
						ordering.addFollowerPair(pred, succ);
						idlist.add(Arrays.asList(new Integer[]{pred.getId(), succ.getId()}));
					}
				}
			}
		}
		ordering.computeReachabilityRelations();
		description.addPartialOrdering(ordering);
	}
	
	private Boolean hasParallelism(Sentence sentence) {
		for (String lemma : sentence.getLemmas()) {
			if (TextUtils.isParallelIndicator(lemma)) {
				return true;
			}
		}
		return false;
	}
	
	private Boolean hasCondition(Sentence sentence) {
		for (String lemma : sentence.getLemmas()) {
			if (TextUtils.isConditional(lemma)) {
				return true;
			}
		}
		return false;
	}
	
	private Boolean hasSequence(Sentence sentence) {
		for (String lemma : sentence.getLemmas()) {
			if (TextUtils.isSequential(lemma) || TextUtils.isTrigger(lemma)) {
				return true;
			}
		}
		return false;
	}
	
	public FlowType getFlowType(Sentence sentence) {
		if (hasParallelism(sentence)) {
			return FlowType.AND;
		}
		if (hasCondition(sentence)) {
			return FlowType.XOR;
		}
		if (hasSequence(sentence)) {
			return FlowType.SEQUENCE;
		}
		return FlowType.NONE;
	}
	
	private void initializeStrictOrdering(ProcessDescription description) {
		List<Sentence> sentences = description.getSentences();
		PartialOrderingGeneric<Sentence> ordering = new PartialOrderingGeneric<Sentence>();
		for (int i = 0; i < sentences.size(); i++) {
			Sentence sentence1 = sentences.get(i);
			ordering.addT(sentence1.getId(), sentence1);
			if (i > 0) {
				ordering.addFollowerPair(i - 1, i);
			}
		}
		ordering.computeReachabilityRelations();
		description.addPartialOrdering(ordering);
	}
	
}
