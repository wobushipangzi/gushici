package com.gushici.bean.product;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@TableName("tb_class_two")
public class ClassTwo implements Serializable{

    private static final long serialVersionUID = -8333129578034597507L;

    private String catlogTwoId; //二级分类id

    private String catlogTwoName; //二级分类名称

    private String catlogOneId; //所属一级分类id

    public String getCatlogTwoId() {
        return catlogTwoId;
    }

    public void setCatlogTwoId(String catlogTwoId) {
        this.catlogTwoId = catlogTwoId;
    }

    public String getCatlogTwoName() {
        return catlogTwoName;
    }

    public void setCatlogTwoName(String catlogTwoName) {
        this.catlogTwoName = catlogTwoName;
    }

    public String getCatlogOneId() {
        return catlogOneId;
    }

    public void setCatlogOneId(String catlogOneId) {
        this.catlogOneId = catlogOneId;
    }

    @Override
    public String toString() {
        return "ClassTwo{" +
                "catlogTwoId=" + catlogTwoId +
                ", catlogTwoName='" + catlogTwoName + '\'' +
                ", catlogOneId=" + catlogOneId +
                '}';
    }
}
