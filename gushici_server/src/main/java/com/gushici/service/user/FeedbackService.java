package com.gushici.service.user;

public interface FeedbackService {


    /**
     * 保存用户反馈
     * @param openId
     * @param content
     * @return
     */
    Integer saveFeedback(String openId, String content);
}
