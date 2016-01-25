package main.java.deeplearning.tools.rbmforcollaborativefiltering;

import main.java.genutils.BigFile;
import main.java.genutils.Rating;
import main.java.operate.BasePool;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by PYH on 2016/1/19.
 * Âß¼­»Ø¹é
 */
public class LineLogistic {
	public static HashMap<String,ArrayList<Rating>> comboRatInfo = new HashMap<String ,ArrayList<Rating>>();
	public  static Hashtable<String,List<Double>> foodsWeigths = new Hashtable<String,List<Double>>();

	public void loadComboTrainRating(String fileNema){
		try {
			BigFile f = new BigFile(fileNema);
			Iterator<String> iterator = f.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				String[] splits = line.split("\t");
				String user = splits[0];
				String combo = splits[1];
				double rat = Double.parseDouble(splits[2]);
				if (comboRatInfo.containsKey(combo)){
					comboRatInfo.get(combo).add(new Rating(user,rat));
				}else {
					ArrayList<Rating> list = new ArrayList<Rating>();
					list.add(new Rating(user,rat));
					comboRatInfo.put(combo,list);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}

 class MyPool {

	private ExecutorService exe;
	private static final int POOL_SIZE =1;
	public static CountDownLatch countDownLatch = null;

	public MyPool() {
		exe = Executors.newFixedThreadPool(POOL_SIZE);
	}

	public void doTask(BasePool basePool) throws InterruptedException {
		countDownLatch = new CountDownLatch(BasePool.size);
		System.out.println(BasePool.size);
		for (Map.Entry<String,ArrayList<Rating>> entry : LineLogistic.comboRatInfo.entrySet()){
			exe.execute(new MyThread(entry.getKey(), exe, countDownLatch));
		}
		exe.shutdown();
	}

	class MyThread extends Thread {
		String combo;
		ExecutorService exe;
		CountDownLatch countDownLatch = null;
		BasePool basePool;
		MyThread(String combo, ExecutorService exe, CountDownLatch countDownLatch) {
			this.exe = exe;
			this.combo = combo;
			this.countDownLatch = countDownLatch;

		}

		public void run() {
			CollaborativeFilteringRBM cfRBM = new CollaborativeFilteringRBM();
			try {
				for (int i = 0; i <  LineLogistic.comboRatInfo.get(combo).size(); i++) {
					String user = LineLogistic.comboRatInfo.get(combo).get(i).itemId;
					double rat = LineLogistic.comboRatInfo.get(combo).get(i).rating;
					String[] foods = ComboRBM.comboSet.get(combo).split("\t");
					for (String food : foods){
						double preFoodRat = cfRBM.predict(user,food,PredictionType.MEAN);

					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			countDownLatch.countDown();
		}
	}
}

