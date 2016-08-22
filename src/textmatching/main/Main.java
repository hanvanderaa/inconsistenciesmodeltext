package textmatching.main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationException;

import textmatching.alignment.Alignment;
import textmatching.alignmentverification.AlignmentVerifier;
import textmatching.alignmentverification.GroundTruth;
import textmatching.alignmentverification.MTPAssertion;
import textmatching.alignmentverification.VerificationSummary;
import textmatching.alignmentverification.VerifiedAlignment;
import textmatching.inconsistencycheck.InconsistencyChecker;
import textmatching.io.in.In;
import textmatching.io.in.TextImporter;
import textmatching.io.out.Out;
import textmatching.io.out.OutToExcel;
import textmatching.matchers.Matcher;
import textmatching.matchers.bfsearcher.BFSearcher;
import textmatching.matchers.naivematcher.NaiveMatcher;
import textmatching.matching.config.MatchingConfig;
import textmatching.matching.similarity.Scorer;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.preprocessing.Preprocessor;
import textmatching.preprocessing.modelprocessing.ModelPreprocessor;
import textmatching.preprocessing.textprocessing.TextPreprocessor;
import textmatching.processmodel.PartialOrdering;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;
import textmatching.util.FileUtils;
import textmatching.util.PrintUtils;


public class Main {

	public final boolean TEST_RUN = false;
	public final String[] TEST_IDS = new String[]{"Model11-6"};
	
	public final boolean reloadInput = true;
	public final boolean createAlignments = false;
	
	public final String MODEL_DIRECTORY = "input/models";
	public final String TEST_MODEL_DIRECTORY = "input/testmodels";
	public final String DESCRIPTION_DIRECTORY = "input/texts";
	public final String GROUNDTRUTH_DIRECTORY = "input/groundtruths"; 
	public final String XMLMODEL_DIRECTORY = "input/xmlfiles";
	public final String MARKOV_INPUT_DIRECTORY = "input/markov/";
	public final String MARKOV_GT_DIRECTORY = "input/markovgt/";
	public final String SERIAL_DIRECTORY = "cache/serialized";
	public final String OUTPUT_DIRECTORY = "output/";
	public final String CASE_OUTPUT_DIRECTORY = "output/caseresults/";
	
	public static Preprocessor preprocessor;
	Map<String, ProcessModel> models;
	Map<String, ProcessDescription> descriptions;
	Map<String, GroundTruth> groundTruths;
	Map<String, PartialOrdering> partialOrderings;
	Map<String, ModelTextPair> mtps;
	List<MatchingConfig> configurations;

	public static void main(String args[]) throws IOException, SerializationException {
		Main main = new Main();
		
		main.loadStuff();

		main.matchStuff();

		main.evaluateApproach();
		
		main.writeAllTheThings();

		System.out.println("Finished.");
	}
	


	private void initializeConfigs() {
		configurations = new ArrayList<MatchingConfig>();
		

		configurations.add(new MatchingConfig(
				MatchingConfig.FULL_PREPROCESSING,
				MatchingConfig.MODE_LIN,
				MatchingConfig.MIHALCEA_SIM,
				MatchingConfig.BFSEARCHER, 
				false
				));
	
		System.out.println(configurations.size() + " configurations initialized.");
	}
	
	
	public void loadStuff() {
		// initialize
		
		initializeConfigs();
		preprocessor = new Preprocessor();
		
		if (reloadInput) {

			// load everything
			if (!TEST_RUN) {
				loadModels(new File(MODEL_DIRECTORY));
			}
			else {
				loadModels(new File(MODEL_DIRECTORY), TEST_IDS);
			}
			loadDescriptions(new File(DESCRIPTION_DIRECTORY)); 
			loadGroundTruths(new File(GROUNDTRUTH_DIRECTORY));

			// find complete processes with model, description, groundtruth
			pairModelsTextsGTs();
		} else {
			loadSerialized();
		}
		System.out.println(mtps.size() + " model-text pairs loaded");
		
	}
	
	public void matchStuff() {

		// match stuff
		for (ModelTextPair mtp : mtps.values()) {
			matchMTP(mtp);
		}
	}
	
	
	private void writeAllTheThings() {
		System.out.println("Writing output files");
		for (ModelTextPair mtp : mtps.values()) {
			String path = CASE_OUTPUT_DIRECTORY +  mtp.getName() + ".csv";
			Out.mtpToCSV(path, mtp);
//			System.out.println("written to " + path);
		}
		String summaryOut = OUTPUT_DIRECTORY + "resultsoverview.csv";
		VerificationSummary summary = new VerificationSummary(configurations, mtps.values());
		if (!TEST_RUN) {
			Out.summaryToCSV(summaryOut, summary);
			
		}
		for (MatchingConfig config : configurations) {
			String predictionOut = OUTPUT_DIRECTORY +  "predictions/" + config.toString() + ".xls";
			File dir = new File(OUTPUT_DIRECTORY + "predictions/");
			if (!dir.exists()) {
				dir.mkdir();
			}
			OutToExcel.predictionsToExcel(predictionOut, mtps.values(), config);
		}
		for (ModelTextPair mtp : mtps.values()) {
			Out.serializeObject(SERIAL_DIRECTORY, mtp.getName() + ".ser", mtp);
		}
	}

	@SuppressWarnings("unused")
	private void matchMTP(ModelTextPair mtp) {
		for (MatchingConfig config : configurations) {
			if (reloadInput || createAlignments || config.reserialize || mtp.getVerification(config) == null) {
				matchMTP(mtp, config);
				verifyAlignment(mtp, config);
			}
		}
	}
	
	private void matchMTP(ModelTextPair mtp, MatchingConfig config) {
			System.out.println("Matching: " + mtp.getName() + " using: " + config.toString());

			// preprocess textual description
			TextPreprocessor textPreprocessor = new TextPreprocessor(preprocessor, config);
			ProcessDescription processedDescription = textPreprocessor.preprocessDescription(mtp.getDescription());
			mtp.addProcessedDescription(config, processedDescription);

			// preprocess model
			ModelPreprocessor modelPreprocessor = new ModelPreprocessor(preprocessor);
			ProcessModel processedModel = modelPreprocessor.preprocessModel(mtp.getName(), mtp.getModel());
			mtp.addAnalyzedModel(config, processedModel);

			Scorer scorer = new Scorer(config, mtp);
			Alignment alignment = null;
			Matcher matcher = obtainMatcher(config, scorer, processedModel, processedDescription);
			if (matcher != null) {
				alignment = matcher.createAlignment();
			}
			PrintUtils.outputAlignment(alignment);
			mtp.addAlignment(config, alignment);
	}
	
	
	private void verifyAlignment(ModelTextPair mtp, MatchingConfig config) {
		AlignmentVerifier verifier = new AlignmentVerifier();
		VerifiedAlignment verification = verifier.verifySolution(mtp, config);
		mtp.addVerification(config, verification);
		PrintUtils.printVerication(verification);
	}
	
	
	
	
	
	public void pairModelsTextsGTs() {
		mtps = new HashMap<String, ModelTextPair>();
		for (String key : models.keySet()) {
			ProcessModel model = models.get(key);
			if (descriptions.containsKey(key) && groundTruths.containsKey(key)) {
				ProcessDescription description = descriptions.get(key);
				GroundTruth gt = groundTruths.get(key);
				MTPAssertion assertion = new MTPAssertion(model, description, gt);
				ModelTextPair mtp = new ModelTextPair(key, model, description, gt, assertion);
				mtps.put(key, mtp);
			}
		}

	}
	



	
	
	public void loadGroundTruths(File directory) {
		groundTruths = In.readGroundTruths(directory, models.keySet());
	}

	public void loadModels(File directory) {
		models = new HashMap<String, ProcessModel>();
		for (File file : FileUtils.getFilesWithExtension(new File(XMLMODEL_DIRECTORY), ".xml")) {
			if (!file.getName().equals("template.xml")) {
				ProcessModel processModel = In.readProcessModel(file);
				models.put(processModel.getName(), processModel);
			}
		}
	}
	
	public void loadModels(File directory, String[] idList) {
		models = new HashMap<String, ProcessModel>();
		for (File file : FileUtils.getFilesWithExtension(new File(XMLMODEL_DIRECTORY), ".xml")) {
			if (!file.getName().equals("template.xml") &&
					Arrays.asList(idList).contains(FileUtils.getName(file))) {
				ProcessModel processModel = In.readProcessModel(file);
				models.put(processModel.getName(), processModel);
			}
		}
	}

	
	public void loadDescriptions(File directory) {
		TextPreprocessor textPreprocessor = new TextPreprocessor(preprocessor);
		Map<String, String> texts = TextImporter.readTexts(directory);
		descriptions = new HashMap<String, ProcessDescription>();
		for (String key : texts.keySet()) {
			if (models.keySet().contains(key)) {
				System.out.println("Processing description: " + key);
				ProcessDescription description = textPreprocessor.readDescription(texts.get(key));
				descriptions.put(key.toLowerCase(), description);
			}
		}
	}
	
	private Matcher obtainMatcher(MatchingConfig config, Scorer scorer, 
			ProcessModel model, ProcessDescription description) {
		if (config.matchingStrategy == MatchingConfig.NAIVE_MATCHER) {
			return new NaiveMatcher(config, scorer, description, model);
		}
		if (config.matchingStrategy == MatchingConfig.BFSEARCHER) {
			return new BFSearcher(config, scorer, description, model);
		}
		return null;
	}
	
	

	private void evaluateApproach() {
		for (MatchingConfig config : configurations) {
			InconsistencyChecker checker = new InconsistencyChecker(mtps.values(), config);
			checker.evaluatePredictorPerformance();
		}
	
	}
	
	
	private void loadSerialized() {
		mtps = new HashMap<String, ModelTextPair>();
		for (File mtpSerial : FileUtils.getFilesWithExtension(new File(SERIAL_DIRECTORY), ".ser")) {
			ModelTextPair mtp = (ModelTextPair) In.deserializeObject(mtpSerial.getAbsolutePath());
			mtps.put(mtp.getName(), mtp);
		}
		
	}

	

}
