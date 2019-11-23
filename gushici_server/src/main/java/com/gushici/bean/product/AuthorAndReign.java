package com.gushici.bean.product;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class AuthorAndReign extends Author implements Serializable {

    private static final long serialVersionUID = -8573829639030069589L;

    private String reignName;  //朝代名称

    public String getReignName() {
        return reignName;
    }

    public void setReignName(String reignName) {
        this.reignName = reignName;
    }

    @Override
    public String toString() {
        return "AuthorAndReign{" +
                "reignName='" + reignName + '\'' +
                '}';
    }
}
