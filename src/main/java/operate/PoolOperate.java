package main.java.operate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoolOperate {
	
		private ExecutorService exe;
		private static final int POOL_SIZE =1;
		public static CountDownLatch countDownLatch = null;

		public PoolOperate() {
			exe = Executors.newFixedThreadPool(POOL_SIZE);
		}
		
		public void doTask(BasePool basePool) throws InterruptedException {
			countDownLatch = new CountDownLatch(BasePool.size);
			System.out.println(BasePool.size);
			for (int i = 0; i < BasePool.size; i++) {
				exe.execute(new MyThread(i, exe, countDownLatch,basePool));
			}
			exe.shutdown();
		}

		class MyThread extends Thread {
			int id;
			ExecutorService exe;
			CountDownLatch countDownLatch = null;
			BasePool basePool;
			MyThread(int id, ExecutorService exe, CountDownLatch countDownLatch,BasePool basePool) {
				this.exe = exe;
				this.id = id;
				this.countDownLatch = countDownLatch;
				this.basePool = basePool;
			}
			
			public void run() {
				try {
					basePool.doSomething(id);
				}catch (Exception e) {
					e.printStackTrace();
				}
				countDownLatch.countDown();
			}
		}
}
