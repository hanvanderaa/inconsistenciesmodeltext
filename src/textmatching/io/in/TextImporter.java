package textmatching.io.in;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import textmatching.util.FileUtils;

public class TextImporter {

	


	
	
	public static Map<String, String> readTexts(File directory) {
		Map<String, String> texts = new HashMap<String, String>();
		for (File file : FileUtils.getFilesWithExtension(directory, ".txt")) {
			try {
				texts.put(FileUtils.getName(file), FileUtils.readFile(file));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return texts;
	}
}
