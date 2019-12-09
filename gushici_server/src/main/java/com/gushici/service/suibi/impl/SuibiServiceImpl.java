package com.gushici.service.suibi.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gushici.bean.forum.Suibi;
import com.gushici.bean.product.Product;
import com.gushici.bean.user.*;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.AliOOS;
import com.gushici.common.smallprogram.CommentType;
import com.gushici.common.smallprogram.OnePageCount;
import com.gushici.common.utils.DateTimeUtils;
import com.gushici.common.utils.OOSUtils;
import com.gushici.common.utils.ThreadPool;
import com.gushici.mapper.suibi.*;
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

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private PraiseMapper praiseMapper;


    /**
     * 随笔文字配图内容入库
     * @param openId   用户openId
     * @param content   随笔的文字内容
     * @return GlobalResult
     */
    @Override
    @Transactional
    public GlobalResult saveSuibiContent(String openId, String content) {
        GlobalResult globalResult = GlobalResult.success();
        try {
            Date date = new Date();
            Suibi suibi = new Suibi();
            suibi.setContent(content);
            suibi.setOpenId(openId);
            suibi.setSuibiTime(date);
            suibi.setIsGood("0");
            suibi.setCommentCount("0");
            suibi.setPraiseCount("0");

            int insert = suibiMapper.insertSuibi(suibi);
            if(insert <= 0){
                logger.error("随笔文字入库失败");
                throw new GlobalException(ResultCode.系统错误.getCode(), ResultCode.系统错误.getMsg());
            }
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("suibiId",String.valueOf(suibi.getSuibiId()));
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
    public GlobalResult getFifSuibis(String lastSuibiId, String openId) {
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

            //批量查询user
            ArrayList<String> openIdList = new ArrayList<>();
            ArrayList<String> suibiIdList = new ArrayList<>();
            for (Suibi suibi : suibis) {
                openIdList.add(suibi.getOpenId());
                suibiIdList.add(String.valueOf(suibi.getSuibiId()));
            }
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.in("open_id",openIdList);
            List<User> users = userMapper.selectList(userQueryWrapper);

            //批量查询举报信息
            QueryWrapper<Report> reportQueryWrapper = new QueryWrapper<>();
            reportQueryWrapper.eq("from_open_id", openId);  //本用户openId
            reportQueryWrapper.in("suibi_id", suibiIdList);
            List<Report> reports = reportMapper.selectList(reportQueryWrapper);

            //批量查询点赞
            QueryWrapper<Praise> praiseQueryWrapper = new QueryWrapper<>();
            praiseQueryWrapper.eq("praise_open_id", openId);
            reportQueryWrapper.in("suibi_id", suibiIdList);
            List<Praise> praises = praiseMapper.selectList(praiseQueryWrapper);

            for (Suibi suibi : suibis) {
                Map<String, Object> suibiHashMap = new HashMap<>();
                suibiHashMap.put("suibiId", String.valueOf(suibi.getSuibiId()));
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
                suibiHashMap.put("isGood", StringUtils.isBlank(suibi.getIsGood())? 0 : Integer.valueOf(suibi.getIsGood()));
                suibiHashMap.put("isReport", 0);
                suibiHashMap.put("isPraised", 0);
                suibiHashMap.put("commentCount", StringUtils.isEmpty(suibi.getCommentCount()) ? 0 : Integer.valueOf(suibi.getCommentCount()));
                suibiHashMap.put("praiseCount", StringUtils.isEmpty(suibi.getPraiseCount()) ? 0 : Integer.valueOf(suibi.getPraiseCount()));

                //合并用户相关信息
                if(null != users && users.size() > 0){
                    for (User user : users) {
                        if(suibi.getOpenId().equals(user.getOpenId())){
                            suibiHashMap.put("openId", user.getOpenId());
                            suibiHashMap.put("userName", user.getName());
                            suibiHashMap.put("headPhoto", user.getHeadPhotoUrl());
                            break;
                        }
                    }
                }

                //判断该随笔有没有被使用小程序的用户举报
                if(null != reports && reports.size() > 0){
                    for (Report report : reports) {
                        if(String.valueOf(suibi.getSuibiId()).equals(report.getSuibiId())){
                            suibiHashMap.put("isReport", 1);
                            break;
                        }
                    }
                }

                //判断该随笔有没有被使用小程序的用户点赞
                if(null != praises && praises.size() > 0){
                    for (Praise praise : praises) {
                        if(String.valueOf(suibi.getSuibiId()).equals(praise.getSuibiId())){
                            suibiHashMap.put("isPraised", 1);
                            break;
                        }
                    }
                }
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


    /**
     * 保存随笔内容和openId
     * @return
     */
    @Override
    public Integer saveSuibi(String openId, String content) {
        Suibi suibi = new Suibi();
        suibi.setOpenId(openId);
        suibi.setContent(content);
        suibi.setSuibiTime(new Date());
        suibi.setPraiseCount("0");
        suibi.setIsGood("0");
        suibi.setCommentCount("0");
        suibiMapper.insertSuibi(suibi);
        return suibi.getSuibiId();
    }

    /**
     * 更新随笔内容
     * @param suibi
     * @return
     */
    @Override
    public Integer updateSuibi(Suibi suibi) {
        QueryWrapper<Suibi> suibiQueryWrapper = new QueryWrapper<>();
        suibiQueryWrapper.eq("suibi_id",suibi.getSuibiId());
        int update = suibiMapper.update(suibi, suibiQueryWrapper);
        if(update <= 0){
            throw new GlobalException(ResultCode.更新数据库失败.getCode(), ResultCode.更新数据库失败.getMsg());
        }
        return update;
    }


}
