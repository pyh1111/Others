package main.java.deeplearning.tools.rbmforcollaborativefiltering;

import main.java.drivers.TestCollaborativeFilteringRBM;
import main.java.genutils.BigFile;
import main.java.genutils.Rating;
import main.java.genutils.RbmOptions;
import org.jblas.DoubleMatrix;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by PYH on 2016/1/16.
 */
public class ComboRBM {

	public static HashMap<String, String> comboSet = new HashMap<String, String>();
	private static final Logger _logger = Logger.getLogger(TestCollaborativeFilteringRBM.class.getName());
	public  void loadComboData(String fileName) {
		try {
//			int columnIndex = 0;
			CollaborativeFilteringRBM.feature2Index = new HashMap<String, Integer>();
			BigFile f = new BigFile(fileName);
			Iterator<String> iterator = f.iterator();
			while (iterator.hasNext()) {
				String[] splits = iterator.next().split("\t");
				String str = "";
				for (int i = 1; i < splits.length; i++) {
					String foodid = splits[i].split("#")[0];
					str += foodid + "\t";
//					if (!CollaborativeFilteringRBM.feature2Index.containsKey(foodid)) {
//						CollaborativeFilteringRBM.feature2Index.put(foodid, columnIndex);
//						columnIndex++;
//					}
				}
				comboSet.put(splits[0], str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  double predictCombo(String user, String combo) {

		CollaborativeFilteringRBM rbmCF = new CollaborativeFilteringRBM();
		double preCom = 3.0;
		if (!comboSet.containsKey(combo)) {
			return preCom;
		} else {
			String[] foods = comboSet.get(combo).split("\t");
			int count = 0;
			for (String food : foods) {

				double preRat = 3.0;
				preRat = rbmCF.predict(user, food, PredictionType.MEAN);
				preCom += preRat;
				count++;
			}
			if (count > 0){
				preCom = preCom / count;
			}

		}
		return preCom;
	}

	public  void runComboRbm() throws IOException{
		_logger.info("Loading data..");

		RbmOptions options = new RbmOptions();
		ComboRBM comboRBM = new ComboRBM();
		options.maxepoch = 10;
		options.avglast = 5;
		options.numhid = 100;
		options.debug = false;

//		comboRBM.loadComboData("D:/DianPingData/combo_food_weight.txt");
		comboRBM.loadComboData("/home/liyuming/pyh/data/combo_food_weight.txt");
		//CollaborativeFilteringLayer fit = CollaborativeFilteringRBM.fit(data, options);
		CollaborativeFilteringRBM rbmCF = new CollaborativeFilteringRBM();
//        rbmCF.loadRatings("./data/" + "u.data");
//		rbmCF.loadRatings("D:/DianPingData/DataSet/" + "AvgTrain.txt");
		rbmCF.loadRatings("/home/liyuming/pyh/data/DataSet/" + "AvgTrain.txt");
		rbmCF.fit(options);

	}

}
