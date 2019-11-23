package com.gushici.bean.user;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_comment")
public class Comment implements Serializable{

    private static final long serialVersionUID = 816785836327041555L;

    private String commentId;   //发布的评论id

    private String topicId;   //被评论的主题id

    private String topicType;   //被评论的主题类型

    private String content;  //评论的内容

    private String fromOpenId;   //发表评论的用户openId

    private Date commentTime;  //发表评论的时间

    private String praiseCount;  //获赞数

    private String replyCount;  //回复数

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
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

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public String getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(String praiseCount) {
        this.praiseCount = praiseCount;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", topicId='" + topicId + '\'' +
                ", topicType='" + topicType + '\'' +
                ", content='" + content + '\'' +
                ", fromOpenId='" + fromOpenId + '\'' +
                ", commentTime=" + commentTime +
                ", praiseCount='" + praiseCount + '\'' +
                ", replyCount='" + replyCount + '\'' +
                '}';
    }
}
