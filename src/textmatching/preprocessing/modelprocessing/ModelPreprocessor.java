package textmatching.preprocessing.modelprocessing;

import textmatching.preprocessing.Preprocessor;
import textmatching.processmodel.Activity;
import textmatching.processmodel.ProcessModel;

public class ModelPreprocessor extends Preprocessor {

	int mode;
	public static final int NORMAL_MODE = 1;
//	public static final int FANCY_
	
	public ModelPreprocessor(Preprocessor preprocessor) {
		super(preprocessor);
		int mode = NORMAL_MODE;
	}

	public ProcessModel preprocessModel(String name, ProcessModel model) {
		ProcessModel newModel = new ProcessModel(model);
		for (Activity activity : newModel.getActivities()) {
			activity.setLemmas(lemmatize(activity.getLabel()));
//			newModel.addActivity(newActivity);
		}
		return newModel;

	}
	
	


	
	
	

	

	

}
