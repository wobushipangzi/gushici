package com.gushici.bean.user;


import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_message")
public class Message implements Serializable{

    private static final long serialVersionUID = -6616273805850108763L;

    private String messageType;  //消息类型

    private String fromOpenId;   //发起人的openId

    private String toOpenId;   //接收人的openId

    private String replyId;   //评论人的评论id

    private String comRepPraId;  //发起的id

    private String comRepPraType;  //发起的类型

    private String isRead;   //是否已读  1：是  0：否

    private Date mesTime;  //消息时间

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFromOpenId() {
        return fromOpenId;
    }

    public void setFromOpenId(String fromOpenId) {
        this.fromOpenId = fromOpenId;
    }

    public String getToOpenId() {
        return toOpenId;
    }

    public void setToOpenId(String toOpenId) {
        this.toOpenId = toOpenId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getComRepPraId() {
        return comRepPraId;
    }

    public void setComRepPraId(String comRepPraId) {
        this.comRepPraId = comRepPraId;
    }

    public String getComRepPraType() {
        return comRepPraType;
    }

    public void setComRepPraType(String comRepPraType) {
        this.comRepPraType = comRepPraType;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Date getMesTime() {
        return mesTime;
    }

    public void setMesTime(Date mesTime) {
        this.mesTime = mesTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType='" + messageType + '\'' +
                ", fromOpenId='" + fromOpenId + '\'' +
                ", toOpenId='" + toOpenId + '\'' +
                ", replyId='" + replyId + '\'' +
                ", comRepPraId='" + comRepPraId + '\'' +
                ", comRepPraType='" + comRepPraType + '\'' +
                ", isRead='" + isRead + '\'' +
                ", mesTime=" + mesTime +
                '}';
    }
}
