package com.gushici.service.suibi;

import com.gushici.common.result.GlobalResult;
import com.gushici.bean.user.Praise;

public interface PraiseService {

    /**
     * 点赞表新增数据
     * @param praise
     * @return
     */
    Integer insertPraise(Praise praise);

    /**
     * 删除点赞记录
     * @param suibiId   随笔id
     * @param commentId   评论id
     * @param replyId   回复id
     */
    GlobalResult delPraise(String suibiId, String commentId, String replyId, String praiseOpenId);
}
