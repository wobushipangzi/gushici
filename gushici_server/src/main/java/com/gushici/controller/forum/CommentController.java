package com.gushici.controller.forum;

import com.alibaba.fastjson.JSONObject;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.CommentType;
import com.gushici.controller.user.UserController;
import com.gushici.service.product.ProductService;
import com.gushici.service.suibi.SuibiService;
import com.gushici.service.suibi.CommentService;
import com.gushici.service.user.MessageService;
import com.gushici.service.suibi.PraiseService;
import com.gushici.service.suibi.ReplyService;
import com.gushici.bean.user.Message;
import com.gushici.bean.user.Praise;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final CommentService commentService;

    private final ReplyService replyService;

    private final PraiseService praiseService;

    private final MessageService messageService;

    private final SuibiService suibiService;

    private final ProductService productService;

    @Autowired
    public CommentController(CommentService commentService,
                             ReplyService replyService,
                             PraiseService praiseService,
                             MessageService messageService,
                             SuibiService suibiService,
                             ProductService productService) {
        this.commentService = commentService;
        this.replyService = replyService;
        this.praiseService = praiseService;
        this.messageService = messageService;
        this.suibiService = suibiService;
        this.productService = productService;
    }

    /**
     * 发表评论接口
     */
    @RequestMapping(value = "/publish/comment", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult publishComment(@RequestBody String jsonData) {
        logger.info("发表评论接口入参为：==>{}", jsonData);
        GlobalResult globalResult;
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);

            //保存评论信息并入库消息通知表
            globalResult = commentService.saveComment(jsonObject);
            if (!ResultCode.处理成功.getCode().equals(globalResult.getCode())) {
                logger.error("调用保存评论并入库消息通知表失败，执行结果{}", JSONObject.toJSONString(globalResult));
                globalResult = GlobalResult.error();
            }
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("发表评论接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 发表回复接口
     */
    @RequestMapping(value = "/publish/reply", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult publishReply(@RequestBody String jsonData) {
        logger.info("发表回复接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);

            globalResult = replyService.saveReply(jsonObject);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用保存回复接口失败,处理结果{}", JSONObject.toJSONString(globalResult));
                globalResult = GlobalResult.error();
            }
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("系统错误异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }



    /**
     * 展示主评论接口
     */
    @RequestMapping(value = "/show/comment", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult showComment(@RequestBody String jsonData) {
        logger.info("展示主评论接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();

        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String topicId = jsonObject.getString("topicId");
            String topicType = jsonObject.getString("topicType");
            String page = jsonObject.getString("page");
            String openId = jsonObject.getString("openId");

            //获取此作品下的所有评论 commentId content fromOpenId commentTime praiseCount nickName avatar_url
            globalResult = commentService.selectCommentByIdAndType(topicId, topicType, page, openId);
            logger.info("展示主评论接口查询结果为：==>{}", JSONObject.toJSONString(globalResult));

            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用获取主评论接口失败");
                globalResult = GlobalResult.error();
            }
        }catch (Exception e){
            logger.error("展示主评论接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 显示回复信息接口
     */
    @RequestMapping(value = "/show/reply", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult showReply(@RequestBody String jsonData) {
        GlobalResult globalResult = GlobalResult.success();
        logger.info("显示回复信息接口入参为：==>{}", jsonData);

        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        String commentId = jsonObject.getString("commentId");
        String page = jsonObject.getString("page");
        String openId = jsonObject.getString("openId");

        //查询此评论下的所有回复
        List<Map<String, Object>> replyList = replyService.getReplyList(commentId, page, openId);

        logger.info("展示回复信息接口查询结果为：==>{}", JSONObject.toJSONString(replyList));

        if(replyList != null && replyList.size() > 0){
            globalResult.setData(replyList);
            return globalResult;
        }
        globalResult.setCode(ResultCode.没有更多信息.getCode());
        globalResult.setMessage(ResultCode.没有更多信息.getMsg());
        return globalResult;
    }


    /**
     * 删除主评论接口
     */
    @RequestMapping(value = "/delComment", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult delComment(@RequestBody String jsonData) {
        logger.info("删除主评论接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        String commentId = jsonObject.getString("commentId");
        String topicId = jsonObject.getString("topicId");
        String topicType = jsonObject.getString("topicType");
        if(StringUtils.isEmpty(commentId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        globalResult = commentService.delCommentAndReply(commentId, topicId, topicType);

        if(ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("isSuccess","yes");
            globalResult.setData(dataMap);
        }

        return globalResult;
    }



    /**
     * 删除回复评论接口
     */
    @RequestMapping(value = "/delReply", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult delReply(@RequestBody String jsonData) {
        logger.info("删除回复评论接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        String replyId = jsonObject.getString("replyId");
        String topicId = jsonObject.getString("topicId");
        String topicType = jsonObject.getString("topicType");
        String commentId = jsonObject.getString("commentId");
        if(StringUtils.isEmpty(replyId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        globalResult = replyService.delReplys(replyId, topicId, topicType, commentId);

        if(ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("isSuccess","yes");
            globalResult.setData(dataMap);
        }

        return globalResult;
    }


    /**
     * 取消点赞接口
     */
    @RequestMapping(value = "/remove/praise", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult removePraise(@RequestBody String jsonData) {
        logger.info("取消点赞接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String suibiId = jsonObject.getString("suibiId");
        String commentId = jsonObject.getString("commentId");
        String replyId = jsonObject.getString("replyId");
        String praiseOpenId = jsonObject.getString("praiseOpenId");

        if(StringUtils.isEmpty(suibiId) && StringUtils.isEmpty(commentId) && StringUtils.isEmpty(replyId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        if(StringUtils.isNotEmpty(suibiId) && StringUtils.isNotEmpty(commentId) && StringUtils.isEmpty(replyId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        if(StringUtils.isNotEmpty(suibiId) && StringUtils.isEmpty(commentId) && StringUtils.isNotEmpty(replyId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        if(StringUtils.isEmpty(suibiId) && StringUtils.isNotEmpty(commentId) && StringUtils.isNotEmpty(replyId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        if(StringUtils.isNotEmpty(suibiId) && StringUtils.isNotEmpty(commentId) && StringUtils.isNotEmpty(replyId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

            //删除点赞表信息  并且对应表点赞字段减一
        globalResult = praiseService.delPraise(suibiId, commentId, replyId, praiseOpenId);

        if(ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("isSuccess","yes");
            globalResult.setData(dataMap);
        }

        return globalResult;
    }



    /**
     * 点赞接口
     */
    @RequestMapping(value = "/praise", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult praise(@RequestBody String jsonData) {
        logger.info("点赞接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        String suibiId = jsonObject.getString("suibiId");
        String commentId = jsonObject.getString("commentId");
        String replyId = jsonObject.getString("replyId");
        String fromOpenId = jsonObject.getString("fromOpenId");
        String toOpenId = jsonObject.getString("toOpenId");
        if(StringUtils.isNotEmpty(commentId) && StringUtils.isEmpty(replyId) && StringUtils.isEmpty(suibiId)){
            //评论表点赞字段加1
            Integer updateCount = commentService.updatePraise(commentId);
            if(!globalResult.checkOutUpdate(globalResult, updateCount)){
                logger.error("点赞接口更新评论数据库失败");
                return globalResult;
            }
            //落库点赞表
            Praise praise = new Praise();
            praise.setCommentId(commentId);
            praise.setPraiseOpenId(fromOpenId);
            praise.setPraiseTime(new Date());
            Integer addCount = praiseService.insertPraise(praise);
            if(!globalResult.checkOutAdd(globalResult,addCount)){
                logger.error("点赞接口落库点赞表失败");
                return globalResult;
            }

            //消息通知表落库
            Message message = new Message();
            message.setComRepPraId(commentId);
            message.setComRepPraType("comment");
            message.setFromOpenId(fromOpenId);
            message.setMessageType("praise");
            message.setToOpenId(toOpenId);
            message.setIsRead("0");
            message.setMesTime(new Date());

            Integer addMesCount = messageService.saveMessage(message);
            if(!globalResult.checkOutAdd(globalResult,addMesCount)){
                logger.error("保存消息列表失败 ==>{}", message.toString());
                return globalResult;
            }

            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("isSuccess",true);
            globalResult.setData(dataMap);
            return globalResult;
        }else if(StringUtils.isNotEmpty(replyId) && StringUtils.isEmpty(commentId) && StringUtils.isEmpty(suibiId)){
            //回复表点赞字段加1
            Integer updateCount = replyService.updatePraise(replyId);
            if(!globalResult.checkOutUpdate(globalResult, updateCount)){
                logger.error("点赞接口更新回复表数据库失败");
                return globalResult;
            }
            //落库点赞表
            Praise praise = new Praise();
            praise.setReplyId(replyId);
            praise.setPraiseOpenId(fromOpenId);
            praise.setPraiseTime(new Date());
            Integer addCount = praiseService.insertPraise(praise);

            if(!globalResult.checkOutAdd(globalResult,addCount)){
                logger.error("点赞接口落库点赞表失败");
                return globalResult;
            }

            //消息通知表落库
            Message message = new Message();
            message.setComRepPraId(replyId);
            message.setComRepPraType("reply");
            message.setFromOpenId(fromOpenId);
            message.setMessageType("praise");
            message.setToOpenId(toOpenId);
            message.setIsRead("0");
            message.setMesTime(new Date());

            Integer addMesCount = messageService.saveMessage(message);
            if(!globalResult.checkOutAdd(globalResult,addMesCount)){
                logger.error("保存消息列表失败 ==>{}", message.toString());
                return globalResult;
            }

            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("isSuccess",true);
            globalResult.setData(dataMap);
            return globalResult;
        }else if(StringUtils.isEmpty(replyId) && StringUtils.isEmpty(commentId) && StringUtils.isNotEmpty(suibiId)){
            //随笔表点赞字段加1
            Integer updateCount = suibiService.updatePraise(suibiId);
            if(!globalResult.checkOutUpdate(globalResult, updateCount)){
                logger.error("点赞接口更新随笔表数据库失败");
                return globalResult;
            }
            //落库点赞表
            Praise praise = new Praise();
            praise.setSuibiId(suibiId);
            praise.setPraiseOpenId(fromOpenId);
            praise.setPraiseTime(new Date());
            Integer addCount = praiseService.insertPraise(praise);

            if(!globalResult.checkOutAdd(globalResult,addCount)){
                logger.error("点赞接口落库随笔表失败");
                return globalResult;
            }

            //消息通知表落库
            Message message = new Message();
            message.setComRepPraId(suibiId);
            message.setComRepPraType(CommentType.随笔类型.getMsg());
            message.setFromOpenId(fromOpenId);
            message.setMessageType("praise");
            message.setToOpenId(toOpenId);
            message.setIsRead("0");
            message.setMesTime(new Date());

            Integer addMesCount = messageService.saveMessage(message);
            if(!globalResult.checkOutAdd(globalResult,addMesCount)){
                logger.error("保存消息列表失败 ==>{}", message.toString());
                return globalResult;
            }

            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("isSuccess",true);
            globalResult.setData(dataMap);
            return globalResult;
        }
        globalResult.setCode(ResultCode.传入参数不合法.getCode());
        globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
        return globalResult;
    }


}
