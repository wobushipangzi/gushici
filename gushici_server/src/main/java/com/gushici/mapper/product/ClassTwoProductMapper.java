package com.gushici.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.product.ClassTwo;
import com.gushici.bean.product.ClassTwoProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassTwoProductMapper extends BaseMapper<ClassTwoProduct> {


    /**
     * 通过作品id查询对应的二级分类名称
     * @param productId  作品id
     * @return
     */
    List<ClassTwo> selectClassTwoByProductId(String productId);
}
