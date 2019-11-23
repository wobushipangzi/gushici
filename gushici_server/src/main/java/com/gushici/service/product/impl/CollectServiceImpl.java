package com.gushici.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.user.Collect;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.mapper.product.CollectMapper;
import com.gushici.service.product.CollectService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CollectServiceImpl implements CollectService {

    private static Logger logger = Logger.getLogger(CollectServiceImpl.class);

    private final CollectMapper collectMapper;

    @Autowired
    public CollectServiceImpl(CollectMapper collectMapper) {
        this.collectMapper = collectMapper;
    }

    /**
     * 判断该作品是否被该用户收藏
     * @param productId  作品id
     * @param openId  用户openId
     */
    @Override
    public boolean checkCollectProduct(String productId, String openId) {
        QueryWrapper<Collect> collectQueryWrapper = new QueryWrapper<>();
        collectQueryWrapper.eq("product_id",productId);
        collectQueryWrapper.eq("open_id",openId);
        Collect collect = collectMapper.selectOne(collectQueryWrapper);
        return null != collect;
    }

    /**
     * 判断该作者是否被该用户收藏
     * @param authorId  作者id
     * @param openId  用户openId
     */
    @Override
    public boolean checkCollectAuthor(String authorId, String openId) {
        QueryWrapper<Collect> collectQueryWrapper = new QueryWrapper<>();
        collectQueryWrapper.eq("author_id",authorId);
        collectQueryWrapper.eq("open_id",openId);
        Collect collect = collectMapper.selectOne(collectQueryWrapper);
        return null != collect;
    }


    /**
     * 保存收藏信息
     * @param collect
     */
    @Override
    public GlobalResult saveCollect(Collect collect) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            int insert = collectMapper.insert(collect);
            if(insert <= 0){
                globalResult.setCode(ResultCode.数据库新增数据失败.getCode());
                globalResult.setMessage(ResultCode.数据库新增数据失败.getMsg());
            }else {
                //如果保存成功，返回isSuccess为yes
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("isSuccess", "yes");
                globalResult.setData(dataMap);
            }
        }catch (Exception e){
            logger.error("保存收藏信息异常{}", e);
            globalResult.setCode(ResultCode.数据库新增数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库新增数据失败.getMsg());
        }
        return globalResult;
    }


    /**
     * 根据作者id或者作品id删除用户收藏
     * @param collect
     * @return
     */
    @Override
    public GlobalResult delCollect(Collect collect) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            QueryWrapper<Collect> collectQueryWrapper = new QueryWrapper<>();
            if(StringUtils.isNotEmpty(collect.getProductId())){
                collectQueryWrapper.eq("product_id",collect.getProductId());
            }

            if(StringUtils.isNotEmpty(collect.getAuthorId())){
                collectQueryWrapper.eq("author_id",collect.getAuthorId());
            }

            collectQueryWrapper.eq("open_id",collect.getOpen_id());
            int delete = collectMapper.delete(collectQueryWrapper);
            if(delete <= 0){
                logger.error("删除用户收藏失败");
                globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
                globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
            }else {
                //如果删除成功，返回isSuccess为yes
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("isSuccess", "yes");
                globalResult.setData(dataMap);
            }
        }catch (Exception e){
            logger.error("删除用户收藏异常{}", e);
            globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
        }
        return globalResult;
    }


}
