package main.java.deeplearning.tools.rbmforcollaborativefiltering;

import main.java.genutils.BigFile;

import java.util.Iterator;

/**
 * Created by PYH on 2016/1/16.
 */
public class ComboRBM {


	public static void loadComboData(String fileName){
		try{
			BigFile f = new BigFile(fileName);
			Iterator<String> iterator = f.iterator();
			while (iterator.hasNext()) {
				testData.add(iterator.next());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
