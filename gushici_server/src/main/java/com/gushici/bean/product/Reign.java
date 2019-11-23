package com.gushici.bean.product;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@TableName("tb_reign")
public class Reign implements Serializable{

    private static final long serialVersionUID = 807658915937813989L;

    private String reignId;  //朝代id

    private String reignName; //朝代名称

    private String reignIntroduce; //朝代简介

    public String getReignId() {
        return reignId;
    }

    public void setReignId(String reignId) {
        this.reignId = reignId;
    }

    public String getReignName() {
        return reignName;
    }

    public void setReignName(String reignName) {
        this.reignName = reignName;
    }

    public String getReignIntroduce() {
        return reignIntroduce;
    }

    public void setReignIntroduce(String reignIntroduce) {
        this.reignIntroduce = reignIntroduce;
    }

    @Override
    public String toString() {
        return "Reign{" +
                "reignId=" + reignId +
                ", reignName='" + reignName + '\'' +
                ", reignIntroduce='" + reignIntroduce + '\'' +
                '}';
    }
}
