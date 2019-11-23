package com.gushici.bean.product;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@TableName("tb_class_one")
public class ClassOne implements Serializable{

    private static final long serialVersionUID = 8533054630111612827L;

    private Integer catlogOneId; //一级分类id

    private String catlogOneName; //一级分类名称

    public Integer getCatlogOneId() {
        return catlogOneId;
    }

    public void setCatlogOneId(Integer catlogOneId) {
        this.catlogOneId = catlogOneId;
    }

    public String getCatlogOneName() {
        return catlogOneName;
    }

    public void setCatlogOneName(String catlogOneName) {
        this.catlogOneName = catlogOneName;
    }

    @Override
    public String toString() {
        return "ClassOne{" +
                "catlogOneId=" + catlogOneId +
                ", catlogOneName='" + catlogOneName + '\'' +
                '}';
    }
}
