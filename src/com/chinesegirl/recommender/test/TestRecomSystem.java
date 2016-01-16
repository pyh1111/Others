package com.chinesegirl.recommender.test;

import com.chinesegirl.recommender.base.ItemBaseCF;
import com.chinesegirl.recommender.base.ItemBaseCFChanged;
import com.chinesegirl.recommender.entity.ItemRateCollect;
import com.chinesegirl.recommender.entity.UserRateCollect;
import com.chinesegirl.recommender.util.FileLoader;
import com.chinesegirl.recommender.util.RecomMethodInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class TestRecomSystem {

    public static void main(String[] args) throws IOException, FileNotFoundException {

        UserRateCollect userData = FileLoader.readUserTrainData("dataset/train.txt");

        ItemRateCollect itemData = FileLoader.readItemTrainData("dataset/train.txt");

        List<TestDataEntity> testData = FileLoader.readTestData("dataset/test.txt");
        System.out.println("Load data finished...\n");

        System.out.println("(1) Original item-based CF:");

        RecomMethodInterface recomMethod1 = new ItemBaseCF(30);

        TestRecMethod test1 = new TestRecMethod(testData, userData, itemData, recomMethod1);

        double mae = test1.getMae();
        double rmse = test1.getRmse();
        System.out.println("平均绝对误差MAE: " + mae);
        System.out.println("均方根误差RMSE: " + rmse);

        System.out.println("\n(2) item-based CF after corrected:");
        RecomMethodInterface recomMethod2 = new ItemBaseCFChanged(30);
        TestRecMethod test2 = new TestRecMethod(testData, userData, itemData, recomMethod2);
        mae = test2.getMae();
        rmse = test2.getRmse();
        System.out.println("校正后平均绝对误差MAE: " + mae);
        System.out.println("校正后均方根误差RMSE: " + rmse);


    }

}
