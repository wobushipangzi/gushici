package com.gushici.bean.user;


import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_attention")
public class Attention implements Serializable{

    private static final long serialVersionUID = 5619851085651501164L;

    private String attentionOpenId;  //被关注人的openId

    private String fansOpenId;   //关注人的openId

    private Date attentionTime;  //关注的时间

    public String getAttentionOpenId() {
        return attentionOpenId;
    }

    public void setAttentionOpenId(String attentionOpenId) {
        this.attentionOpenId = attentionOpenId;
    }

    public String getFansOpenId() {
        return fansOpenId;
    }

    public void setFansOpenId(String fansOpenId) {
        this.fansOpenId = fansOpenId;
    }

    public Date getAttentionTime() {
        return attentionTime;
    }

    public void setAttentionTime(Date attentionTime) {
        this.attentionTime = attentionTime;
    }

    @Override
    public String toString() {
        return "Attention{" +
                "attentionOpenId='" + attentionOpenId + '\'' +
                ", fansOpenId='" + fansOpenId + '\'' +
                ", attentionTime=" + attentionTime +
                '}';
    }
}
