package com.gushici.service.product.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.product.ClassTwo;
import com.gushici.bean.product.Product;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.OnePageCount;
import com.gushici.common.utils.ParseWordUtils;
import com.gushici.service.product.ProductService;
import com.gushici.bean.product.ClassOne;
import com.gushici.bean.product.ProductAndReignAndAuthor;
import com.gushici.mapper.product.ClassTwoProductMapper;
import com.gushici.mapper.product.ProductMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProductServiceImpl implements ProductService {

    private static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;

    private final ClassTwoProductMapper classTwoProductMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ClassTwoProductMapper classTwoProductMapper) {
        this.productMapper = productMapper;
        this.classTwoProductMapper = classTwoProductMapper;
    }

    /**
     * 获取所有分类
     */
    @Override
    public GlobalResult getClassify() {
        GlobalResult globalResult = GlobalResult.success();
        try {
            ArrayList<Object> productContentList = new ArrayList<>();
            //获取一级分类
            List<ClassOne> classOnes = productMapper.selectCatlogOne();
            for (ClassOne classOne : classOnes) {
                Map<String, Object> classifyMap = new HashMap<>();
                String catlogOneName = classOne.getCatlogOneName();
                ArrayList<Object> classTwoMapList = new ArrayList<>();
                //通过一级分类id查询二级分类
                List<ClassTwo> classTwoList = productMapper.getcatlogTwoByOneId(classOne.getCatlogOneId());
                for (ClassTwo classTwo : classTwoList) {
                    HashMap<String, Object> classTwoMap = new HashMap<>();
                    classTwoMap.put("catlogTwoId",classTwo.getCatlogTwoId());
                    classTwoMap.put("catlogTwoName",classTwo.getCatlogTwoName());
                    classTwoMapList.add(classTwoMap);
                }
                classifyMap.put("products",classTwoMapList);
                classifyMap.put("catlogOneId",classOne.getCatlogOneId());
                classifyMap.put("catlogOneName",catlogOneName);
                productContentList.add(classifyMap);
            }
            globalResult.setData(productContentList);
        }catch (Exception e){
            logger.error("获取所有分类异常{}", e);
            globalResult.setCode(ResultCode.查询数据库失败.getCode());
            globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
        }
        return globalResult;
    }

    /**
     * 通过二级分类id获取作品列表
     * @param catlogTwoId 二级分类id
     * @param page 第几页
     */
    @Override
    public GlobalResult getProductByName(String catlogTwoId, String page) {

        GlobalResult globalResult = GlobalResult.success();
        if(StringUtils.isEmpty(page)){
            page = "0";
        }
        Integer indexCount = Integer.valueOf(page) * OnePageCount.GUSHICI_COUNT_ONE_PAGE;
        try {
            //通过二级分类id获取对应的作品列表
            List<ProductAndReignAndAuthor> products = productMapper.getProductAndReignAndAuthorListByCatlogTwoId(catlogTwoId, indexCount, OnePageCount.GUSHICI_COUNT_ONE_PAGE);
            List<Map<String,Object>> productList = new ArrayList<>();
            //通过作品id获取对应的作品详情
            if(null != products){
                for (ProductAndReignAndAuthor product : products) {
                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put("productId",product.getProductId());
                    productMap.put("productName",product.getProductName());
                    String productContent = product.getProductContent();
                    productContent = productContent.replace("\n","").replace("\r","").substring(0,15) + "...";
                    productMap.put("productContent",productContent);
                    productMap.put("reignName",product.getReignName());
                    productMap.put("authorName",product.getAuthorName());
                    productList.add(productMap);
                }
                globalResult.setData(productList);
            }
        }catch (Exception e){
            logger.error("通过二级分类id获取作品列表异常{}", e);
            globalResult.setCode(ResultCode.查询数据库失败.getCode());
            globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
        }
        return globalResult;
    }

    /**
     * 通过作品id查询作品详情
     * @param productId  作品id
     */
    @Override
    public Map<String,Object> getProductDetailsByProId(String productId) {
        Map<String, Object> productDetailMap = new HashMap<>();
        try {
            //通过作品id查询作品详情
            ProductAndReignAndAuthor productDetail = productMapper.getProductDetailByProductId(productId);
            if(null != productDetail){
                List<Map<String, String>> classTwoProductList = new ArrayList<>();
                //通过作品id查询对应的二级分类名称
                List<ClassTwo> classTwoProducts = classTwoProductMapper.selectClassTwoByProductId(productId);
                if(null != classTwoProducts){
                    for (ClassTwo classTwoProduct : classTwoProducts) {
                        HashMap<String, String> catlogTwoNameMap = new HashMap<>();
                        catlogTwoNameMap.put("catlogTwoName", classTwoProduct.getCatlogTwoName());
                        catlogTwoNameMap.put("catlogTwoId", classTwoProduct.getCatlogTwoId());
                        classTwoProductList.add(catlogTwoNameMap);
                    }
                }
                //组装参数
                productDetailMap.put("productId",productDetail.getProductId());
                productDetailMap.put("productName",productDetail.getProductName());
                productDetailMap.put("productContent",productDetail.getProductContent());
                productDetailMap.put("productAnnotation",productDetail.getProductAnnotation());
                productDetailMap.put("productTranslate",productDetail.getProductTranslate());
                productDetailMap.put("productEnjoy",productDetail.getProductEnjoy());
                productDetailMap.put("imgUrl",productDetail.getImgUrl());
                productDetailMap.put("authorId",productDetail.getAuthorId());
                productDetailMap.put("authorName",productDetail.getAuthorName());
                productDetailMap.put("authorIntroduce",productDetail.getAuthorIntroduce());
                productDetailMap.put("reignName",productDetail.getReignName());
                productDetailMap.put("catlogTwoNames",classTwoProductList);
                productDetailMap.put("commentCount",StringUtils.isNotEmpty(productDetail.getCommentCount())? Integer.valueOf(productDetail.getCommentCount()) : 0);    //评论数
            }
        }catch (Exception e){
            logger.error("通过作品id查询作品详情接口异常{}", e);
        }
        return productDetailMap;
    }


    /**
     * 搜索作品功能
     * @param productName  搜索的关键字
     * @return
     */
    @Override
    public GlobalResult searchProduct(String productName, String page) {

        GlobalResult globalResult = GlobalResult.success();
        try {
            if(StringUtils.isEmpty(page)){
                page = "0";
            }

            Integer indexCount = Integer.valueOf(page) * OnePageCount.GUSHICI_COUNT_ONE_PAGE;

            ArrayList<Object> productList = new ArrayList<>();
            List<ProductAndReignAndAuthor> products = productMapper.selectProductAndReignAndAuthorList(productName, indexCount, OnePageCount.GUSHICI_COUNT_ONE_PAGE);
            if(null != products){
                for (ProductAndReignAndAuthor product : products) {
                    HashMap<String, String> productMap = new HashMap<>();
                    productMap.put("productId",product.getProductId());
                    productMap.put("productName",product.getProductName());
                    String productContent = product.getProductContent();

                    //如果查到的是诗词的题目 直接把诗句截取15个字
                    if(product.getProductName().contains(productName)){
                        productContent = productContent.replace("\n","").replace("\r","").substring(0,15);
                    }else {
                        //如果查到的是内容，对内容进行截取
                        String[] productContents;
                        if(productContent.contains("。")){
                            productContents = productContent.split("。");
                            for (String productCon : productContents) {
                                if(productCon.contains(productName)){
                                    productContent = productCon.replace("\n","").replace("\r","");
                                }
                            }
                        }

                        if(productContent.contains("？")){
                            productContents = productContent.split("？");
                            for (String productCon : productContents) {
                                if(productCon.contains(productName)){
                                    productContent = productCon.replace("\n","").replace("\r","");
                                }
                            }
                        }

                        if(productContent.contains("！")){
                            productContents = productContent.split("！");
                            for (String productCon : productContents) {
                                if(productCon.contains(productName)){
                                    productContent = productCon.replace("\n","").replace("\r","");
                                }
                            }
                        }
                    }

                    productMap.put("productContent",productContent + "...");
                    productMap.put("reignName",product.getReignName());
                    productMap.put("authorName",product.getAuthorName());
                    productList.add(productMap);
                }
                globalResult.setData(productList);
            }
        }catch (Exception e){
            logger.error("搜索作品功能接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 通过图片解析到的文字查询相关作品
     * @param text   图片解析出来的文字
     * @return
     */
    @Override
    public GlobalResult getProductsByPhoto(String text, String page) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            Map textMap = JSONObject.parseObject(text, Map.class);
            List wordsResultList = JSONObject.parseObject(textMap.get("words_result").toString(), List.class);

            ArrayList<String> wordList = new ArrayList<>();

            for (Object wordsResult : wordsResultList) {
                Map wordsResultMap = JSONObject.parseObject(wordsResult.toString(), Map.class);
                String words = wordsResultMap.get("words").toString();

                if(wordsResultList.size() > 1 && words.toCharArray().length < 3){
                    continue;
                }

                //解析文字
                words = ParseWordUtils.parseWord(words);
                if(wordsResultList.size() > 1 && words.toCharArray().length < 3){
                    continue;
                }
                wordList.add(words);
            }

            Set<Object> searchProductSet = new HashSet<>();
            Set<String> productIdSet = new HashSet<>();

            CountDownLatch countDownLatch = new CountDownLatch(wordList.size());
            //去作品库开启多线程查询
            for (String word : wordList) {
                new Thread(() -> {
                    GlobalResult result = searchProduct(word, page);
                    if(ResultCode.处理成功.getCode().equals(result.getCode())){
                        List searchProduct = JSONObject.parseObject(JSONObject.toJSONString(result.getData()),List.class);
                        if(null != searchProduct){
                            for (Object o : searchProduct) {
                                Map productMap = JSONObject.parseObject(JSONObject.toJSONString(o), Map.class);
                                productIdSet.add(String.valueOf(productMap.get("productId")));
                                searchProductSet.add(productMap);
                            }
                        }
                    }
                    countDownLatch.countDown();
                }).start();
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                logger.error("拍照识诗词方法线程挂起异常==>{}", e);
                globalResult = GlobalResult.error();
            }

            Set<Object> allProductList = new HashSet<>();
            for (String productId : productIdSet) {
                for (Object proMap : searchProductSet) {
                    Map productMap = JSONObject.parseObject(JSONObject.toJSONString(proMap), Map.class);
                    if(productId.equals(productMap.get("productId").toString())){
                        allProductList.add(productMap);
                        break;
                    }
                }
            }
            globalResult.setData(allProductList);
        }catch (Exception e){
            logger.error("通过图片解析到的文字查询相关作品接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 相关古诗词的评论数加1
     * @param topicId  作品id
     */
    @Override
    public Integer addCommentCount(String topicId) {
        String commentCountStr = productMapper.selectcommentCountByProductId(topicId);
        Integer commentCount;
        if(StringUtils.isEmpty(commentCountStr)){
            AtomicInteger atomicInteger = new AtomicInteger(0);
            atomicInteger.getAndAdd(1);
            commentCount = atomicInteger.get();
        }else {
            AtomicInteger atomicInteger = new AtomicInteger(Integer.valueOf(commentCountStr));
            atomicInteger.getAndAdd(1);
            commentCount = atomicInteger.get();
        }
        QueryWrapper<Product> productUpdate = new QueryWrapper<>();
        Product product = new Product();
        productUpdate.eq("product_id",topicId);
        product.setCommentCount(String.valueOf(commentCount));
        return productMapper.update(product,productUpdate);
    }


}
