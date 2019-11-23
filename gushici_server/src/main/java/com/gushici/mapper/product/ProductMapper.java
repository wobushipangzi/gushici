package com.gushici.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.product.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product>{

    /**
     * 查询所有一级分类id
     */
    List<ClassOne> selectCatlogOne();

    /**
     * 查询所有分类
     */
    List<AllClass> selectAllClass();

    /**
     * 通过一级分类id  查询二级分类
     */
    List<ClassTwo> getcatlogTwoByOneId(Integer catlogOneId);

    /**
     * 通过二级分类id获取对应作品id
     * @param catlogTwoId  二级分类id
     * @return
     */
    List<String> getProductIdByCatlogTwoId(String catlogTwoId);

    /**
     * 通过作品id查询所对应的作品详情
     * @param productId 作品id
     * @return
     */
    ProductAndReignAndAuthor getProductDetailByProductId(String productId);


    /**
     * 通过作者id获取对应作品
     * @param authorId 作者id
     * @return
     */
    List<Product> getProductByAuthId(@Param("authorId") String authorId,
                                     @Param("indexCount") Integer indexCount,
                                     @Param("countInPage") Integer countInPage);


    /**
     * 通过二级分类id查询对应所有作品
     * @param catlogTwoId 二级分类id
     * @return
     */
    List<ProductAndReignAndAuthor> getProductAndReignAndAuthorListByCatlogTwoId(@Param("catlogTwoId") String catlogTwoId,
                                                                                @Param("indexCount") Integer indexCount,
                                                                                @Param("countInPage") Integer countInPage );


    /**
     *   根据关键字搜索作品
     * @param productName 搜索作者关键字的名称
     * @return
     */
    List<ProductAndReignAndAuthor> selectProductAndReignAndAuthorList(@Param("productName") String productName,
                                                                      @Param("indexCount") Integer indexCount,
                                                                      @Param("countInPage") Integer countInPage);

    /**
     * 通过作者id  查询作品数量
     * @param authorId
     * @return
     */
    Integer selectProductCountByAuthorId(String authorId);

    /**
     * 根据作品id查询评论数
     * @param productId
     * @return
     */
    String selectcommentCountByProductId(String productId);
}
