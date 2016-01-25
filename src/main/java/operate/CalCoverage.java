package main.java.operate;

import main.java.deeplearning.tools.rbmforcollaborativefiltering.CollaborativeFilteringRBM;
import main.java.deeplearning.tools.rbmforcollaborativefiltering.ComboRBM;
import main.java.deeplearning.tools.rbmforcollaborativefiltering.PredictionType;
import main.java.genutils.PredictResult;
import main.java.genutils.RbmOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;



/*
 * 计算覆盖率
 */
public class CalCoverage extends BasePool {

	private static int top_K = 100;
	private static double[] cov;
	private static double[] recla;
	private static double[] yue;
	private static int[][] position ;
	private static ArrayList<String> testData = new ArrayList<String>();
	public static List<String> yuexiaoliang = new ArrayList<String>();
	
	public void doSomething(int id) {
		CollaborativeFilteringRBM cfRBM = new CollaborativeFilteringRBM();
		Arrays.fill(position[id], 0);
		int count = 0,countyue = 0;
		ArrayList<PredictResult> perRtn = new ArrayList<PredictResult>();
		String userid = testData.get(id).split("\t")[0];
		Set<String> combos = CollaborativeFilteringRBM.feature2Index.keySet();
		for (String combo : combos) {
			if (CollaborativeFilteringRBM.user2Index.containsKey(userid)) {
				double rat = cfRBM.predict(userid,combo, PredictionType.MEAN);
				System.out.println("rat = "+ rat);
				perRtn.add(new PredictResult(combo + "", rat));
			}

		}
		Collections.sort(perRtn);
		String[] splits = testData.get(id).split("\t");
		for (int i = 1; i < splits.length; i++) {
			for (int j = 0; j < perRtn.size(); j++) {
				if (perRtn.get(j).getItem().equals(splits[i])) {
					position[id][count] = j;
					count++;
					
				}
			}
			if(yuexiaoliang.contains(splits[i])){
				countyue++;
			}
		}
		recla[id] = (double) count / (splits.length - 1);
		cov[id] = (double) count / top_K;
		yue[id] = (double) countyue / top_K;
		count = 0;
	}



	public void setSize(int lengt) {
		CalCoverage.size = lengt;
	}

	public static void main(String[] args) throws Exception {
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


		yuexiaoliang.add("11426974");
		yuexiaoliang.add("13708393");
		yuexiaoliang.add("11994469");
		yuexiaoliang.add("9128898");
		yuexiaoliang.add("11784051");
		System.out.println("参数训练结束");
		 File file = new File("D:/DianPingData/usersSet.txt");
//		File file = new File("/home/liyuming/pyh/data/usersSet.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String inputLine = br.readLine();
		while ((inputLine = br.readLine()) != null) {
			testData.add(inputLine);
		}
		br.close();
		System.out.println("读文件结束");
		cov = new double[testData.size()];
		recla = new double[testData.size()];
		yue = new double[testData.size()];
		position = new int[testData.size()][top_K];
		CalCoverage calCoverage = new CalCoverage();
		calCoverage.setSize(testData.size());
		PoolOperate poolOperate = new PoolOperate();
		poolOperate.doTask(calCoverage);
		PoolOperate.countDownLatch.await();
		System.out.println("结束2");
		 file = new File("D:/DianPingData/RBMcoverageItem.txt");
//		file = new File("/home/liyuming/pyh/data/RBMcoverage10.txt");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		String content = "";
		for (int i = 0; i < cov.length; i++) {
			content += testData.get(i).split("\t")[0] + "\t" + cov[i] + "\t" + recla[i] + "\t" + yue[i];
			for(int j = 0;position[i][j] > 0;j++){
				content += "###"+position[i][j];
			}
			 content += "\n";
		}
		bw.write(content);
		bw.flush();
		bw.close();
	}





}
