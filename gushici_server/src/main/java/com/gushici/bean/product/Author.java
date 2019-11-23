package com.gushici.bean.product;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@TableName("tb_author")
public class Author implements Serializable{

    private static final long serialVersionUID = 8439397378609048900L;

    private String authorId;  //作者id

    private String authorName;  //作者名字

    private String authorZi;  //作者的字

    private String authorHao;  //作者的号

    private String reignId;  //作者所在朝代

    private String authorIntroduce;  //作者简介

    private String authorUrl;  //作者图片

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorZi() {
        return authorZi;
    }

    public void setAuthorZi(String authorZi) {
        this.authorZi = authorZi;
    }

    public String getAuthorHao() {
        return authorHao;
    }

    public void setAuthorHao(String authorHao) {
        this.authorHao = authorHao;
    }

    public String getReignId() {
        return reignId;
    }

    public void setReignId(String reignId) {
        this.reignId = reignId;
    }

    public String getAuthorIntroduce() {
        return authorIntroduce;
    }

    public void setAuthorIntroduce(String authorIntroduce) {
        this.authorIntroduce = authorIntroduce;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", authorZi='" + authorZi + '\'' +
                ", authorHao='" + authorHao + '\'' +
                ", reignId=" + reignId +
                ", authorIntroduce='" + authorIntroduce + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                '}';
    }
}
