package com.gushici.service.user;

import com.gushici.bean.user.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {

    /**
     * 消息通知落库
     * @param message
     * @return
     */
    Integer saveMessage(Message message);

    /**
     * 获取未读消息数及类型
     * @return
     */
    Map<String, String> getMsgCount(String toOpenId);

    /**
     * 获取被赞或者被评论消息列表
     * @param openId
     * @param messageType
     */
    List<Map<String,String>> getMsgList(String openId, String messageType);


    /**
     * 清空消息列表
     * @param messageType  消息类型
     * @param openId   用户的openId
     */
    Integer delMessageList(String messageType, String openId);
}
