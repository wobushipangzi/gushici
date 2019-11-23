package com.gushici.mapper.suibi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gushici.bean.user.Reply;
import com.gushici.bean.user.ReplyAndUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {

    /**
     * 查询某主评论下所有挂载的回复内容以及用户信息
     * @param commentId  主评论id
     * @return
     */
    List<ReplyAndUser> selectReplyAndUser(@Param("commentId") String commentId,
                                          @Param("indexCount") Integer indexCount,
                                          @Param("countInPage") Integer countInPage);
}
