package com.gushici.bean.product;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class AllClass extends ClassTwo implements Serializable{

    private static final long serialVersionUID = 3010074812888720538L;

    private String catlogOneName;   //一级分类名称

    public String getCatlogOneName() {
        return catlogOneName;
    }

    public void setCatlogOneName(String catlogOneName) {
        this.catlogOneName = catlogOneName;
    }

    @Override
    public String toString() {
        return "AllClass{" +
                "catlogOneName='" + catlogOneName + '\'' +
                '}';
    }
}
