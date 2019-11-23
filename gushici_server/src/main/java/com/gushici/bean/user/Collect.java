package com.gushici.bean.user;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@TableName("tb_collect")
public class Collect implements Serializable {

    private static final long serialVersionUID = -5972023879553853948L;

    private String productId;  //作品id

    private String authorId;  //作者id

    private Integer collectType;   //收藏类型

    private String open_id;  //收藏人id

    private Date collectTime;  //收藏时间

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    @Override
    public String toString() {
        return "Collect{" +
                "productId='" + productId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", collectType=" + collectType +
                ", open_id='" + open_id + '\'' +
                ", collectTime=" + collectTime +
                '}';
    }
}
