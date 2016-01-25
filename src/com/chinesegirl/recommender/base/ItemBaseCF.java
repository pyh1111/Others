package com.chinesegirl.recommender.base;

import com.chinesegirl.recommender.entity.ItemRateCollect;
import com.chinesegirl.recommender.entity.ItemRateCollect.ItemRate;
import com.chinesegirl.recommender.entity.SimItem;
import com.chinesegirl.recommender.entity.UserRateCollect;
import com.chinesegirl.recommender.util.ItemSimDegree;
import com.chinesegirl.recommender.util.RecomMethodInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * @author Shaq
 * @version 0.1
 * @filename ItemBaseCFChanged.java
 * @note ��дpredictionUserItemRate�ӿڷ���, ��ΪItem-Based��д
 * @since 2015-05-20
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */
public class ItemBaseCF implements RecomMethodInterface {
    //ע��������item-Based,��topK item! ������topK�ھ��û�(����user-Based)!
    public int topKCount = 30; // �û���ϲ����topK item"��Ʒ"����

    public ItemBaseCF(int topKCount) {
        this.topKCount = topKCount;
    }

    /**
     * ��дpredictionUserItemRate�ӿڷ���
     */
    @Override
    public double predictionUserItemRate(UserRateCollect trainUserData, ItemRateCollect collect, Integer userId, Integer itemId) {
        ItemRate itemRate = collect.itemCollect.get(itemId); //��ȡ��itemId������ֵ��
        if (itemRate == null) { //û��user��Ŀ��item��������,��ȡĿ��user�������������ֵ�ƽ��ֵΪĿ��user��Ŀ��item������
            if (trainUserData.userCollect.containsKey(userId))
                return trainUserData.userCollect.get(userId).avgRate;
            else
                return 3.0; //��Ŀ��userδ�����κ�����,������3.0
        }
        if (itemRate.itemUserRate.containsKey(userId)) {
            return itemRate.itemUserRate.get(userId);
        }

        double fenzi = 0.0;
        double fenmu = 0.0;
        List<SimItem> similarItemList = getSimilarItemsFromTopKUser(collect, itemId, userId);
        for (SimItem simItem : similarItemList) {
            double similarItemAvgRate = collect.itemCollect.get(simItem.itemId).avgRate;
            double similarUserItemRate = collect.itemCollect.get(simItem.itemId).itemUserRate.get(userId);
            fenzi += simItem.similarDegree * (similarUserItemRate - similarItemAvgRate);
            fenmu += Math.abs(simItem.similarDegree);
        }
        if (similarItemList == null || similarItemList.size() == 0) {
            return collect.itemCollect.get(itemId).avgRate;
        }
        if (fenmu == 0) {
            return collect.itemCollect.get(itemId).avgRate;
        }
        return collect.itemCollect.get(itemId).avgRate + (fenzi / fenmu);
    }

    /**
     * �õ�����SimilarItem�б� --from TopK�����û�
     */
    public List<SimItem> getSimilarItemsFromTopKUser(ItemRateCollect collect, Integer itemId, Integer userId) {
        List<SimItem> items = new ArrayList<SimItem>(); //�������item�ļ���
        SimItem[] topKSimilarItem = new SimItem[topKCount]; //���k������item�ļ���
        int neighbourCount = 0;
        Set<Integer> itemSet = collect.itemCollect.keySet(); //��ȡ����itemId
        for (Integer eachItem : itemSet) {
            //Ŀ��item�����ÿһ��item�����ֶ�����
            if (eachItem.equals(itemId) == false && collect.itemCollect.get(eachItem).itemUserRate.containsKey(userId)) {
                double itemSimDegree = ItemSimDegree.getSimDegree(collect, itemId, eachItem);
                if (neighbourCount < topKCount) {
                    insertTopKArray(topKSimilarItem, eachItem, itemSimDegree, neighbourCount);
                    neighbourCount++;
                } else {
                    insertTopKArray(topKSimilarItem, eachItem, itemSimDegree, topKSimilarItem.length);
                }
            }
        }
        for (int i = 0; i < neighbourCount; i++) {
            items.add(topKSimilarItem[i]);
        }
        return items;
    }

    private static void insertTopKArray(SimItem[] topKSimilarItem, Integer itemId, Double simDegree, int nearestCount) {
        int nearestMaxIndex = 0;
        if (nearestCount < topKSimilarItem.length) {
            nearestMaxIndex = nearestCount;
        } else {
            if (topKSimilarItem[topKSimilarItem.length - 1].similarDegree >= simDegree)
                return;
            nearestMaxIndex = topKSimilarItem.length - 1;
        }
        SimItem oneSimilarItem = new SimItem();
        oneSimilarItem.itemId = itemId;
        oneSimilarItem.similarDegree = simDegree;
        topKSimilarItem[nearestMaxIndex] = oneSimilarItem;
        for (int i = nearestMaxIndex; i > 0; i--) { //bubble sort...
            if (topKSimilarItem[i].similarDegree > topKSimilarItem[i - 1].similarDegree) {
                SimItem tempSimilarItem = topKSimilarItem[i];
                topKSimilarItem[i] = topKSimilarItem[i - 1];
                topKSimilarItem[i - 1] = tempSimilarItem;
            }
        }
    }

}
