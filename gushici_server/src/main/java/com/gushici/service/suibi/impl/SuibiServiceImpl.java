package com.gushici.service.suibi.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.forum.Suibi;
import com.gushici.bean.product.Product;
import com.gushici.bean.user.Comment;
import com.gushici.bean.user.Reply;
import com.gushici.bean.user.User;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.AliOOS;
import com.gushici.common.smallprogram.CommentType;
import com.gushici.common.smallprogram.OnePageCount;
import com.gushici.common.utils.DateTimeUtils;
import com.gushici.common.utils.OOSUtils;
import com.gushici.common.utils.ThreadPool;
import com.gushici.mapper.suibi.SuibiMapper;
import com.gushici.mapper.suibi.CommentMapper;
import com.gushici.mapper.suibi.ReplyMapper;
import com.gushici.mapper.user.UserMapper;
import com.gushici.service.suibi.SuibiService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SuibiServiceImpl implements SuibiService {

    private static Logger logger = LoggerFactory.getLogger(SuibiServiceImpl.class);

    private final SuibiMapper suibiMapper;

    private final UserMapper userMapper;

    private final CommentMapper commentMapper;

    private final ReplyMapper replyMapper;

    @Autowired
    public SuibiServiceImpl(SuibiMapper suibiMapper, UserMapper userMapper, CommentMapper commentMapper, ReplyMapper replyMapper) {
        this.suibiMapper = suibiMapper;
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
        this.replyMapper = replyMapper;
    }

    @Autowired
    private SuibiService suibiService;


    /**
     * 随笔文字配图内容入库
     * @param openId   用户openId
     * @param content   随笔的文字内容
     * @param imgList   随笔配图
     * @return GlobalResult
     */
    @Override
    @Transactional
    public GlobalResult saveSuibiContent(String openId, String content, List<MultipartFile> imgList) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            Date date = new Date();
            Suibi suibi = new Suibi();
            suibi.setContent(content);
            suibi.setOpenId(openId);
            suibi.setSuibiTime(date);

            ExecutorService executorService = ThreadPool.newExecutorInstance();
            List<Future> taskList = new ArrayList<>();
            for (MultipartFile img : imgList) {
                Future<GlobalResult> submit = executorService.submit(() -> {
                    GlobalResult result;
                    //获取文件后缀名
                    String[] splitArr = img.getOriginalFilename().split("\\.");
                    String format = splitArr[splitArr.length - 1];
                    if(!"MP4".equals(format.toUpperCase()) && !"GIF".equals(format.toUpperCase())){
                        //图片添加水印
                        InputStream inputStream = OOSUtils.markImageByMoreIcon(img, null);
                        //上传添加水印后的图片到OOS
                        result = OOSUtils.uploadToOOS(inputStream, AliOOS.SUIBI_PICTURE, format);
                        if (!ResultCode.处理成功.getCode().equals(result.getCode())) {
                            logger.error("上传随笔配图至OOS失败");
                            throw new GlobalException(result.getCode(), result.getMessage());
                        }
                    }else {
                        //上传添加水印后的图片到OOS
                        result = OOSUtils.uploadToOOS(img.getInputStream(), AliOOS.SUIBI_PICTURE, format);
                        if (!ResultCode.处理成功.getCode().equals(result.getCode())) {
                            logger.error("上传随笔配图至OOS失败");
                            throw new GlobalException(result.getCode(), result.getMessage());
                        }
                    }
                    return result;
                });
                taskList.add(submit);
            }

            ArrayList<String> imgUrlList = new ArrayList<>();
            for (Future future : taskList) {
                try {
                    Object o = future.get();
                    GlobalResult result = JSONObject.parseObject(JSONObject.toJSONString(o), GlobalResult.class);
                    //获取随笔图片在OOS的地址
                    Map jsonDataMap = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), Map.class);
                    String imgUrl = jsonDataMap.get("uploadUrl").toString();
                    imgUrlList.add(imgUrl);
                } catch (InterruptedException e) {
                    logger.error("线程挂起异常", e);
                    throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
                } catch (ExecutionException e) {
                    logger.error("线程执行异常", e);
                    throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
                }
            }

            String urlJson = JSONObject.toJSONString(imgUrlList);
            suibi.setImgUrl(urlJson);
            int insert = suibiMapper.insert(suibi);

            if(insert <= 0){
                logger.error("随笔文字入库失败");
                throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
            }
        }catch (Exception e){
            logger.error("上传随笔接口异常", e);
            throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
        }
        return globalResult;
    }


    /**
     * 查询十条随笔数据
     * @param lastSuibiId   前端返回的最后一条数据的id
     */
    @Override
    public GlobalResult getFifSuibis(String lastSuibiId) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            List<Object> suibiDetails = new ArrayList<>();
            List<Suibi> suibis;
            if(StringUtils.isEmpty(lastSuibiId)){
                QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
                suibiQueryWrapper.orderByDesc("suibi_time").last("LIMIT " + OnePageCount.SUIBI_COUNT_ONE_PAGE);
                suibis = suibiMapper.selectList(suibiQueryWrapper);
            }else {
                QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
                suibiQueryWrapper.lt("suibi_id",lastSuibiId).orderByDesc("suibi_time").last("LIMIT " + OnePageCount.SUIBI_COUNT_ONE_PAGE);
                suibis = suibiMapper.selectList(suibiQueryWrapper);
            }

            for (Suibi suibi : suibis) {
                Map<String, Object> suibiHashMap = new HashMap<>();
                suibiHashMap.put("suibiId", String.valueOf(suibi.getSuibiId()));
                suibiHashMap.put("praiseCount", suibi.getPraiseCount());
                if (StringUtils.isNotEmpty(suibi.getContent())) {
                    suibiHashMap.put("content", suibi.getContent());
                }

                List imgList = new ArrayList<>();
                if(StringUtils.isNotBlank(suibi.getImgUrl())){
                    imgList = JSONObject.parseObject(suibi.getImgUrl(), List.class);
                }

                suibiHashMap.put("imgList",imgList);
                String suibiTime = DateTimeUtils.computeTime(suibi.getSuibiTime().getTime());
                suibiHashMap.put("suibiTime", suibiTime);
                suibiHashMap.put("isGood", suibi.getIsGood());

                //查询发表人的相关信息
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("open_id", suibi.getOpenId());
                User user = userMapper.selectOne(userQueryWrapper);
                suibiHashMap.put("openId", user.getOpenId());
                suibiHashMap.put("userName", user.getName());
                suibiHashMap.put("headPhoto", user.getHeadPhotoUrl());

                suibiHashMap.put("commentCount", StringUtils.isEmpty(suibi.getCommentCount()) ? "0" : suibi.getCommentCount());
                suibiHashMap.put("praiseCount", StringUtils.isEmpty(suibi.getPraiseCount()) ? "0" : suibi.getPraiseCount());

                suibiDetails.add(suibiHashMap);
            }
            globalResult.setData(suibiDetails);
        }catch (Exception e){
            logger.error("获取随笔内容接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 更新随笔表的点赞数
     * @param suibiId    随笔id
     * @return
     */
    @Override
    public synchronized Integer updatePraise(String suibiId) {
        QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
        suibiQueryWrapper.eq("suibi_id",suibiId);
        Suibi suibi = suibiMapper.selectOne(suibiQueryWrapper);

        if(StringUtils.isEmpty(suibi.getPraiseCount())){
            suibi.setPraiseCount(String.valueOf(1));
        }else {
            AtomicInteger praiseCount = new AtomicInteger(Integer.valueOf(suibi.getPraiseCount()));
            praiseCount.getAndAdd(1);
            suibi.setPraiseCount(String.valueOf(praiseCount.get()));
        }

        return suibiMapper.update(suibi, suibiQueryWrapper);
    }


    /**
     * 查询随笔
     * @param openId  用户openID
     * @param date   日期
     * @return
     */
    @Override
    public Suibi getSuibiOne(String openId, Date date) {
        QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
        suibiQueryWrapper.eq("open_id",openId);
        suibiQueryWrapper.eq("suibi_time",date);
        return suibiMapper.selectOne(suibiQueryWrapper);
    }


    /**
     * 通过随笔id查询随笔
     * @param suibiId
     * @return
     */
    @Override
    public Suibi getSuibiBySuibiId(String suibiId) {
        QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
        suibiQueryWrapper.eq("suibi_id",suibiId);
        return suibiMapper.selectOne(suibiQueryWrapper);
    }


    /**
     * 通过随笔id删除随笔记录
     * @param suibiId  随笔id
     * @return
     */
    @Override
    @Transactional
    public GlobalResult delSuibiBySuibiId(String suibiId, List<String> files) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            if(files != null && files.size() > 0){
                globalResult = OOSUtils.delToOOS(files);
                if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                    logger.error("通过随笔id删除随笔记录接口OOS删除数据失败，执行结果为{}", JSONObject.toJSONString(globalResult));
                    throw new GlobalException(ResultCode.OOS删除数据失败.getCode(),ResultCode.OOS删除数据失败.getMsg());
                }
            }

            QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
            suibiQueryWrapper.eq("suibi_id",suibiId);
            int delete = suibiMapper.delete(suibiQueryWrapper);
            if(delete <= 0){
                logger.error("通过随笔id删除随笔记录接口数据库删除数据失败，执行结果为{}", delete);
                throw new GlobalException(ResultCode.数据库删除数据失败.getCode(),ResultCode.数据库删除数据失败.getMsg());
            }


            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("isSuccess","yes");
            globalResult.setData(dataMap);
        }catch (Exception e){
            logger.error("通过随笔id删除随笔记录接口异常{}", e);
            throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
        }
        return globalResult;
    }


    /**
     * 相关随笔的评论数加1
     * @param topicId  随笔id
     */
    @Override
    public Integer addCommentCount(String topicId) {
        QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
        suibiQueryWrapper.eq("suibi_id",topicId);
        suibiQueryWrapper.select("comment_count");
        Suibi suibi = suibiMapper.selectOne(suibiQueryWrapper);
        String commentCountStr = suibi.getCommentCount();
        Integer commentCount;
        if(StringUtils.isEmpty(commentCountStr)){
            AtomicInteger atomicInt = new AtomicInteger(0);
            atomicInt.getAndAdd(1);
            commentCount = atomicInt.get();
        }else {
            AtomicInteger atomicInteger = new AtomicInteger(Integer.valueOf(commentCountStr));
            atomicInteger.getAndAdd(1);
            commentCount = atomicInteger.get();
        }
        QueryWrapper<Suibi> suibiUpdate = new QueryWrapper<>();
        suibiUpdate.eq("suibi_id",topicId);
        suibi.setCommentCount(String.valueOf(commentCount));
        return suibiMapper.update(suibi,suibiUpdate);
    }


}
