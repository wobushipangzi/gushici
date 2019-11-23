package com.gushici.service.suibi.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.forum.Suibi;
import com.gushici.bean.product.Product;
import com.gushici.bean.user.*;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.CommentType;
import com.gushici.common.smallprogram.OnePageCount;
import com.gushici.common.utils.DateTimeUtils;
import com.gushici.mapper.product.ProductMapper;
import com.gushici.mapper.suibi.CommentMapper;
import com.gushici.mapper.suibi.PraiseMapper;
import com.gushici.mapper.suibi.SuibiMapper;
import com.gushici.mapper.user.UserMapper;
import com.gushici.mapper.suibi.ReplyMapper;
import com.gushici.service.product.ProductService;
import com.gushici.service.suibi.CommentService;
import com.gushici.service.suibi.ReplyService;
import com.gushici.service.suibi.SuibiService;
import com.gushici.service.user.MessageService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ReplyServiceImpl implements ReplyService {

    private static Logger logger = Logger.getLogger(ReplyServiceImpl.class);

    private final ReplyMapper replyMapper;

    private final UserMapper userMapper;

    private final ProductMapper productMapper;

    private final CommentMapper commentMapper;

    private final SuibiMapper suibiMapper;

    private final PraiseMapper praiseMapper;

    @Autowired
    public ReplyServiceImpl(ReplyMapper replyMapper,
                            UserMapper userMapper,
                            ProductMapper productMapper,
                            CommentMapper commentMapper,
                            SuibiMapper suibiMapper,
                            PraiseMapper praiseMapper) {
        this.replyMapper = replyMapper;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.commentMapper = commentMapper;
        this.suibiMapper = suibiMapper;
        this.praiseMapper = praiseMapper;
    }

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SuibiService suibiService;


    /**
     * 保存回复内容
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional
    public GlobalResult saveReply(JSONObject jsonObject) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            String commentId = jsonObject.getString("commentId");
            String comRepId = jsonObject.getString("comRepId");
            String comRepType = jsonObject.getString("comRepType");
            String content = jsonObject.getString("content");
            String fromOpenId = jsonObject.getString("fromOpenId");
            String toOpenId = jsonObject.getString("toOpenId");
            String topicId = jsonObject.getString("topicId");
            String topicType = jsonObject.getString("topicType");

            //TODO 以后对接评论审核接口

            //回复信息入库
            Reply reply = new Reply();
            reply.setComRepId(comRepId);
            reply.setComRepType(comRepType);
            reply.setCommentId(commentId);
            reply.setContent(content);
            reply.setFromOpenId(fromOpenId);
            reply.setToOpenId(toOpenId);
            reply.setReplyTime(new Date());

            int insert = replyMapper.insert(reply);
            if(insert <= 0){
                logger.error("回复评论落库失败");
                throw new GlobalException(ResultCode.数据库新增数据失败.getCode(), ResultCode.数据库新增数据失败.getMsg());
            }

            //所挂载的评论的回复数量加1
            Integer updateReplyCount = commentService.updateReplyCount(commentId);
            if(updateReplyCount <= 0){
                logger.error("所挂载评论加1失败");
                throw new GlobalException(ResultCode.更新数据库失败.getCode(), ResultCode.更新数据库失败.getMsg());
            }

            Reply replyByReply = replyService.getReplyByReply(reply);

            //消息通知表落库
            Message message = new Message();
            message.setComRepPraId(comRepId);
            message.setComRepPraType(comRepType);
            message.setFromOpenId(fromOpenId);
            message.setMessageType("comment");
            message.setToOpenId(toOpenId);
            message.setIsRead("0");
            message.setMesTime(new Date());
            message.setReplyId(replyByReply.getReplyId());

            Integer addMesCount = messageService.saveMessage(message);
            if(addMesCount <= 0){
                logger.error("消息通知表落库失败");
                throw new GlobalException(ResultCode.数据库新增数据失败.getCode(), ResultCode.数据库新增数据失败.getMsg());
            }

            //如果主题类型是古诗词，对相关古诗词评论数加1
            if(CommentType.古诗词类型.getMsg().equals(topicType)){
                Integer integer = productService.addCommentCount(topicId);
                if(integer <= 0){
                    logger.error("古诗词评论数加一失败");
                    throw new GlobalException(ResultCode.更新数据库失败.getCode(), ResultCode.更新数据库失败.getMsg());
                }
            }

            //如果主题类型是随笔，对相关随笔评论数加1
            if(CommentType.随笔类型.getMsg().equals(topicType)){
                Integer integer = suibiService.addCommentCount(topicId);
                if(integer <= 0){
                    logger.error("随笔评论数加一失败");
                    throw new GlobalException(ResultCode.更新数据库失败.getCode(), ResultCode.更新数据库失败.getMsg());
                }
            }
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("isSuccess","yes");
            globalResult.setData(dataMap);
        }catch (Exception e){
            logger.error("保存回复内容接口异常{}", e);
            throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
        }
        return globalResult;
    }


    /**
     * 获取回复内容id
     * @param reply
     * @return
     */
    @Override
    public Reply getReplyByReply(Reply reply) {
        QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
        replyQueryWrapper.eq("com_rep_id",reply.getComRepId());
        replyQueryWrapper.eq("com_rep_type",reply.getComRepType());
        //replyQueryWrapper.eq("comment_id",reply.getCommentId());
        replyQueryWrapper.eq("from_open_id",reply.getFromOpenId());
        replyQueryWrapper.eq("to_open_id",reply.getToOpenId());
        replyQueryWrapper.eq("reply_time",reply.getReplyTime());
        return replyMapper.selectOne(replyQueryWrapper);
    }


    /**
     * 查询某一评论下的所有回复
     * @param commentId 主评论id
     * @return
     */
    @Override
    public List<Map<String, Object>> getReplyList(String commentId, String page, String openId) {

        if(StringUtils.isEmpty(page)){
            page = "0";
        }
        Integer indexCount = Integer.valueOf(page) * OnePageCount.GUSHICI_COUNT_ONE_PAGE;
        List<ReplyAndUser> replies = replyMapper.selectReplyAndUser(commentId, indexCount, OnePageCount.GUSHICI_COUNT_ONE_PAGE);

        if(replies == null || replies.size() == 0){
            return null;
        }

        List<String> replyIdList = new ArrayList<>();
        for (ReplyAndUser reply : replies) {
            replyIdList.add(reply.getReplyId());
        }
        //查询被点赞的评论有哪些
        List<String> replyIds = praiseMapper.selectIsPraisedReplyId(replyIdList, openId);

        //组装此评论下的所有回复 replyId content fromOpenId replyTime praiseCount nickName avatar_url
        List<Map<String, Object>> replyList = new ArrayList<>();
        for (ReplyAndUser reply : replies) {
            Map<String, Object> replyMap = new HashMap<>();
            replyMap.put("isPraised",false);
            for (String replyId : replyIds) {
                if(reply.getReplyId().equals(replyId)){
                    replyMap.put("isPraised",true);
                    break;
                }
            }
            replyMap.put("replyId",Integer.valueOf(reply.getReplyId()));
            replyMap.put("content",reply.getContent());
            replyMap.put("comRepType",reply.getComRepType());

            String replyTime = DateTimeUtils.computeTime(reply.getReplyTime().getTime());
            replyMap.put("replyTime",replyTime);
            if(StringUtils.isEmpty(reply.getPraiseCount())){
                replyMap.put("praiseCount",0);
            }else {
                replyMap.put("praiseCount",Integer.valueOf(reply.getPraiseCount()));
            }

            //用户昵称  头像
            replyMap.put("fromOpenId",reply.getFromOpenId());
            replyMap.put("fromName",reply.getFromUserName());
            replyMap.put("fromHeadPhotoUrl",reply.getFromHeadPhoto());

            //被回复用户昵称 头像
            replyMap.put("toOpenId",reply.getToOpenId());
            replyMap.put("toName",reply.getToUserName());
            replyMap.put("toHeadPhotoUrl",reply.getToHeadPhoto());

            replyList.add(replyMap);
        }
        return replyList;
    }


    /**
     * 回复表点赞次数加1
     * @param replyId
     * @return
     */
    @Override
    public synchronized Integer updatePraise(String replyId) {
        QueryWrapper<Reply> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("reply_id",replyId);
        Reply reply = replyMapper.selectOne(commentQueryWrapper);

        if(StringUtils.isEmpty(reply.getPraiseCount())){
            reply.setPraiseCount(String.valueOf(1));
        }else {
            AtomicInteger praiseCount = new AtomicInteger(Integer.valueOf(reply.getPraiseCount()));
            praiseCount.getAndAdd(1);
            reply.setPraiseCount(String.valueOf(praiseCount.get()));
        }
        return replyMapper.update(reply, commentQueryWrapper);
    }


    /**
     * 通过openID获取用户的评论总赞数
     * @param openId
     * @return
     */
    @Override
    public Integer getReplyPraiseByOpenId(String openId) {
        QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
        replyQueryWrapper.eq("from_open_id",openId);
        List<Reply> replies = replyMapper.selectList(replyQueryWrapper);
        Integer praiseCount = 0;
        for (Reply reply : replies) {
            String praiseCountStr = reply.getPraiseCount();
            if(StringUtils.isNotEmpty(praiseCountStr)){
                praiseCount = Integer.valueOf(praiseCountStr) + praiseCount;
            }
        }
        return praiseCount;
    }


    /**
     * 删除回复评论
     * @param replyId  回复id
     */
    @Override
    public GlobalResult delReplys(String replyId, String topicId, String topicType, String commentId) {
        GlobalResult globalResult = GlobalResult.success();

        if(CommentType.古诗词类型.getMsg().equals(topicType)){
            //古诗词主题评论数减少相应个数
            QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
            productQueryWrapper.select("comment_count");
            productQueryWrapper.eq("product_id",topicId);
            Product product = productMapper.selectOne(productQueryWrapper);
            Integer commentCount = Integer.valueOf(product.getCommentCount());

            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.select("reply_count");
            commentQueryWrapper.eq("comment_id",commentId);
            Comment comment = commentMapper.selectOne(commentQueryWrapper);
            Integer replyCount = Integer.valueOf(comment.getReplyCount());

            //查询回复评论下挂载的所有回复数量
            QueryWrapper<Reply> replyQuery = new QueryWrapper<>();
            replyQuery.eq("com_rep_id",replyId);
            replyQuery.eq("com_rep_type","reply");
            replyQuery.select("com_rep_id");
            Integer selectCount = replyMapper.selectCount(replyQuery);
            selectCount++;

            commentCount = commentCount - selectCount;
            replyCount = replyCount - selectCount;

            product.setCommentCount(String.valueOf(commentCount));
            QueryWrapper<Product> productQuery = new QueryWrapper<>();
            productQuery.eq("product_id",topicId);
            productMapper.update(product,productQuery);

            comment.setReplyCount(String.valueOf(replyCount));
            QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
            commentQuery.eq("comment_id",commentId);
            commentMapper.update(comment,commentQuery);
        }

        if(CommentType.随笔类型.getMsg().equals(topicType)){
            //随笔主题评论数减少相应个数
            QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
            suibiQueryWrapper.select("comment_count");
            suibiQueryWrapper.eq("suibi_id",topicId);
            Suibi suibi = suibiMapper.selectOne(suibiQueryWrapper);
            Integer commentCount = Integer.valueOf(suibi.getCommentCount());

            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.select("reply_count");
            commentQueryWrapper.eq("comment_id",commentId);
            Comment comment = commentMapper.selectOne(commentQueryWrapper);
            Integer replyCount = Integer.valueOf(comment.getReplyCount());

            //查询回复评论下挂载的所有回复数量
            QueryWrapper<Reply> replyQuery = new QueryWrapper<>();
            replyQuery.eq("com_rep_id",replyId);
            replyQuery.eq("com_rep_type","reply");
            replyQuery.select("com_rep_id");
            Integer selectCount = replyMapper.selectCount(replyQuery);
            selectCount++;

            commentCount = commentCount - selectCount;
            replyCount = replyCount - selectCount;

            suibi.setCommentCount(String.valueOf(commentCount));
            QueryWrapper<Suibi> suibiQuery = new QueryWrapper<>();
            suibiQuery.eq("suibi_id",topicId);
            suibiMapper.update(suibi,suibiQuery);

            comment.setReplyCount(String.valueOf(replyCount));
            QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
            commentQuery.eq("comment_id",commentId);
            commentMapper.update(comment,commentQuery);
        }

        QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
        replyQueryWrapper.eq("reply_id",replyId);
        int delete = replyMapper.delete(replyQueryWrapper);
        if(1 > delete){
            logger.error("回复表删除数据失败");
            globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
            return globalResult;
        }

        //删除回复评论下挂载的回复
        QueryWrapper<Reply> replyQuery = new QueryWrapper<>();
        replyQuery.eq("com_rep_id",replyId);
        replyQuery.eq("com_rep_type","reply");
        replyMapper.delete(replyQuery);


        return globalResult;
    }

}
