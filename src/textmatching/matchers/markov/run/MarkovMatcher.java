package textmatching.matchers.markov.run;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import textmatching.alignment.Alignment;
import textmatching.alignment.Match;
import textmatching.io.in.In;
import textmatching.io.out.Out;
import textmatching.matchers.Matcher;
import textmatching.matchers.markov.rockit.PyRockitAPI;
import textmatching.matching.config.MatchingConfig;
import textmatching.matching.similarity.Scorer;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;

public class MarkovMatcher extends Matcher{


	private static final double MARKOV_GAP = 0.0000000001;
	
	private String modelname;
	private String outFolder;
	private String outFile;
	
	
	public MarkovMatcher(MatchingConfig config, Scorer scorer,
			ProcessDescription description, ProcessModel model) {
		super(config, scorer, description, model);
		modelname = model.getName();
		
		outFolder = MarkovConfig.PATH_MARKOV_OUTPUT + config.folderName();
		new File(outFolder).mkdirs();
		outFile =  outFolder + modelname + ".txt";
		
	}


	public Alignment run()  {
		
		// create evidence file
		createEvidenceFile();
		
		// Run Markov using generated files
		runMarkov();
		
		
		// create alignment out of Markov results
		Alignment alignment = createMarkovAlignment();
		return alignment;
		
		// Evaluate Markov Results
//		FileWriter fw = new FileWriter(new File(MarkovConfig.PATH_MARKOV_OUTPUT + "EvaluationResults.txt"),true);
//		Evaluator.evaluateResult(modelIDs);
//		fw.close();
	}
	
	private void createEvidenceFile() {
		String dirpath = MarkovConfig.PATH_MARKOV_INPUT + config.folderName();
		new File(dirpath).mkdirs();
		String path = dirpath + model.getName() + ".db";
		Out.createMarkovEvidenceFile(path, model, description);
	}
	
//	private void createMarkovFileGT(ModelTextPair mtp, MatchingConfig config) {
//		String dirpath = MARKOV_GT_DIRECTORY;
//		new File(dirpath).mkdirs();
//		String path = dirpath + mtp.getName() + ".txt";
//		Out.createMarkovGTFile(path, mtp, config);
//	}

	private void runMarkov() {
		String evidenceFile = model.getName();
		String evidencePath = MarkovConfig.PATH_MARKOV_INPUT + config.folderName() + evidenceFile + ".db"; 
		String path_markov_model;
		if (config.matchingStrategy == MatchingConfig.NAIVE_MATCHER) {
			path_markov_model = MarkovConfig.PATH_MARKOV_MODEL_NAIVE;
		} else {
			path_markov_model = MarkovConfig.PATH_MARKOV_MODEL_ORDER;
		}
		
//		String id = evidenceFile.substring(0, evidenceFile.length()-3);
		File input = new File(MarkovConfig.PATH_MARKOV_INPUT + path_markov_model);
		File data = new File(evidencePath);
		try {
			FileWriter fw = new FileWriter(outFile);
			
			
			PyRockitAPI.runMapRockit(input, data, "" + MARKOV_GAP);


			double objective = PyRockitAPI.getObjective();
			List<String> mapstate = PyRockitAPI.getMAPState();

			System.out.println("OBJECTIVE (Model " + model.getName() + "): " + objective);
			System.out.println("MAPSTATE: ");

//			FileWriter fw = new FileWriter(outFile);
			for (String line : mapstate) {
				if (line.startsWith("map")) {
					System.out.println(line);
					fw.write(line + "\n");
				}
			}
			fw.close();
			System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Alignment createMarkovAlignment() {
		Alignment alignment = new Alignment(model.getName(), config, model.getActivities(), model.getScoreMap());
		Map<String, String> markovMatches = In.readMarkovMatches(outFile);
		if (markovMatches != null) {
			for (String activityLabel : markovMatches.keySet()) {
				Activity activity = model.getActivity(activityLabel);
				Sentence sentence = description.findSentenceOfMarkovString(markovMatches.get(activityLabel));
				Match match = model.getMatch(activity, sentence);
				alignment.addMatch(match);
			}
		}
		return alignment;

	}
}