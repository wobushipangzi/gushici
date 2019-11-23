package com.gushici.mapper.suibi;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.user.Praise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface PraiseMapper extends BaseMapper<Praise>{


    /**
     * 查询被点过赞的评论
     * @param commentIdList  所有的评论id集合
     * @param openId  用户openId
     * @return
     */
    List<String> selectIsPraisedCommentId(@Param("commentIdList") List<String> commentIdList, @Param("openId") String openId);

    /**
     * 查询被点过赞的回复评论
     * @return
     */
    ArrayList<String> selectIsPraisedReplyId(@Param("replyIdList") List<String> replyIdList, @Param("openId") String openId);

}
