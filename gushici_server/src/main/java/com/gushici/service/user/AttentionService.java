package com.gushici.service.user;

import java.util.List;
import java.util.Map;

public interface AttentionService {

    /**
     * 关注用户接口
     * @param attentionOpenId
     * @param fansOpenId
     */
    Integer addAttention(String attentionOpenId, String fansOpenId);


    /**
     * 获取粉丝数
     * @param openId
     */
    Integer getFansCount(String openId);


    /**
     * 获取总关注数
     * @param openId
     * @return
     */
    Integer getAttentionCount(String openId);


    /**
     * 我的关注列表
     * @param openId
     * @return
     */
    List<Map<String,String>> getAttentions(String openId);


    /**
     * 我的粉丝列表
     * @param openId
     * @return
     */
    List<Map<String,String>> getFans(String openId);
}
