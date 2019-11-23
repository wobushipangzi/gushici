package com.gushici.bean.user;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_reply")
public class Reply implements Serializable {

    private static final long serialVersionUID = -8535108938123925068L;

    private String replyId;  //回复的评论id

    private String commentId;  //被评论的主评论id

    private String comRepId;  //被回复的评论id

    private String comRepType;   //被回复的类型

    private String content;   //回复内容

    private String fromOpenId;  //回复用户openId

    private String toOpenId;  //被回复用户openId

    private Date replyTime;  //回复评论的时间

    private String praiseCount;  //获赞数

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComRepId() {
        return comRepId;
    }

    public void setComRepId(String comRepId) {
        this.comRepId = comRepId;
    }

    public String getComRepType() {
        return comRepType;
    }

    public void setComRepType(String comRepType) {
        this.comRepType = comRepType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public String getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(String praiseCount) {
        this.praiseCount = praiseCount;
    }


    @Override
    public String toString() {
        return "Reply{" +
                "replyId='" + replyId + '\'' +
                ", commentId='" + commentId + '\'' +
                ", comRepId='" + comRepId + '\'' +
                ", comRepType='" + comRepType + '\'' +
                ", content='" + content + '\'' +
                ", fromOpenId='" + fromOpenId + '\'' +
                ", toOpenId='" + toOpenId + '\'' +
                ", replyTime=" + replyTime +
                ", praiseCount='" + praiseCount + '\'' +
                '}';
    }
}
