package com.gushici.service.product;

import com.gushici.common.result.GlobalResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface ProductService {

    /**
     * 获取所有分类
     */
    GlobalResult getClassify();

    /**
     * 通过二级分类名称获取作品
     * @param catlogTwoName 二级分类名称
     * @return
     */
    GlobalResult getProductByName(String catlogTwoName, String page);

    /**
     * 通过作品id查询作品详情
     * @param productId  作品id
     * @return
     */
    Map<String,Object> getProductDetailsByProId(String productId);


    /**
     * 搜索作品功能
     * @param productName
     * @return
     */
    GlobalResult searchProduct(String productName, String page);


    /**
     * 通过图片解析到的文字查询相关作品
     * @param text   图片解析出来的文字
     * @return
     */
    GlobalResult getProductsByPhoto(String text, String page);


    /**
     * 相关古诗词的评论数加1
     * @param topicId  作品id
     */
    Integer addCommentCount(String topicId);
}
