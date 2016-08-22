package textmatching.io.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import textmatching.alignment.Alignment;
import textmatching.alignmentverification.GroundTruth;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;
import textmatching.util.CleanupUtils;
import textmatching.util.FileUtils;

import com.opencsv.CSVReader;

public class In {

	
	private static String readFile(String path) throws IOException {
		Charset encoding = Charset.defaultCharset();
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	

	
	public static String readProcessText(File file) throws IOException {
		  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setValidating(false);
		    factory.setIgnoringElementContentWhitespace(true);
		    try {
		        DocumentBuilder builder = factory.newDocumentBuilder();
		        Document doc = builder.parse(file);
		        NodeList texts = doc.getElementsByTagName("text");
		        return texts.item(0).getTextContent();
		        
		    } catch (ParserConfigurationException e) {
		    	System.out.println(e.getLocalizedMessage());
		    } catch (SAXException e) {
		    	System.out.println(e.getLocalizedMessage());
		    } catch (IOException e) { 
		    	System.out.println(e.getLocalizedMessage());
		    }
		    return "";
	}
	
	
	public static Map<String, GroundTruth> readGroundTruths(File directory, Set<String> names) {
		Map<String, GroundTruth> groundTruths = new HashMap<String, GroundTruth>();
		for (File file : FileUtils.getFilesWithExtension(directory, ".csv")) {
			String name = FileUtils.getName(file).toLowerCase();
			if (names.contains(name)) {
				groundTruths.put(FileUtils.getName(file).toLowerCase(), readGroundTruth(file));
			}
		}
		return groundTruths;
	}
	
	public static GroundTruth readGroundTruth(File file) {
		System.out.println("processing groundtruth: " + file.getName());
		if (file.isFile()) {
			try {
				GroundTruth groundTruth = new GroundTruth(FileUtils.getName(file));
				CSVReader reader = new CSVReader(new FileReader(file.getAbsolutePath()), ';');
				reader.readNext();
				String[] nextLine;
				Map<Integer, Integer> activityToSentenceIds = new HashMap<Integer, Integer>();
				Map<Integer, String> activityIds = new HashMap<Integer, String>();
				Map<String, String> idsToSentences = new HashMap<String, String>();
				Map<Integer, String> activityToErrorCode = new HashMap<Integer, String>();
				
				// read activities and id matches
				while ((nextLine = reader.readNext()) != null && nextLine.length > 1 && 
						!nextLine[0].isEmpty()) {
						int activityid = Integer.parseInt(nextLine[0]);
						String activity = CleanupUtils.cleanActivityLabel(nextLine[1]);
						int sentenceid = -1;
						if (!nextLine[2].isEmpty()) {
							sentenceid = Integer.parseInt(nextLine[2]);
						}
						if (!nextLine[3].isEmpty()) {
							activityToErrorCode.put(activityid, nextLine[3]);
						}
						activityToSentenceIds.put(activityid, sentenceid);
						activityIds.put(activityid, activity);
					}
				
				reader.close();
				
				// start reading sentences
				reader = new CSVReader(new FileReader(file.getAbsolutePath()), ';');
				reader.readNext();
				int i = 0;
				while ((nextLine = reader.readNext()) != null && nextLine[6] != null && !nextLine[6].isEmpty()) {
					String sentenceid = String.valueOf(i);
					String sentence = nextLine[6];
					idsToSentences.put(sentenceid, sentence);
					i++;
				}
				for (int activityid : activityToSentenceIds.keySet()) {
					int sentenceid = activityToSentenceIds.get(activityid);
					String sentence = idsToSentences.get(sentenceid);
					String activity = activityIds.get(activityid);
					if (!activityToErrorCode.containsKey(activityid)) {
						groundTruth.addMatch(activity, activityid, sentence, sentenceid);
					} else {
						String errorcode = activityToErrorCode.get(activityid);
						groundTruth.addMatch(activity, activityid, sentence, sentenceid, errorcode);
					}
				}
				

				
				reader.close();
				return groundTruth;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static ProcessModel readProcessModel(File file) {
		String name = FileUtils.getName(file);
		ProcessModel processModel = new ProcessModel(name);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList activitiesList = doc.getElementsByTagName("activity");
			for (int i = 1; i < activitiesList.getLength(); i++) {
				Element activityNode = (Element) activitiesList.item(i);
				int id = Integer.parseInt(activityNode.getAttributes().getNamedItem("id").getNodeValue());
				String label = activityNode.getElementsByTagName("label").item(0).getTextContent();
				label = CleanupUtils.cleanActivityLabel(label);
				Activity activity = new Activity(id, label);
				processModel.addActivity(activity);
			}

			NodeList followersList = doc.getElementsByTagName("pair");
			for (int i = 0; i < followersList.getLength(); i++) {
				Node node = followersList.item(i);
				int pred = Integer.parseInt(node.getFirstChild().getTextContent());
				int succ = Integer.parseInt(node.getLastChild().getTextContent()); 
				processModel.addFollowerPair(pred, succ);
			}

		} catch (ParserConfigurationException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (SAXException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) { 
			System.out.println(e.getLocalizedMessage());
		}
		processModel.computeReachabilityRelations();
		return processModel;
	}
	
	
	public static Map<String, String> readMarkovMatches(String filepath) {
		String activity;
		String sentence;
		Map<String, String> result = new HashMap<String, String>();

		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(filepath));
			String line;
			while ((line = bf.readLine()) != null) {
				activity = line.substring(5, line.indexOf("\","));
				sentence = line.substring(line.indexOf(",")+3, line.length()-2);
				result.put(activity, sentence);
			}
			bf.close();
			return result;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	   public static Object deserializeObject(String filepath)
	   {
	      Object o = null;
	      try
	      {
	         FileInputStream fileIn = new FileInputStream(filepath);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         o = in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(IOException i)
	      {
	         i.printStackTrace();
	      }catch(ClassNotFoundException c)
	      {
	         c.printStackTrace();
	      }
	      return o;
	   }
}
