package main.java.drivers;

import main.java.deeplearning.tools.rbmforcollaborativefiltering.CollaborativeFilteringRBM;
import main.java.deeplearning.tools.rbmforcollaborativefiltering.ComboRBM;
import main.java.deeplearning.tools.rbmforcollaborativefiltering.PredictionType;
import main.java.genutils.BigFile;
import main.java.genutils.Rating;
import main.java.genutils.RbmOptions;
import org.jblas.DoubleMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by PYH on 2016/1/16.
 */
public class Evaluate {

	//nisdhao
	public static List<String> testData = new ArrayList<String>();
	private static final Logger _logger = Logger.getLogger(TestCollaborativeFilteringRBM.class.getName());


	public static void loadTestData(String fileName) {
		try {
			BigFile f = new BigFile(fileName);
			Iterator<String> iterator = f.iterator();
			while (iterator.hasNext()) {
				testData.add(iterator.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws IOException {
		_logger.info("Loading data..");
//
		RbmOptions options = new RbmOptions();
		options.maxepoch = 10;
		options.avglast = 5;
		options.numhid = 100;
		options.debug = false;

		//CollaborativeFilteringLayer fit = CollaborativeFilteringRBM.fit(data, options);
		CollaborativeFilteringRBM rbmCF = new CollaborativeFilteringRBM();
//        rbmCF.loadRatings("./data/" + "u.data");
		rbmCF.loadRatings("D:/DianPingData/DataSet/BaseLine1/" + "trainSet.txt");
		rbmCF.fit(options);

//		ComboRBM comboRBM = new ComboRBM();
//		comboRBM.runComboRbm();

//		writeToFile("./data/RBM/weight.txt", CollaborativeFilteringRBM.matrix);
//		writeToFile("./data/RBM/hidbiases.txt",CollaborativeFilteringRBM.hidbiases);
//		writeToFile("./data/RBM/visbiases.txt",CollaborativeFilteringRBM.visbiases);

//		for (int i = 1; i <= 5; i++) {
//			writeToFile("./data/RBM/weight.txt", CollaborativeFilteringRBM.Wijk.get(i));
//			writeToFile("./data/RBM/visbiases.txt", CollaborativeFilteringRBM.visbiases.get(i));
//		}
//		writeToFile("./data/RBM/hidbiases.txt", CollaborativeFilteringRBM.hidbiases);

		loadTestData("D:/DianPingData/DataSet/BaseLine1/" + "testSet.txt");
//		loadTestData("/home/liyuming/pyh/data/DataSet/BaseLine1/" + "testSet.txt");
		double RMSE = 0;
		int count = 0;
		for (String test : testData) {
			String[] splits = test.split("\t");
			double rat = Double.parseDouble(splits[2]);
			double preRat = rbmCF.predict(splits[0], splits[1], PredictionType.MEAN);
//			double preRat = comboRBM.predictCombo(splits[0], splits[1]);
//			double preRat = 3.1;
			System.out.println("Mean prediction = " + preRat +"\t\t" + rat);
			count++;
			RMSE += (preRat - rat) * (preRat - rat);
		}
		RMSE = Math.sqrt(RMSE / count);
		System.out.println("RMSE = " + RMSE);
//		System.out.println("Max prediction = " + rbmCF.predict("166", "346", PredictionType.MAX));
//		System.out.println("Mean prediction = " + rbmCF.predict("166", "346", PredictionType.MEAN));
	}

	public static void writeToFile(String fileName, DoubleMatrix matrix) {
		File file = new File(fileName);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			String str = "";
			for (int i = 0; i < matrix.getRows(); i++) {
				for (int j = 0; j < matrix.getColumns(); j++) {
					str += matrix.get(i, j) + "\t";
				}
				str += "\n";
			}
			str += "OK" + "\n";
			bw.write(str);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
