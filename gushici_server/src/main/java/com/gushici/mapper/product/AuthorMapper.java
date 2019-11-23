package com.gushici.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.product.Reign;
import com.gushici.bean.product.Author;
import com.gushici.bean.product.AuthorAndReign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthorMapper extends BaseMapper<Author>{

    /**
     * 通过作者id 查询作者详情
     * @param authorId 作者id
     * @return
     */
    Author getAuthorByAuthId(String authorId);

    /**
     * 通过朝代id获取朝代名称
     * @param reignId 朝代id
     * @return
     */
    Reign getReignByReignId(Integer reignId);

    /**
     * 查询所有的朝代
     * @return
     */
    List<Reign> getReigns();

    /**
     * 通过朝代id获取所有作者
     * @param reignId
     * @return
     */
    List<Author> getAuthorByReignId(String reignId);


    /**
     * 通过作者关键字查询相关作者
     * @param authorName  作者搜索关键字
     * @return
     */
    List<AuthorAndReign> selectAuthorAndReignList(@Param("authorName") String authorName,
                                                  @Param("indexCount") Integer indexCount,
                                                  @Param("countInPage") Integer countInPage);


    /**
     * 通过作者id查询作者以及所在朝代
     * @param authorId  作者id
     * @return
     */
    AuthorAndReign getAuthorDetailsByAuthId(String authorId);
}
