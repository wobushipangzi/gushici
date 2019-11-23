package com.gushici.service.user.impl;

import com.gushici.bean.user.Feedback;
import com.gushici.mapper.user.FeedbackMapper;
import com.gushici.service.user.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Autowired
    public FeedbackServiceImpl(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }


    /**
     * 保存用户反馈
     * @param openId
     * @param content
     * @return
     */
    @Override
    public Integer saveFeedback(String openId, String content) {
        Feedback feedback = new Feedback();
        feedback.setOpenId(openId);
        feedback.setContent(content);
        feedback.setFbTime(new Date());
        return feedbackMapper.insert(feedback);
    }
}
