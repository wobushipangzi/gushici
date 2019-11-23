package com.gushici.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.product.ClassTwo;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.controller.product.ProductController;
import com.gushici.mapper.product.ClassTwoMapper;
import com.gushici.service.product.ClassTwoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ClassTwoServiceImpl implements ClassTwoService {

    private static Logger logger = Logger.getLogger(ProductController.class);

    @Autowired
    private ClassTwoMapper classTwoMapper;


    /**
     * 根据一级分类获取二级分类的集合
     * @param catlogOneId
     */
    @Override
    public GlobalResult getClassTwoList(String catlogOneId) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            ArrayList<Object> classTwoList = new ArrayList<>();
            QueryWrapper<ClassTwo> classTwoQueryWrapper = new QueryWrapper<>();
            classTwoQueryWrapper.eq("catlog_one_id",catlogOneId);
            List<ClassTwo> classTwos = classTwoMapper.selectList(classTwoQueryWrapper);
            if(null != classTwos){
                for (ClassTwo classTwo : classTwos) {
                    HashMap<String, String> classTwoMap = new HashMap<>();
                    classTwoMap.put("catlogTwoName",classTwo.getCatlogTwoName());
                    classTwoMap.put("catlogTwoId",classTwo.getCatlogTwoId());
                    classTwoList.add(classTwoMap);
                }
                globalResult.setData(classTwoList);
            }
        }catch (Exception e){
            logger.error("根据一级分类获取二级分类的集合查询数据库失败{}",e);
            globalResult.setCode(ResultCode.查询数据库失败.getCode());
            globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
            throw new GlobalException(ResultCode.查询数据库失败.getCode(),ResultCode.查询数据库失败.getMsg());
        }
        return globalResult;
    }
}
