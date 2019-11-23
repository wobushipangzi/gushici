package com.gushici.bean.product;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@TableName("tb_class_two_product")
public class ClassTwoProduct implements Serializable{

    private static final long serialVersionUID = -533114122606872825L;

    private String catlogTwoId;  //二级分类id

    private String productId;  //作品id

    public String getCatlogTwoId() {
        return catlogTwoId;
    }

    public void setCatlogTwoId(String catlogTwoId) {
        this.catlogTwoId = catlogTwoId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "ClassTwoProduct{" +
                "catlogTwoId='" + catlogTwoId + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}
