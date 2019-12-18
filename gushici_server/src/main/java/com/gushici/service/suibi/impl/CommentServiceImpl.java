package com.gushici.service.suibi.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.forum.Suibi;
import com.gushici.bean.product.Product;
import com.gushici.bean.user.Comment;
import com.gushici.bean.user.CommentAndUser;
import com.gushici.bean.user.Message;
import com.gushici.bean.user.Reply;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.CommentType;
import com.gushici.common.smallprogram.OnePageCount;
import com.gushici.common.utils.DateTimeUtils;
import com.gushici.mapper.product.ProductMapper;
import com.gushici.mapper.suibi.CommentMapper;
import com.gushici.mapper.suibi.PraiseMapper;
import com.gushici.mapper.suibi.ReplyMapper;
import com.gushici.mapper.suibi.SuibiMapper;
import com.gushici.mapper.user.UserMapper;
import com.gushici.service.product.ProductService;
import com.gushici.service.suibi.CommentService;
import com.gushici.service.suibi.SuibiService;
import com.gushici.service.user.MessageService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CommentServiceImpl implements CommentService {

    private static Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentMapper commentMapper;

    private final UserMapper userMapper;

    private final ReplyMapper replyMapper;

    private final ProductMapper productMapper;

    private final SuibiMapper suibiMapper;

    private final PraiseMapper praiseMapper;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,
                              UserMapper userMapper,
                              ReplyMapper replyMapper,
                              ProductMapper productMapper,
                              SuibiMapper suibiMapper,
                              PraiseMapper praiseMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.replyMapper = replyMapper;
        this.productMapper = productMapper;
        this.suibiMapper = suibiMapper;
        this.praiseMapper = praiseMapper;
    }

    @Autowired
    private CommentService commentService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SuibiService suibiService;

    /**
     * 保存评论信息
     */
    @Override
    @Transactional
    public GlobalResult saveComment(JSONObject jsonObject) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            String topicType = jsonObject.getString("topicType");
            String topicId = jsonObject.getString("topicId");
            String content = jsonObject.getString("content");
            String fromOpenId = jsonObject.getString("fromOpenId");
            String toOpenId = jsonObject.getString("toOpenId");

            //TODO 以后对接评论审核接口

            //将评论信息入库
            Comment comment = new Comment();
            comment.setTopicType(topicType);
            comment.setTopicId(topicId);
            comment.setContent(content);
            comment.setFromOpenId(fromOpenId);
            Date commentTime = new Date();
            comment.setCommentTime(commentTime);
            int insert = commentMapper.insert(comment);
            if(insert <= 0){
                logger.error("评论信息入库失败，执行结果为{}", insert);
                throw new GlobalException(ResultCode.数据库新增数据失败.getCode(), ResultCode.数据库新增数据失败.getMsg());
            }

            //查询刚刚入库的评论id
            Comment commentByComment = commentService.getCommentByComment(comment);
            if(null == commentByComment){
                logger.error("查询刚刚入库的评论id失败");
                throw new GlobalException(ResultCode.查询数据库失败.getCode(), ResultCode.查询数据库失败.getMsg());
            }

            //  入库消息通知表
            Message message = new Message();
            message.setComRepPraType(topicType);
            message.setComRepPraId(topicId);
            message.setFromOpenId(fromOpenId);
            message.setMessageType("comment");
            message.setIsRead("0");
            message.setToOpenId(toOpenId);
            message.setMesTime(new Date());
            message.setReplyId(commentByComment.getCommentId());

            Integer addMesCount = messageService.saveMessage(message);
            if(addMesCount <= 0){
                logger.error("入库消息通知表失败，执行结果{}", addMesCount);
                throw new GlobalException(ResultCode.数据库新增数据失败.getCode(), ResultCode.数据库新增数据失败.getMsg());
            }

            //如果主题类型是古诗词，对相关古诗词评论数加1
            if(CommentType.古诗词类型.getMsg().equals(topicType)){
                Integer integer = productService.addCommentCount(topicId);
                if(integer <= 0){
                    logger.error("更新古诗词评论数失败，执行结果{}", integer);
                    throw new GlobalException(ResultCode.更新数据库失败.getCode(), ResultCode.更新数据库失败.getMsg());
                }
            }

            //如果主题类型是随笔，对相关古诗词评论数加1
            if(CommentType.随笔类型.getMsg().equals(topicType)){
                Integer integer = suibiService.addCommentCount(topicId);
                if(integer <= 0){
                    logger.error("更新随笔评论数失败，执行结果{}", integer);
                    throw new GlobalException(ResultCode.更新数据库失败.getCode(), ResultCode.更新数据库失败.getMsg());
                }
            }
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("isSuccess","yes");
            globalResult.setData(dataMap);
        }catch (Exception e){
            logger.error("保存评论信息接口异常{}", e);
            throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
        }
        return globalResult;
    }


    /**
     * 查询评论id
     * @return
     */
    @Override
    public Comment getCommentByComment(Comment comment) {
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.select("comment_id");
        commentQueryWrapper.eq("topic_id",comment.getTopicId());
        commentQueryWrapper.eq("topic_type",comment.getTopicType());
        commentQueryWrapper.eq("from_open_id",comment.getFromOpenId());
        commentQueryWrapper.eq("comment_time",comment.getCommentTime());
        return commentMapper.selectOne(commentQueryWrapper);
    }


    /**
     * 获取所有的主评论
     * @param topicId
     * @param topicType
     */
    @Override
    public GlobalResult selectCommentByIdAndType(String topicId, String topicType, String page, String openId) {

        GlobalResult globalResult = GlobalResult.success();
        try {
            if (StringUtils.isEmpty(page)){
                page = "0";
            }
            Integer indexCount = Integer.valueOf(page) * OnePageCount.GUSHICI_COUNT_ONE_PAGE;
            //获取此作品下的所有评论 commentId content fromOpenId commentTime praiseCount nickName avatar_url
            List<CommentAndUser> comments = commentMapper.selectCommentAndUser(topicId, topicType, indexCount, OnePageCount.GUSHICI_COUNT_ONE_PAGE);

            if(comments == null || comments.size() == 0){
                return globalResult;
            }

            List<String> commentIdList = new ArrayList<>();
            for (CommentAndUser comment : comments) {
                commentIdList.add(comment.getCommentId());
            }
            //查询哪条主评论被点过赞
            List<String> commentIds = praiseMapper.selectIsPraisedCommentId(commentIdList,openId);

            ArrayList<HashMap<String,Object>> commentList = new ArrayList<>();
            for (CommentAndUser comment : comments) {
                HashMap<String, Object> commentMap = new HashMap<>();
                commentMap.put("isPraised",false);
                for (String commentId : commentIds) {
                    if(comment.getCommentId().equals(commentId)){
                        commentMap.put("isPraised",true);   //是否被点过赞
                        break;
                    }
                }
                commentMap.put("commentId",Integer.valueOf(comment.getCommentId()));
                commentMap.put("content",comment.getContent());
                commentMap.put("commentOpenId",comment.getFromOpenId());
                String commentTime = DateTimeUtils.computeTime(comment.getCommentTime().getTime());
                commentMap.put("commentTime",commentTime);
                if(StringUtils.isEmpty(comment.getPraiseCount())){
                    commentMap.put("praiseCount",0);
                }else {
                    commentMap.put("praiseCount", Integer.valueOf(comment.getPraiseCount()));
                }

                //用户昵称  头像
                commentMap.put("name",comment.getName());
                commentMap.put("headPhotoUrl",comment.getHeadPhotoUrl());
                //回复数量
                commentMap.put("replyCount",StringUtils.isEmpty(comment.getReplyCount())? 0 : Integer.valueOf(comment.getReplyCount()));
                commentList.add(commentMap);
            }
            globalResult.setData(commentList);
        }catch (Exception e){
           logger.error("获取所有主评论接口异常{}", e);
           globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 点赞字段加1
     * @param commentId
     */
    @Override
    public synchronized Integer updatePraise(String commentId) {
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("comment_id",commentId);
        Comment comment = commentMapper.selectOne(commentQueryWrapper);

        if(StringUtils.isEmpty(comment.getPraiseCount())){
            comment.setPraiseCount(String.valueOf(1));
        }else {
            AtomicInteger praiseCount = new AtomicInteger(Integer.valueOf(comment.getPraiseCount()));
            praiseCount.getAndAdd(1);
            comment.setPraiseCount(String.valueOf(praiseCount.get()));
        }
        return commentMapper.update(comment, commentQueryWrapper);
    }


    /**
     * 通过openId获取该用户所有的评论赞数
     * @param openId
     */
    @Override
    public Integer getCommentPraiseByOpenId(String openId) {
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("from_open_id",openId);
        List<Comment> commentList = commentMapper.selectList(commentQueryWrapper);
        Integer praiseCount = 0;
        for (Comment comment : commentList) {
            String praiseCountStr = comment.getPraiseCount();
            if(StringUtils.isNotEmpty(praiseCountStr)){
                praiseCount = Integer.valueOf(praiseCountStr) + praiseCount;
            }
        }
        return praiseCount;
    }


    /**
     * 删除主评论以及其下所挂载的所有回复
     * @param commentId  评论id
     */
    @Override
    public GlobalResult delCommentAndReply(String commentId, String topicId, String topicType) {
        GlobalResult globalResult = GlobalResult.success();

        if(CommentType.古诗词类型.getMsg().equals(topicType)){
            //古诗词主题评论减少相应个数
            QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
            productQueryWrapper.select("comment_count");
            productQueryWrapper.eq("product_id",topicId);
            Product product = productMapper.selectOne(productQueryWrapper);

            Integer commentCount = Integer.valueOf(product.getCommentCount());

            //查询此评论下所有的回复个数
            QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
            replyQueryWrapper.eq("comment_id",commentId);
            replyQueryWrapper.select("comment_id");
            Integer replyCount = replyMapper.selectCount(replyQueryWrapper);


            replyCount++;
            commentCount = commentCount - replyCount;


            product.setCommentCount(String.valueOf(commentCount));
            QueryWrapper<Product> productQuery = new QueryWrapper<>();
            productQuery.eq("product_id",topicId);
            productMapper.update(product,productQuery);
        }

        if(CommentType.随笔类型.getMsg().equals(topicType)){
            //随笔主题评论减少相应个数
            QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
            suibiQueryWrapper.select("comment_count");
            suibiQueryWrapper.eq("suibi_id",topicId);
            Suibi suibi = suibiMapper.selectOne(suibiQueryWrapper);

            Integer commentCount = Integer.valueOf(suibi.getCommentCount());

            //查询此评论下所有的回复个数
            QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
            replyQueryWrapper.eq("comment_id",commentId);
            replyQueryWrapper.select("comment_id");
            Integer replyCount = replyMapper.selectCount(replyQueryWrapper);


            replyCount++;
            commentCount = commentCount - replyCount;


            suibi.setCommentCount(String.valueOf(commentCount));
            QueryWrapper<Suibi> suibiQuery = new QueryWrapper<>();
            suibiQuery.eq("suibi_id",topicId);
            suibiMapper.update(suibi,suibiQuery);
        }

        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("comment_id",commentId);
        int deleteCount = commentMapper.delete(commentQueryWrapper);
        if(1 == deleteCount){

            //删除此评论下挂载的所有评论
            QueryWrapper<Reply> replyQueryWrapper = new QueryWrapper<>();
            replyQueryWrapper.eq("comment_id",commentId);
            int delete = replyMapper.delete(replyQueryWrapper);
            if(delete < 0){
                logger.error("回复评论表删除数据失败");
                globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
                globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
                return globalResult;
            }
        }else {
            logger.error("主评论表删除数据失败");
            globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
            return globalResult;
        }



        return globalResult;
    }


    /**
     * 该评论的回复数加一
     * @param commentId 评论id
     * @return
     */
    @Override
    public Integer updateReplyCount(String commentId) {
        String replyCountStr = commentMapper.selectReplyCountByCommentId(commentId);

        AtomicInteger replyCount ;
        if(StringUtils.isEmpty(replyCountStr)){
            replyCount = new AtomicInteger(0);
            replyCount.getAndAdd(1);
        }else {
            replyCount = new AtomicInteger(Integer.valueOf(replyCountStr));
            replyCount.getAndAdd(1);
        }

        QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
        commentQuery.eq("comment_id",commentId);
        Comment comment = new Comment();
        comment.setReplyCount(String.valueOf(replyCount.get()));
        return commentMapper.update(comment, commentQuery);
    }


}
