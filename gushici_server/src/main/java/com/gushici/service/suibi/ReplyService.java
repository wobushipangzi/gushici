package com.gushici.service.suibi;

import com.alibaba.fastjson.JSONObject;
import com.gushici.common.result.GlobalResult;
import com.gushici.bean.user.Reply;

import java.util.List;
import java.util.Map;

public interface ReplyService {


    /**
     *保存回复内容
     * @param jsonObject
     * @return
     */
    GlobalResult saveReply(JSONObject jsonObject);


    /**
     * 获取回复内容
     * @param reply
     * @return
     */
    Reply getReplyByReply(Reply reply);


    /**
     * 查询某一评论或回复下的所有回复
     * @param commentId
     * @return
     */
    List<Map<String, Object>> getReplyList(String commentId, String page, String openId);


    /**
     * 回复表点赞次数加1
     * @param replyId
     * @return
     */
    Integer updatePraise(String replyId);


    /**
     * 通过openID获取用户的回复总赞数
     * @param openId
     * @return
     */
    Integer getReplyPraiseByOpenId(String openId);

    /**
     * 删除回复评论
     * @param replyId  回复id
     */
    GlobalResult delReplys(String replyId, String topicId, String topicType, String commentId);
}
