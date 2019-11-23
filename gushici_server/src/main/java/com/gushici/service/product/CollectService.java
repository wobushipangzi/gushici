package com.gushici.service.product;


import com.gushici.bean.user.Collect;
import com.gushici.common.result.GlobalResult;

public interface CollectService {

    /**
     * 判断该作品是否被该用户收藏
     * @param productId  作品id
     * @param openId  用户openId
     * @return
     */
    boolean checkCollectProduct(String productId, String openId);

    /**
     * 判断该作者是否被该用户收藏
     * @param authorId  作者id
     * @param openId  用户openId
     * @return
     */
    boolean checkCollectAuthor(String authorId, String openId);

    /**
     * 保存收藏信息
     * @param collect
     */
    GlobalResult saveCollect(Collect collect);


    /**
     * 根据作者id或者作品id删除用户收藏
     * @param collect
     * @return
     */
    GlobalResult delCollect(Collect collect);

}
