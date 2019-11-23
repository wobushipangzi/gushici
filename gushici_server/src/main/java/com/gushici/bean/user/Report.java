package com.gushici.bean.user;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_report")
public class Report implements Serializable {

    private static final long serialVersionUID = 6205863714803028542L;

    private Integer reportId;  //举报主键id

    private String fromOpenId;  //举报人的openId

    private String toOpenId;  //被举报人的openId

    private String suibiId;  //被举报的随笔id

    private String commentId;  //被举报的评论id

    private String replyId;  //被举报的回复id

    private String description;  //违规描述

    private String classify;  //违规分类

    private String picture;  //举报截图

    private Date reportTime;  //举报时间

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", fromOpenId='" + fromOpenId + '\'' +
                ", toOpenId='" + toOpenId + '\'' +
                ", suibiId='" + suibiId + '\'' +
                ", commentId='" + commentId + '\'' +
                ", replyId='" + replyId + '\'' +
                ", description='" + description + '\'' +
                ", classify='" + classify + '\'' +
                ", picture='" + picture + '\'' +
                ", reportTime=" + reportTime +
                '}';
    }
}
