package com.gushici.bean.product;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ProductAndReignAndAuthor extends Product implements Serializable {

    private static final long serialVersionUID = -7273611129149300039L;

    private String authorName;   //作者名字

    private String reignName;   //朝代名称

    private String authorIntroduce;   //作者简介

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getReignName() {
        return reignName;
    }

    public void setReignName(String reignName) {
        this.reignName = reignName;
    }

    public String getAuthorIntroduce() {
        return authorIntroduce;
    }

    public void setAuthorIntroduce(String authorIntroduce) {
        this.authorIntroduce = authorIntroduce;
    }

    @Override
    public String toString() {
        return "ProductAndReignAndAuthor{" +
                "authorName='" + authorName + '\'' +
                ", reignName='" + reignName + '\'' +
                ", authorIntroduce='" + authorIntroduce + '\'' +
                '}';
    }
}
