package com.gushici.bean.user;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_praise")
public class Praise implements Serializable {

    private static final long serialVersionUID = 2040039634376659564L;

    private String suibiId;   //被点赞的随笔id

    private String commentId;   //被点赞的评论id

    private String replyId;  //被点赞的回复id

    private String praiseOpenId;  //点赞人的openId

    private Date praiseTime;   //点赞时的时间戳

    public String getSuibiId() {
        return suibiId;
    }

    public void setSuibiId(String suibiId) {
        this.suibiId = suibiId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getPraiseOpenId() {
        return praiseOpenId;
    }

    public void setPraiseOpenId(String praiseOpenId) {
        this.praiseOpenId = praiseOpenId;
    }

    public Date getPraiseTime() {
        return praiseTime;
    }

    public void setPraiseTime(Date praiseTime) {
        this.praiseTime = praiseTime;
    }

    @Override
    public String toString() {
        return "Praise{" +
                "suibiId='" + suibiId + '\'' +
                ", commentId='" + commentId + '\'' +
                ", replyId='" + replyId + '\'' +
                ", praiseOpenId='" + praiseOpenId + '\'' +
                ", praiseTime=" + praiseTime +
                '}';
    }
}
