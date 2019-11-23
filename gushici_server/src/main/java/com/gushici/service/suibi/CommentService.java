package com.gushici.service.suibi;

import com.alibaba.fastjson.JSONObject;
import com.gushici.common.result.GlobalResult;
import com.gushici.bean.user.Comment;

import java.util.HashMap;
import java.util.List;

public interface CommentService {

    /**
     * 保存评论信息
     */
    GlobalResult saveComment(JSONObject jsonObject);


    /**
     * 查询评论id
     */
    Comment getCommentByComment(Comment comment);


    /**
     * 获取所有的评论
     * @param topicId    主题id
     * @param topicType  主题类型
     */
    GlobalResult selectCommentByIdAndType(String topicId, String topicType, String page, String openId);


    /**
     * 点赞字段加1
     * @param commentId  主评论id
     */
    Integer updatePraise(String commentId);


    /**
     * 通过openId获取改用户所有的评论赞数
     * @param openId   用户openId
     */
    Integer getCommentPraiseByOpenId(String openId);

    /**
     * 删除主评论以及其下所挂载的所有回复
     * @param commentId   主评论id
     */
    GlobalResult delCommentAndReply(String commentId, String topicId, String topicType);


    /**
     * 改评论的回复数加一
     * @param commentId 评论id
     * @return
     */
    Integer updateReplyCount(String commentId);
}
