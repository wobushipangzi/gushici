package com.gushici.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.OnePageCount;
import com.gushici.common.utils.ParseWordUtils;
import com.gushici.bean.product.Author;
import com.gushici.bean.product.AuthorAndReign;
import com.gushici.bean.product.Product;
import com.gushici.bean.product.Reign;
import com.gushici.mapper.product.AuthorMapper;
import com.gushici.mapper.product.ProductMapper;
import com.gushici.mapper.product.ReignMapper;
import com.gushici.service.product.AuthorService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorMapper authorMapper;

    private final ProductMapper productMapper;

    private final ReignMapper reignMapper;

    @Autowired
    public AuthorServiceImpl(AuthorMapper authorMapper, ProductMapper productMapper, ReignMapper reignMapper) {
        this.authorMapper = authorMapper;
        this.productMapper = productMapper;
        this.reignMapper = reignMapper;
    }

    /**
     * 查询所有的朝代以及对应的作者
     */
    @Override
    public GlobalResult getReignAndAuthor() {
        GlobalResult globalResult = GlobalResult.success();
        try {
            ArrayList<Map<String, Object>> reignContentList = new ArrayList<>();

            //查询所有的朝代
            List<Reign> reignList = authorMapper.getReigns();

            for (Reign reign : reignList) {
                Map<String,Object> reignAndAuthMap = new LinkedHashMap<>();
                //通过朝代id查询作者信息
                List<Author> authorDetailList = authorMapper.getAuthorByReignId(reign.getReignId());
                //只展示有作者的朝代
                if(null == authorDetailList || authorDetailList.size() == 0){
                    continue;
                }
                List<Map<String,String>> authorList = new ArrayList<>();
                for (Author author : authorDetailList) {
                    HashMap<String, String> authorMap = new HashMap<>();
                    authorMap.put("authorName", author.getAuthorName());
                    authorMap.put("authorId", String.valueOf(author.getAuthorId()));
                    authorList.add(authorMap);
                }
                reignAndAuthMap.put("reignId",reign.getReignId());
                reignAndAuthMap.put("reignName",reign.getReignName());
                reignAndAuthMap.put("authors",authorList);
                reignContentList.add(reignAndAuthMap);
            }
            globalResult.setData(reignContentList);
        }catch (Exception e){
            logger.error("查询所有的朝代以及对应的作者异常：{}" , e);
            globalResult.setCode(ResultCode.查询数据库失败.getCode());
            globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
        }
        return globalResult;
    }

    /**
     * 通过作者id查询作者详情以及对应作品
     * @param authorId
     * @return
     */
    @Override
    public HashMap<Object, Object> getAuthAndProsByAuthId(String authorId, String page) {
        HashMap<Object, Object> authAndProsMap = new HashMap<>();
        //查询作者信息
        AuthorAndReign authorDetails = authorMapper.getAuthorDetailsByAuthId(authorId);

        HashMap<String, Object> authorTeailMap = new HashMap<>();
        authorTeailMap.put("authorName",authorDetails.getAuthorName());
        authorTeailMap.put("authorIntroduce",authorDetails.getAuthorIntroduce());
        authorTeailMap.put("authorUrl",authorDetails.getAuthorUrl());
        authorTeailMap.put("reignName",authorDetails.getReignName());
        authAndProsMap.put("authDetails",authorTeailMap);

        ArrayList<Map<String, Object>> productListMap = new ArrayList<>();

        if(StringUtils.isEmpty(page)){
            page = "0";
        }
        Integer indexCount = Integer.valueOf(page) * OnePageCount.GUSHICI_COUNT_ONE_PAGE;
        //根据作者id查询对应作品
        List<Product> productList = productMapper.getProductByAuthId(authorId, indexCount, OnePageCount.GUSHICI_COUNT_ONE_PAGE);

        //作品数量
        Integer productCount = productMapper.selectProductCountByAuthorId(authorId);

        for (Product product : productList) {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("productId",product.getProductId());
            productMap.put("productName",product.getProductName());
            String productContent;
            if(product.getProductContent().toCharArray().length > 15){
                productContent = product.getProductContent().replace("\n","").replace("\r","").substring(0,15) + "...";
            }else {
                productContent = product.getProductContent().replace("\n","").replace("\r","");
            }

            productMap.put("productContent",productContent);
            productListMap.add(productMap);
        }
        authAndProsMap.put("productCount",productCount);
        authAndProsMap.put("productList",productListMap);
        return authAndProsMap;
    }


    /**
     * 搜索作者
     */
    @Override
    public GlobalResult searchAuthor(String authorName, String page) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            if(StringUtils.isEmpty(page)){
                page = "0";
            }
            Integer indexCount = Integer.valueOf(page) * OnePageCount.GUSHICI_COUNT_ONE_PAGE;
            authorName = ParseWordUtils.parseWord(authorName);

            List<AuthorAndReign> authorList = authorMapper.selectAuthorAndReignList(authorName, indexCount, OnePageCount.GUSHICI_COUNT_ONE_PAGE);
            List<Object> authors = new ArrayList<>();
            if(null != authorList){
                for (AuthorAndReign author : authorList) {
                    HashMap<String, String> authorMap = new HashMap<>();
                    authorMap.put("authorId",author.getAuthorId());
                    authorMap.put("authorName",author.getAuthorName());
                    authorMap.put("authorZi", StringUtils.isEmpty(author.getAuthorZi())? "" : ("字" + author.getAuthorZi()));
                    authorMap.put("authorHao",StringUtils.isEmpty(author.getAuthorHao())? "" : ("号" + author.getAuthorHao()));
                    authorMap.put("reignName",author.getReignName());
                    authors.add(authorMap);
                }
                globalResult.setData(authors);
            }
        }catch (Exception e){
            logger.error("搜索作者接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 根据朝代id获取对应的所有作者集合
     * @param reignId  朝代id
     * @return
     */
    @Override
    public GlobalResult getAuthorList(String reignId) {

        GlobalResult globalResult = GlobalResult.success();
        try {
            ArrayList<Object> authors = new ArrayList<>();
            QueryWrapper<Author> authorQueryWrapper = new QueryWrapper<>();
            authorQueryWrapper.select("author_id","author_name");
            authorQueryWrapper.eq("reign_id",reignId);
            List<Author> authorList = authorMapper.selectList(authorQueryWrapper);
            if(null != authorList){
                for (Author author : authorList) {
                    HashMap<String, String> authorMap = new HashMap<>();
                    authorMap.put("authorId", String.valueOf(author.getAuthorId()));
                    authorMap.put("authorName", author.getAuthorName());
                    authors.add(authorMap);
                }
                globalResult.setData(authors);
            }
        }catch (Exception e){
            logger.error("根据朝代id获取对应的所有作者集合异常{}", e);
            globalResult.setCode(ResultCode.查询数据库失败.getCode());
            globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
        }
        return globalResult;
    }
}
