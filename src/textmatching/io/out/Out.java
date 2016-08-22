package textmatching.io.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import textmatching.alignment.Match;
import textmatching.alignmentverification.GroundTruth;
import textmatching.alignmentverification.VerificationSummary;
import textmatching.matchers.ActivityMatches;
import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.textualdescription.ProcessDescription;
import textmatching.textualdescription.Sentence;
import textmatching.util.PrintUtils;

import com.opencsv.CSVWriter;

public class Out {


	public static void summaryToCSV(String path, VerificationSummary summary) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(path), CSVOutUtils.separator);
			
			// write header
			writer.writeNext(CSVOutUtils.SUMMARY_HEADER);
			writer.writeNext(CSVOutUtils.SUMMARY(summary));
			emptyLine(writer);
			emptyLine(writer);
			
			// summarize configurations
			writer.writeNext(CSVOutUtils.SUMMARY_FOR_CONFIG_HEADER);
			for (MatchingConfig config : summary.getConfigs()) {
				writer.writeNext(CSVOutUtils.SUMMARY_FOR_CONFIG(config, summary));
			}
			
			// summarize results per model-text pair
			emptyLine(writer);
			emptyLine(writer);
			writer.writeNext(CSVOutUtils.SUMMARY_FOR_MTP_HEADER1(summary.getConfigs()));
			writer.writeNext(CSVOutUtils.SUMMARY_FOR_MTP_HEADER2(summary.getConfigs()));
			for (ModelTextPair mtp : summary.getMtps() ) {
				writer.writeNext(CSVOutUtils.SUMMARY_FOR_MTP(summary.getConfigs(), mtp));
			}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void mtpToCSV(String path, ModelTextPair mtp) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(path), CSVOutUtils.separator);
			
			// write overview
			writer.writeNext(CSVOutUtils.MTP_CSV_OVERVIEW_HEADER);
			writer.writeNext(CSVOutUtils.MTP_CSV_OVERVIEW(mtp));
			emptyLine(writer);
			
			// write groundtruth overview
			writer.writeNext(CSVOutUtils.MTP_CSV_GROUNDTRUTH_HEADER);
			writer.writeNext(CSVOutUtils.MTP_CSV_GROUNDTRUTH(mtp.getGroundTruth()));
			
			emptyLine(writer);
			
			GroundTruth gt = mtp.getGroundTruth();
			
			if (gt.missingActivityCount() > 0) {
				writer.writeNext(new String[]{"Missing activities:"});
			}
			
			List<Integer> unaligned = gt.getUnalignedActivityIDs();
			for (int id : mtp.getGroundTruth().getUnalignedActivityIDs()) {
				Activity activity = mtp.getModel().getEntry(id);
				writer.writeNext(new String[]{String.valueOf(id), activity.getLabel()});
			}
			
			
			emptyLine(writer);
			

			// write summarized results per configuration
			writer.writeNext(CSVOutUtils.MTP_CSV_RESULT_SUMMARY_HEADER);
			for (MatchingConfig config : mtp.getConfigs()) {
				writer.writeNext(CSVOutUtils.MTP_CSV_RESULT_SUMMARY(config, mtp.getVerification(config)));
			}
			emptyLine(writer);
			
			// write results per configuration
			for (MatchingConfig config : mtp.getConfigs()) {
				String[][] output = CSVOutUtils.MTP_CSV_RESULT_FOR_CONFIG(config, mtp.getVerification(config));
				for (int i = 0; i < output.length; i++) {
					writer.writeNext(output[i]);
				}
				emptyLine(writer);
				emptyLine(writer);
			}
			
			
			// output scores
			for (MatchingConfig config : mtp.getConfigs()) {
				writer.writeNext(CSVOutUtils.CONFIG_CSV(config));
				writer.writeNext(CSVOutUtils.MTP_CSV_SCORES_HEADER(mtp.getSentenceCount()));
				ProcessModel anModel = mtp.getAnalyzedModel(config);
				for (Activity activity : anModel.getActivities()) {
					writer.writeNext(CSVOutUtils.MTP_CSV_SCORES(activity, anModel.getScores(activity)));
				}
				emptyLine(writer);
			}
			
			
			// write description overview
			writer.writeNext(CSVOutUtils.MTP_CSV_DESCRIPTION_OVERVIEW_HEADER);
//			for (Sentence sentence : mtp.getDescription().getSentences()) {
//				writer.writeNext(CSVOutUtils.MTP_CSV_DESCRIPTION_OVERVIEW(sentence));
//			}
			for (Sentence sentence : mtp.getHighestProcessedDescription().getSentences()) {
				writer.writeNext(CSVOutUtils.MTP_CSV_DESCRIPTION_OVERVIEW(sentence));
			}
			
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void emptyLine(CSVWriter writer) {
		writer.writeNext(new String[]{});
	}

	
	
	public static void createMarkovEvidenceFile(String path, ProcessModel processModel, ProcessDescription description) {
		try {
			FileWriter fw = new FileWriter(path);
			Map<Activity, ActivityMatches> matches = processModel.getScoreMap();
			
			// output similarity map
			String predicate = "lexicalSupport";
			for (ActivityMatches activityMatches : matches.values()) {
				for (Match match : activityMatches.getMatches()) {
					Object[] entries = new Object[]{match.getActivity().getLabel(), 
							match.getSentence().getOriginal(), match.getScore()};
					fw.write(PrintUtils.createPredicate(predicate, entries));
					}
				}
	
			
			// output partial orderings
			predicate = "soActivity";
			for (Activity activity : processModel.getActivities()) {
				for (Activity predecessor : processModel.getAllPredecessors(activity)) {
					if (predecessor != null && !predecessor.equals(activity)) {
						Object[] entries = new Object[]{predecessor.getLabel(), activity.getLabel()};
						fw.write(PrintUtils.createPredicate(predicate, entries));
					}
				}
			}
			predicate = "soSentence";
			for (Sentence sentence : description.getSentences()) {
				for (Sentence predecessor : description.getAllPredecessors(sentence)) {
					if (predecessor != null) {
						Object[] entries = new Object[]{predecessor.getLabel(), sentence.getLabel()};
						fw.write(PrintUtils.createPredicate(predicate, entries));
					}
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void createMarkovGTFile(String path, ModelTextPair mtp,
			MatchingConfig config) {
		try {
			FileWriter fw = new FileWriter(path);
			
			GroundTruth gt = mtp.getGroundTruth();
			ProcessDescription description = mtp.getProcessedDescription(config);
			List<Activity> activities = mtp.getAnalyzedModel(config).getActivities();
			
			
			String predicate = "gt";
			for (Activity activity : activities) {
				int sentenceid = gt.getSentenceID(activity);
				Sentence sentence = description.getSentence(sentenceid);
				if (sentence != null) {
					
					Object[] entries = new Object[]{activity.getLabel(), sentence.getLabel()};
					fw.write(PrintUtils.createPredicate(predicate, entries));
				}
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void writeInconsistencySummary(String path, VerificationSummary summary) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(path), CSVOutUtils.separator);
			
			int total = 0;
			
			for (ModelTextPair mtp : summary.getMtps() ) {
				GroundTruth gt = mtp.getGroundTruth();
				
				
				if (gt.getUnalignedActivityIDs().size() > 0) {
					writer.writeNext(new String[]{mtp.getName()});
					writer.writeNext(new String[]{"Missing activities:"});
				

				List<Integer> unaligned = gt.getUnalignedActivityIDs();
				for (int id : mtp.getGroundTruth().getUnalignedActivityIDs()) {
					Activity activity = mtp.getModel().getEntry(id);
					writer.writeNext(new String[]{String.valueOf(id), activity.getLabel()});
					total++;
				}
				
				emptyLine(writer);
				
				// output scores
				for (MatchingConfig config : mtp.getConfigs()) {
					writer.writeNext(CSVOutUtils.CONFIG_CSV(config));
					writer.writeNext(CSVOutUtils.MTP_CSV_SCORES_HEADER(mtp.getSentenceCount()));
					ProcessModel anModel = mtp.getAnalyzedModel(config);
					for (Activity activity : anModel.getActivities()) {
						writer.writeNext(CSVOutUtils.MTP_CSV_SCORES(activity, anModel.getScores(activity)));
					}
					emptyLine(writer);
				}

				}

			}
			
			emptyLine(writer);
			emptyLine(writer);
			writer.writeNext(new String[]{"total missing activities:", String.valueOf(total)});
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void serializeObject(String folderpath, String filename, Object object) {
		if (!folderpath.endsWith("/")) {
			folderpath += "/";
		}
		File folder = new File(folderpath);
		if (!folder.exists()) {
			folder.mkdir();
		}
	   try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(folderpath + filename);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(object);
	         out.close();
	         fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	   }
	
}
