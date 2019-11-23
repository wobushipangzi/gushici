package com.gushici.bean.user;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_feedback")
public class Feedback implements Serializable {

    private static final long serialVersionUID = -1273917394180906872L;

    private String openId;    //反馈的用户id

    private String content;   //反馈的内容

    private Date fbTime;   //反馈时间

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getFbTime() {
        return fbTime;
    }

    public void setFbTime(Date fbTime) {
        this.fbTime = fbTime;
    }


    @Override
    public String toString() {
        return "Feedback{" +
                "openId='" + openId + '\'' +
                ", content='" + content + '\'' +
                ", fbTime=" + fbTime +
                '}';
    }
}
