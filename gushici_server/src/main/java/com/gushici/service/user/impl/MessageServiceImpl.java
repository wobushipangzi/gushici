package com.gushici.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.common.utils.DateTimeUtils;
import com.gushici.service.user.MessageService;
import com.gushici.bean.user.Message;
import com.gushici.bean.user.Reply;
import com.gushici.bean.user.User;
import com.gushici.mapper.user.MessageMapper;
import com.gushici.mapper.suibi.ReplyMapper;
import com.gushici.mapper.user.UserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    private final UserMapper userMapper;

    private final ReplyMapper replyMapper;

    @Autowired
    public MessageServiceImpl(MessageMapper messageMapper, UserMapper userMapper, ReplyMapper replyMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.replyMapper = replyMapper;
    }

    /**
     * 消息通知落库
     * @param message
     * @return
     */
    @Override
    public synchronized Integer saveMessage(Message message) {
        return messageMapper.insert(message);
    }


    /**
     * 获取未读消息数
     * @return
     */
    @Override
    public Map<String, String> getMsgCount(String toOpenId) {
        HashMap<String, String> hashMap = new HashMap<>();

        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("to_open_id",toOpenId);
        messageQueryWrapper.eq("is_read","0");
        messageQueryWrapper.eq("message_type","praise");
        Integer praMsgCount = messageMapper.selectCount(messageQueryWrapper);
        hashMap.put("praise",String.valueOf(praMsgCount));

        QueryWrapper<Message> messageQueryWrapper2 = new QueryWrapper<>();
        messageQueryWrapper2.eq("to_open_id",toOpenId);
        messageQueryWrapper2.eq("is_read","0");
        messageQueryWrapper2.eq("message_type","comment");
        Integer comMsgCount = messageMapper.selectCount(messageQueryWrapper2);
        hashMap.put("comment",String.valueOf(comMsgCount));

        return hashMap;
    }


    /**
     * 获取被赞或者被评论消息列表
     * @param openId
     * @param messageType
     * List<Map<String,String>>
     */
    @Override
    public List<Map<String,String>> getMsgList(String openId, String messageType) {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("to_open_id",openId);
        messageQueryWrapper.eq("message_type",messageType).orderByDesc("mes_time");

        List<Message> messageList = messageMapper.selectList(messageQueryWrapper);
        for (Message message : messageList) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("open_id",message.getFromOpenId());
            User fromUser = userMapper.selectOne(userQueryWrapper);
            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("fromUserName",fromUser.getName());
            messageMap.put("headPhotoUrl",fromUser.getHeadPhotoUrl());
            messageMap.put("msgTime",DateTimeUtils.computeTime(message.getMesTime().getTime()));
            messageMap.put("isRead",message.getIsRead());

            //将消息标记为已读
            messageQueryWrapper.eq("is_read","0");
            message.setIsRead("1");
            messageMapper.update(message,messageQueryWrapper);
            if(StringUtils.isNotEmpty(message.getReplyId())){
                QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
                replyQueryWrapper.eq("reply_id",message.getReplyId());
                Reply reply = replyMapper.selectOne(replyQueryWrapper);
                if(null == reply){
                    messageMap.put("content","此评论已删除");
                }else {
                    messageMap.put("content",reply.getContent());
                }
            }
            list.add(messageMap);
        }

        return list;
    }


    /**
     * 清空消息列表
     * @param messageType  消息类型
     * @param openId   用户的openId
     */
    @Override
    public Integer delMessageList(String messageType, String openId) {
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("message_type",messageType);
        messageQueryWrapper.eq("to_open_id",openId);
        return messageMapper.delete(messageQueryWrapper);
    }
}
