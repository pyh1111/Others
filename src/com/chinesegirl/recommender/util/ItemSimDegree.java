package com.chinesegirl.recommender.util;

import com.chinesegirl.recommender.entity.ItemRateCollect;
import com.chinesegirl.recommender.entity.ItemRateCollect.ItemRate;

import java.util.Set;

/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * @author Shaq
 * @version 0.1
 * @filename ItemSimDegree.java
 * @note Calculate item similarity:
 * (1) getSimDegree(); ----求item1,item2的相似性
 * (2) getSimDegreeChanged(); ----校正的余弦相似度计算
 * @since 2015-05-20
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */
public class ItemSimDegree {
    /**
     * 求item1,item2的相似性
     */
    public static double getSimDegree(ItemRateCollect collect, Integer itemId1, Integer itemId2) {
        if (itemId1.equals(itemId2)) {
            return 1.0;
        }

        double item1AvgRate = collect.itemCollect.get(itemId1).avgRate; //对应item1的所有评分的均值
        double item2AvgRate = collect.itemCollect.get(itemId2).avgRate;

        if (collect.itemCollect.containsKey(itemId1) == false || collect.itemCollect.containsKey(itemId2) == false) {
            return 0.0;
        }

        ItemRate item1Rate = collect.itemCollect.get(itemId1); //由itemid1对应的评分值类
        ItemRate item2Rate = collect.itemCollect.get(itemId2);
        Set<Integer> userIdSet1 = item1Rate.itemUserRate.keySet(); //返回对item1有评分的userid
        Set<Integer> userIdSet2 = item2Rate.itemUserRate.keySet();
        double fenzi = 0.0;
        double fenmu1 = 0.0;
        double fenmu2 = 0.0;
        int count = 0,count1 = 0,count2 = 0;
        for (Integer userId1 : userIdSet1) { //取所有对item1有过的评分
            double item1UserRate = item1Rate.itemUserRate.get(userId1); //每个user对item1的评分
            if (userIdSet2.contains(userId1)) {
                double item2UserRate = item2Rate.itemUserRate.get(userId1); //每一对item1有评价的user对Item2的评分
                count++;
                fenzi += (item1UserRate - item1AvgRate) * (item2UserRate - item2AvgRate);

            }
            count1++;
            fenmu1 += (item1UserRate - item1AvgRate) * (item1UserRate - item1AvgRate);
        }

        for (Integer userId2 : userIdSet2) { //取所有对item2有过的评分
            double item2UserRate = item2Rate.itemUserRate.get(userId2);
            count2++;
            fenmu2 += (item2UserRate - item2AvgRate) * (item2UserRate - item2AvgRate);
        }

        if (fenmu1 == 0 && fenmu2 == 0) {
            if (item1AvgRate == item2AvgRate)
                return 0.0;
            else
                return 0.0;
        } else if (fenmu1 == 0 || fenmu2 == 0) {
            return 0.0;
        }
        double fenmu = Math.sqrt(fenmu1 / count1) * Math.sqrt(fenmu2 / count2);
        fenzi = fenzi / count;
        return fenzi / fenmu;
    }

    /**
     * 校正的similarity计算
     */
    public static double getSimDegreeChanged(ItemRateCollect collect, Integer itemId1, Integer itemId2) {
        double item1AvgRate = collect.itemCollect.get(itemId1).avgRate;
        double item2AvgRate = collect.itemCollect.get(itemId2).avgRate;
        double factorS = 1.0; //校正因子系数S
        int comUserCount = 0;
        int totalUserCount = 0;

        if (collect.itemCollect.containsKey(itemId1) == false || collect.itemCollect.containsKey(itemId2) == false) {
            return 0.0;
        }

        ItemRate item1Rate = collect.itemCollect.get(itemId1);
        ItemRate item2Rate = collect.itemCollect.get(itemId2);
        Set<Integer> userIdSet1 = item1Rate.itemUserRate.keySet();
        Set<Integer> userIdSet2 = item2Rate.itemUserRate.keySet();
        double fenzi = 0.0;
        double fenmu1 = 0.0;
        double fenmu2 = 0.0;

        for (Integer userId1 : userIdSet1) {
            totalUserCount++;
            double item1UserRate = item1Rate.itemUserRate.get(userId1);
            if (userIdSet2.contains(userId1)) {
                double item2UserRate = item2Rate.itemUserRate.get(userId1);
                fenzi += (item1UserRate - item1AvgRate) * (item2UserRate - item2AvgRate);
                comUserCount++;
            }
            fenmu1 += (item1UserRate - item1AvgRate) * (item1UserRate - item1AvgRate);
        }

        for (Integer userId2 : userIdSet2) {
            double item2UserRate = item2Rate.itemUserRate.get(userId2);
            if (userIdSet1.contains(userId2) == false) {
                totalUserCount++;
            }
            fenmu2 += (item2UserRate - item2AvgRate) * (item2UserRate - item2AvgRate);
        }

        factorS = 1.0 * (double) comUserCount / (double) totalUserCount;
        if (fenmu1 == 0 && fenmu2 == 0) {
            if (item1AvgRate == item2AvgRate)
                return factorS;
            else
                return 0.0;
        } else if (fenmu1 == 0 || fenmu2 == 0) {
            return 0.0;
        }
        double fenmu = Math.sqrt(fenmu1) * Math.sqrt(fenmu2);
        return factorS * fenzi / fenmu;
    }

}
