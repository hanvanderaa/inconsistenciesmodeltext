package textmatching.io.out;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import textmatching.evaluation.PredictionEvaluator;
import textmatching.inconsistencycheck.InconsistencyChecker;
import textmatching.inconsistencycheck.ActivityPredictor;
import textmatching.inconsistencycheck.Prediction;
import textmatching.inconsistencycheck.ProcessPredictor;
import textmatching.matching.config.MatchingConfig;
import textmatching.modeltextpair.ModelTextPair;
import textmatching.util.PrintUtils;

public class OutToExcel {

	public static void predictionsToExcel(String filepath, Collection<ModelTextPair> mtps, MatchingConfig config) {
		FileOutputStream fileOut;
		InconsistencyChecker checker = new InconsistencyChecker(mtps, config);
		
		boolean filteredrecall = false;
		
		try {
			fileOut = new FileOutputStream(filepath);
			Workbook wb = new HSSFWorkbook();

			CellStyle boldStyle = wb.createCellStyle();
			Font font = wb.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			boldStyle.setFont(font);
			
			
			
			for (ProcessPredictor p : ProcessPredictor.missingActivities()) {
				List<Double> recalls = new ArrayList<Double>();
				Sheet worksheet =  wb.createSheet("(p) " + p);
				int rownr = 0;
				arrayToRow(new String[]{"mtp", "activities", "missing", "wrongorder", "score",
						"precision", "recall", "fmeasure"},
						worksheet, rownr, boldStyle);
				rownr++;
				
				List<Prediction> predictions = checker.prioritizeProcesses(p);
				PredictionEvaluator eval = new PredictionEvaluator(predictions);
				for (int i = 0; i < predictions.size(); i++) {
					Prediction prediction = predictions.get(i);
					ModelTextPair mtp = (ModelTextPair) prediction.o;
					if (!filteredrecall || !recalls.contains(eval.recallAtN(i + 1))) {
						arrayToRow(new Object[]{mtp.getName(), mtp.getActivityCount(), mtp.hasMissingActivities(),
								mtp.hasWrongOrder(), prediction.getScore(),
								eval.precisionAtN(i + 1), eval.recallAtN(i + 1), eval.fMeasureAtN(i + 1)}, 
								worksheet, rownr);
						rownr++;
						recalls.add(eval.recallAtN(i + 1));
					}
				}
				rownr = rownr + 2;
				arrayToRow(new Object[]{"best precision", "full recall at", "best f-measure"}, worksheet, rownr);
				rownr++;
				arrayToRow(new Object[]{eval.highestPrecision(), eval.recallOneAt(), eval.highestFMeasure()}, worksheet, rownr);
			}
			

			for (ActivityPredictor p : ActivityPredictor.missingActivities()) {
				List<Double> recalls = new ArrayList<Double>();
				Sheet worksheet =  wb.createSheet("(m) " + p);
				int rownr = 0;
				arrayToRow(new String[]{"mtp", "activity", "missing", "wrongorder", "score",
						"precision", "recall", "fmeasure"},
						worksheet, rownr, boldStyle);
				rownr++;
				
				List<Prediction> predictions = checker.prioritizeMatches(p);
				PredictionEvaluator eval = new PredictionEvaluator(predictions);
				for (int i = 0; i < predictions.size(); i++) {
					Prediction prediction = predictions.get(i);
					if (!filteredrecall ||!recalls.contains(eval.recallAtN(i + 1))) {
						arrayToRow(new Object[]{prediction.getParent().toString(), prediction.o.toString(), prediction.isMissing(),
								prediction.getParent().hasWrongOrder(), prediction.getScore(),
								eval.precisionAtN(i + 1), eval.recallAtN(i + 1), eval.fMeasureAtN(i + 1)}, 
								worksheet, rownr);
						rownr++;
						recalls.add(eval.recallAtN( i + 1));
					}
				}
				rownr = rownr + 2;
				arrayToRow(new Object[]{"best precision", "full recall at", "best f-measure"}, worksheet, rownr);
				rownr++;
				arrayToRow(new Object[]{eval.highestPrecision(), eval.recallOneAt(), eval.highestFMeasure()}, worksheet, rownr);
			}
			
			
			for (ProcessPredictor p : ProcessPredictor.wrongOrder()) {
				List<Double> recalls = new ArrayList<Double>();
				Sheet worksheet =  wb.createSheet(p + " (Wrong Order)");
				int rownr = 0;
				arrayToRow(new String[]{"mtp", "activities", "missing", "wrongorder", "score",
						"precision", "recall", "fmeasure"},
						worksheet, rownr, boldStyle);
				rownr++;
				
				List<Prediction> predictions = checker.prioritizeProcesses(p);
				PredictionEvaluator eval = new PredictionEvaluator(predictions, true);
				for (int i = 0; i < predictions.size(); i++) {
					Prediction prediction = predictions.get(i);
					if (!prediction.isMissing() || prediction.isWrongOrder()) {
						ModelTextPair mtp = (ModelTextPair) prediction.o;
						if (!filteredrecall || !recalls.contains(eval.recallAtN(i + 1))) {
							arrayToRow(new Object[]{mtp.getName(), mtp.getActivityCount(), mtp.hasMissingActivities(),
									mtp.hasWrongOrder(), prediction.getScore(),
									eval.precisionAtN(i + 1), eval.recallAtN(i + 1), eval.fMeasureAtN(i + 1)}, 
									worksheet, rownr);
							rownr++;
							recalls.add(eval.recallAtN(i + 1));
						}
					}
				}
				rownr = rownr + 2;
				arrayToRow(new Object[]{"best precision", "full recall at", "best f-measure"}, worksheet, rownr);
				rownr++;
				arrayToRow(new Object[]{eval.highestPrecision(), eval.recallOneAt(), eval.highestFMeasure()}, worksheet, rownr);
			}
			
		
		
	    wb.write(fileOut);
	    wb.close();
	    fileOut.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
		


	
	
private static void arrayToRow(Object[] array, Sheet worksheet, int rownr) {
	arrayToRow(array, worksheet, rownr, null);
}


private static void arrayToRow(Object[] array, Sheet worksheet, int rownr, CellStyle style) {
	Row row = worksheet.createRow(rownr);
	for (int i = 0; i < array.length; i++) {
		Cell cell = row.createCell(i);
		if (array[i] != null && !array[i].equals("null")) {
			String val = String.valueOf(array[i]);
			if (array[i] instanceof Double) {
				double val2 = Double.parseDouble(val);
				val = String.valueOf(PrintUtils.dts((Double) array[i]));
				val.replace(".", ",");
//				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(val2);
			} else {
				cell.setCellValue(val);	
			}
			
		}
		if (style != null) {
			cell.setCellStyle(style);
		}
	}
}
}
