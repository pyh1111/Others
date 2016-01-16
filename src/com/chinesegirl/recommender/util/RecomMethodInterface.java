package com.chinesegirl.recommender.util;

import com.chinesegirl.recommender.entity.ItemRateCollect;
import com.chinesegirl.recommender.entity.UserRateCollect;

public interface RecomMethodInterface {
    public double predictionUserItemRate(UserRateCollect trainUserData,
                                         ItemRateCollect trainItemData, Integer userId, Integer itemId);

}
