package com.gushici.bean.forum;


import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_suibi")
public class Suibi implements Serializable{

    private static final long serialVersionUID = -5636118813970707902L;

    private Integer suibiId;     //随笔id

    private String content;  //随笔内容

    private String openId;  //发表人的openId

    private Date suibiTime;  //发表随笔的时间

    private String imgUrl;  //配图

    private String isGood;  //是否加精

    private String praiseCount;  //被赞数

    private String commentCount;  //被评论数

    public Integer getSuibiId() {
        return suibiId;
    }

    public void setSuibiId(Integer suibiId) {
        this.suibiId = suibiId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getSuibiTime() {
        return suibiTime;
    }

    public void setSuibiTime(Date suibiTime) {
        this.suibiTime = suibiTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIsGood() {
        return isGood;
    }

    public void setIsGood(String isGood) {
        this.isGood = isGood;
    }

    public String getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(String praiseCount) {
        this.praiseCount = praiseCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "Suibi{" +
                "suibiId=" + suibiId +
                ", content='" + content + '\'' +
                ", openId='" + openId + '\'' +
                ", suibiTime=" + suibiTime +
                ", imgUrl='" + imgUrl + '\'' +
                ", isGood='" + isGood + '\'' +
                ", praiseCount='" + praiseCount + '\'' +
                ", commentCount='" + commentCount + '\'' +
                '}';
    }
}
