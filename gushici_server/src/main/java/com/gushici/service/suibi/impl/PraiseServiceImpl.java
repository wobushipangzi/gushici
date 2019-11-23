package com.gushici.service.suibi.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.forum.Suibi;
import com.gushici.bean.user.Comment;
import com.gushici.bean.user.Reply;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.mapper.suibi.SuibiMapper;
import com.gushici.mapper.suibi.CommentMapper;
import com.gushici.mapper.suibi.PraiseMapper;
import com.gushici.mapper.suibi.ReplyMapper;
import com.gushici.bean.user.Praise;
import com.gushici.service.suibi.PraiseService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PraiseServiceImpl implements PraiseService {

    private static Logger logger = Logger.getLogger(PraiseServiceImpl.class);

    private final PraiseMapper praiseMapper;

    private final SuibiMapper suibiMapper;

    private final CommentMapper commentMapper;

    private final ReplyMapper replyMapper;

    @Autowired
    public PraiseServiceImpl(PraiseMapper praiseMapper, SuibiMapper suibiMapper, CommentMapper commentMapper, ReplyMapper replyMapper) {
        this.praiseMapper = praiseMapper;
        this.suibiMapper = suibiMapper;
        this.commentMapper = commentMapper;
        this.replyMapper = replyMapper;
    }


    /**
     * 点赞表新增数据
     * @param praise
     * @return
     */
    @Override
    public synchronized Integer insertPraise(Praise praise) {
        return praiseMapper.insert(praise);
    }



    /**
     * 删除点赞记录
     * @param suibiId   随笔id
     * @param commentId   评论id
     * @param replyId   回复id
     * @param praiseOpenId   点赞人的openId
     */
    @Override
    public GlobalResult delPraise(String suibiId, String commentId, String replyId, String praiseOpenId) {
        GlobalResult globalResult = GlobalResult.success();

        QueryWrapper<Praise> praiseQueryWrapper = new QueryWrapper<>();
        praiseQueryWrapper.eq("praise_open_id",praiseOpenId);
        if(StringUtils.isNotEmpty(suibiId)){
            praiseQueryWrapper.eq("suibi_id",suibiId);
        }

        if(StringUtils.isNotEmpty(commentId)){
            praiseQueryWrapper.eq("comment_id",commentId);
        }

        if(StringUtils.isNotEmpty(replyId)){
            praiseQueryWrapper.eq("reply_id",replyId);
        }

        int delete = praiseMapper.delete(praiseQueryWrapper);
        if(1 == delete){
            //随笔表的点赞数减一
            if(StringUtils.isNotEmpty(suibiId) && StringUtils.isEmpty(commentId) && StringUtils.isEmpty(replyId)){
                QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
                suibiQueryWrapper.eq("suibi_id",suibiId);
                Suibi suibi = suibiMapper.selectOne(suibiQueryWrapper);
                if(null == suibi){
                    logger.error("随笔表无此条数据");
                    globalResult.setCode(ResultCode.查询数据库失败.getCode());
                    globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
                    return globalResult;
                }
                String praiseCountStr = suibi.getPraiseCount();
                if(StringUtils.isNotEmpty(praiseCountStr) || !"0".equals(praiseCountStr)){
                    AtomicInteger praiseCount = new AtomicInteger(Integer.valueOf(praiseCountStr));
                    praiseCount.getAndAdd(-1);
                    praiseCountStr = String.valueOf(praiseCount.get());

                    suibi.setPraiseCount(praiseCountStr);
                    int update = suibiMapper.update(suibi, suibiQueryWrapper);
                    if(1 != update) {
                        logger.error("随笔表更新点赞字段失败");
                        globalResult.setCode(ResultCode.更新数据库失败.getCode());
                        globalResult.setMessage(ResultCode.更新数据库失败.getMsg());
                        return globalResult;
                    }
                }
            }


            //评论表的点赞数减一
            if(StringUtils.isEmpty(suibiId) && StringUtils.isNotEmpty(commentId) && StringUtils.isEmpty(replyId)){
                QueryWrapper<Comment> suibiQueryWrapper = new QueryWrapper<>();
                suibiQueryWrapper.eq("comment_id",commentId);
                Comment comment = commentMapper.selectOne(suibiQueryWrapper);
                if(null == comment){
                    logger.error("评论表无此条数据");
                    globalResult.setCode(ResultCode.查询数据库失败.getCode());
                    globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
                    return globalResult;
                }
                String praiseCountStr = comment.getPraiseCount();
                if(StringUtils.isNotEmpty(praiseCountStr)){
                    AtomicInteger praiseCount = new AtomicInteger(Integer.valueOf(praiseCountStr));
                    praiseCount.getAndAdd(-1);
                    praiseCountStr = String.valueOf(praiseCount.get());

                    comment.setPraiseCount(praiseCountStr);
                    int update = commentMapper.update(comment, suibiQueryWrapper);
                    if(1 != update) {
                        logger.error("评论表更新点赞字段失败");
                        globalResult.setCode(ResultCode.更新数据库失败.getCode());
                        globalResult.setMessage(ResultCode.更新数据库失败.getMsg());
                        return globalResult;
                    }
                }
            }


            //回复表的点赞数减一
            if(StringUtils.isEmpty(suibiId) && StringUtils.isEmpty(commentId) && StringUtils.isNotEmpty(replyId)){
                QueryWrapper<Reply> suibiQueryWrapper = new QueryWrapper<>();
                suibiQueryWrapper.eq("reply_id",replyId);
                Reply reply = replyMapper.selectOne(suibiQueryWrapper);
                if(null == reply){
                    logger.error("回复表无此条数据");
                    globalResult.setCode(ResultCode.查询数据库失败.getCode());
                    globalResult.setMessage(ResultCode.查询数据库失败.getMsg());
                    return globalResult;
                }
                String praiseCountStr = reply.getPraiseCount();
                if(StringUtils.isNotEmpty(praiseCountStr)){
                    AtomicInteger praiseCount = new AtomicInteger(Integer.valueOf(praiseCountStr));
                    praiseCount.getAndAdd(-1);
                    praiseCountStr = String.valueOf(praiseCount.get());

                    reply.setPraiseCount(praiseCountStr);
                    int update = replyMapper.update(reply, suibiQueryWrapper);
                    if(1 != update) {
                        logger.error("回复表更新点赞字段失败");
                        globalResult.setCode(ResultCode.更新数据库失败.getCode());
                        globalResult.setMessage(ResultCode.更新数据库失败.getMsg());
                        return globalResult;
                    }
                }
            }
        }else {
            logger.error("点赞表删除数据失败");
            globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
            return globalResult;
        }
        return globalResult;
    }
}
