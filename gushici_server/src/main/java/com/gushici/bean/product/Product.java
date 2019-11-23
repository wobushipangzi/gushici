package com.gushici.bean.product;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@TableName("tb_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 3836712412881085394L;

    private String productId;  //作品id

    private String reignId;   //作者朝代id

    private String authorId;  //作者id

    private String productName;  //作品名称

    private String productContent; //作品内容

    private String productAnnotation;  //作品注释

    private String productTranslate;  //作品翻译

    private String productEnjoy;  //作品赏析

    private String imgUrl;  //作品配图地址

    private String commentCount;   //评论数

    private String isCheck;  //是否校验

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getReignId() {
        return reignId;
    }

    public void setReignId(String reignId) {
        this.reignId = reignId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductContent() {
        return productContent;
    }

    public void setProductContent(String productContent) {
        this.productContent = productContent;
    }

    public String getProductAnnotation() {
        return productAnnotation;
    }

    public void setProductAnnotation(String productAnnotation) {
        this.productAnnotation = productAnnotation;
    }

    public String getProductTranslate() {
        return productTranslate;
    }

    public void setProductTranslate(String productTranslate) {
        this.productTranslate = productTranslate;
    }

    public String getProductEnjoy() {
        return productEnjoy;
    }

    public void setProductEnjoy(String productEnjoy) {
        this.productEnjoy = productEnjoy;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", reignId=" + reignId +
                ", authorId='" + authorId + '\'' +
                ", productName='" + productName + '\'' +
                ", productContent='" + productContent + '\'' +
                ", productAnnotation='" + productAnnotation + '\'' +
                ", productTranslate='" + productTranslate + '\'' +
                ", productEnjoy='" + productEnjoy + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", commentCount='" + commentCount + '\'' +
                ", isCheck='" + isCheck + '\'' +
                '}';
    }
}
