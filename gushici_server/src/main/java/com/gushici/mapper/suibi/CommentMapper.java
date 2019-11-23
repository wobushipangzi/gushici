package com.gushici.mapper.suibi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.user.Comment;
import com.gushici.bean.user.CommentAndUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment>{


    /**
     * 根据主题id和主题类型查询对应的主评论和用户信息
     * @param topicId  主题id
     * @param topicType  主题类型
     */
    List<CommentAndUser> selectCommentAndUser(@Param("topicId") String topicId,
                                              @Param("topicType")String topicType,
                                              @Param("indexCount") Integer indexCount,
                                              @Param("countInPage") Integer countInPage);


    /**
     * 通过评论id查询回复数
     * @param commentId
     * @return
     */
    String selectReplyCountByCommentId(String commentId);
}
