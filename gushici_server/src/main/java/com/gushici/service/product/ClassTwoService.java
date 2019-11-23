package com.gushici.service.product;

import com.gushici.common.result.GlobalResult;

import java.util.List;

public interface ClassTwoService {


    /**
     * 根据一级分类获取二级分类的集合
     * @param catlogOneId
     */
    GlobalResult getClassTwoList(String catlogOneId);
}
