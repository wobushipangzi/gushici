package com.gushici.controller.user;


import com.alibaba.fastjson.JSONObject;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.AliOOS;
import com.gushici.common.smallprogram.Xiaochengxu;
import com.gushici.common.utils.*;
import com.gushici.bean.user.Report;
import com.gushici.bean.user.User;
import com.gushici.service.suibi.CommentService;
import com.gushici.service.suibi.ReplyService;
import com.gushici.service.suibi.ReportService;
import com.gushici.service.user.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final CommentService commentService;

    private final ReplyService replyService;

    private final AttentionService attentionService;

    private final FeedbackService feedbackService;

    private final MessageService messageService;

    private final ReportService reportService;

    @Autowired
    public UserController(UserService userService, CommentService commentService, ReplyService replyService,AttentionService attentionService,
                          FeedbackService feedbackService, MessageService messageService, ReportService reportService) {
        this.userService = userService;
        this.commentService = commentService;
        this.replyService = replyService;
        this.attentionService = attentionService;
        this.feedbackService = feedbackService;
        this.messageService = messageService;
        this.reportService = reportService;
    }

    /**
     * 检查登录接口
     *
     * yes: 已登录
     * no:  未登录
     */
    @RequestMapping(value = "/checkLogin", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult checkLogin(@RequestBody String jsonData){
        logger.info("检查登录接口入参为：==>" + jsonData);
        GlobalResult globalResult = GlobalResult.success();
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String loginToken = jsonObject.getString("loginToken");

            HashMap<String, String> checkLoginMap = new HashMap<>();
            if(StringUtils.isEmpty(loginToken)){
                checkLoginMap.put("isLogin","no");
                globalResult.setData(checkLoginMap);
                return globalResult;
            }

            //在redis检查loginToken是否存在，如果不存在，返回登录失效
            String key = Xiaochengxu.PERFIX + loginToken.split("_")[0];
            String loginTokenValue = RedisUtils.get(key);

            if(StringUtils.isEmpty(loginTokenValue)){
                checkLoginMap.put("isLogin","no");
                globalResult.setData(checkLoginMap);
                return globalResult;
            }

            //如果存在，校验是否一致，如果不一致，返回登录失效，删除redis数据
            if(!(loginToken.equals(loginTokenValue))){
                RedisUtils.del(key);
                checkLoginMap.put("isLogin","no");
                globalResult.setData(checkLoginMap);
                return globalResult;
            }

            //如果一致，更新失效时间
            RedisUtils.expireKey(key,Xiaochengxu.FIFTEEN_DAY);

            String[] loginTokenSplit = loginToken.split("_");
            String openId = loginTokenSplit[0];

            //TODO  获取上一次登录时间  对用户问候，暂时先不上

            //将登陆时间设置进去
            User user = new User();
            user.setOpenId(openId);
            user.setLoginTime(new Date());
            globalResult = userService.updateUser(user);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用更新用户接口失败");
                return GlobalResult.error();
            }
            checkLoginMap.put("isLogin","yes");
            globalResult.setData(checkLoginMap);
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("检查登录接口异常", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 用户登录接口
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult login(@RequestBody String jsonData){

        logger.info("用户登录接口入参为：==>" + jsonData);
        GlobalResult globalResult = GlobalResult.success();
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String js_code = jsonObject.getString("code");
            String appid = Xiaochengxu.APP_ID;
            String secret = Xiaochengxu.APPSECRET;
            String grant_type = Xiaochengxu.GRANT_TYPE;

            String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+appid+"&secret="+secret+"&js_code="+js_code+"&grant_type="+grant_type;
            String userJson = HttpClientUtils.httpPostRequest(url);
            logger.info("微信服务器返回数据为：==>" + userJson);

            if(StringUtils.isEmpty(userJson)){
                logger.error("调用微信登录时服务端请求微信服务器失败");
                globalResult.setCode(ResultCode.服务端请求微信服务器失败.getCode());
                globalResult.setMessage(ResultCode.服务端请求微信服务器失败.getMsg());
                return globalResult;
            }

            JSONObject userJsonObject = JSONObject.parseObject(userJson);

            String openId = userJsonObject.getString("openid");
            String sessionKey = userJsonObject.getString("session_key");

            User user = new User();
            //如果数据库能获取到openId  更新登录时间 并设置redis过期时间
            User userInfo = userService.selectUserOne(openId);
            if(null == userInfo || StringUtils.isEmpty(userInfo.getOpenId())){
                //如果获取不到 将openID  注册时间，登录时间，修改时间 存入数据库
                user.setLoginTime(new Date());
                user.setRegisterTime(new Date());
                user.setUpdateTime(new Date());
                user.setBlacklist(0);
                user.setIsDelete(0);
                user.setLevel(1);
                user.setGender(0);
                user.setOpenId(openId);
                globalResult = userService.saveUser(user);

                if (!ResultCode.处理成功.getCode().equals(globalResult.getCode())) {
                    logger.error("保存用户信息openId至数据库失败");
                    return GlobalResult.error();
                }
            }

            //将信息存入redis  过期时间15天
            String key = Xiaochengxu.PERFIX + openId;
            String value = openId + "_" + sessionKey;
            RedisUtils.setAndTime(key,value,Xiaochengxu.FIFTEEN_DAY);

            user.setOpenId(openId);
            user.setLoginTime(new Date());
            globalResult = userService.updateUser(user);
            if (!ResultCode.处理成功.getCode().equals(globalResult.getCode())) {
                logger.error("根据openId更新用户信息至数据库失败");
                return GlobalResult.error();
            }

            //把信息传回给前端
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("loginToken",value);
            dataMap.put("sessionKey",sessionKey);
            dataMap.put("openId",openId);
            globalResult.setData(dataMap);
            logger.info("登录接口返参为：==>" + JSONObject.toJSONString(globalResult));
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("用户登录接口异常", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 获取用户信息接口
     * yes: 获取成功  no:获取失败
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult getUserInfo(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("获取用户信息入参为：==>" + jsonData);

        try {
            if(StringUtils.isEmpty(jsonData)){
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String encryptedData = jsonObject.getString("encryptedData");
            String sessionKey = jsonObject.getString("sessionKey");
            String iv = jsonObject.getString("iv");

            if(StringUtils.isEmpty(encryptedData) || StringUtils.isEmpty(sessionKey) || StringUtils.isEmpty(iv)){
                logger.error("获取用户信息入参不合法" + jsonData);
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }

            logger.info("开始解密==>" + jsonData);
            JSONObject userInfo = DecodeUserInfoUtils.getUserInfo(encryptedData, sessionKey, iv);
            logger.info("解密完成==>" + JSONObject.toJSONString(userInfo));

            HashMap<String, String> dataMap = new HashMap<>();
            if(null == userInfo){
                globalResult.setCode(ResultCode.解密用户信息失败.getCode());
                globalResult.setMessage(ResultCode.解密用户信息失败.getMsg());
                dataMap.put("isSuccess","no");
                globalResult.setData(dataMap);
                return globalResult;
            }

            User newUser = JSONObject.parseObject(JSONObject.toJSONString(userInfo), User.class);

            User oldUser = userService.selectUserOne(newUser.getOpenId());
            if(null == oldUser.getName()){
                oldUser.setName(newUser.getNickName());
            }
            oldUser.setOpenId(newUser.getOpenId());
            oldUser.setNickName(newUser.getNickName());

            if("0".equals(String.valueOf(oldUser.getGender()))){
                oldUser.setGender(newUser.getGender());
            }

            oldUser.setCity(newUser.getCity());
            oldUser.setProvince(newUser.getProvince());
            oldUser.setCountry(newUser.getCountry());
            oldUser.setAvatarUrl(newUser.getAvatarUrl());

            if(null == oldUser.getBirthday()){
                oldUser.setBirthday(new Date());
            }
            if(null == oldUser.getHeadPhotoUrl()){
                oldUser.setHeadPhotoUrl(newUser.getAvatarUrl());
            }

            oldUser.setUnionId(newUser.getUnionId());
            //更新数据库
            globalResult = userService.updateUser(oldUser);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用更新用户信息接口失败");
                return GlobalResult.error();
            }

            dataMap.put("isSuccess","yes");
            globalResult.setData(dataMap);
            logger.info("获取用户信息接口返参为：==>" + JSONObject.toJSONString(globalResult));
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("获取用户信息接口异常", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 保存用户信息接口
     */
    @RequestMapping(value = "/saveUserInfo", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult saveUserInfo(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("保存用户信息接口入参为：==>" + jsonData);

        try {
            if(StringUtils.isEmpty(jsonData)){
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }

            JSONObject jsonObject = JSONObject.parseObject(jsonData);

            String openId = jsonObject.getString("openId");
            String nickName = jsonObject.getString("nickName");
            String gender = jsonObject.getString("gender");
            String city = jsonObject.getString("city");
            String province = jsonObject.getString("province");
            String country = jsonObject.getString("country");
            String avatarUrl = jsonObject.getString("avatarUrl");

            User user = userService.selectUserOne(openId);
            if(null == user.getName()){
                user.setName(nickName);
            }

            user.setOpenId(openId);
            user.setNickName(nickName);

            if("0".equals(String.valueOf(user.getGender()))){
                user.setGender(Integer.valueOf(gender));
            }

            user.setCity(city);
            user.setProvince(province);
            user.setCountry(country);
            user.setAvatarUrl(avatarUrl);

            if(null == user.getHeadPhotoUrl()){
                user.setHeadPhotoUrl(avatarUrl);
            }

            //更新数据库
            globalResult = userService.updateUser(user);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用更新用户信息服务失败");
                return GlobalResult.error();
            }

            HashMap<String, String> dateMap = new HashMap<>();
            dateMap.put("isSuccess","yes");
            globalResult.setData(dateMap);
            logger.info("保存用户信息接口返参为：==>" + JSONObject.toJSONString(globalResult));
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("保存用户信息接口异常", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }



    /**
     * 个人主页获赞数 关注数 粉丝数接口
     */
    @RequestMapping(value = "/getSumPraAttFan", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getSumPraAttFan(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("个人主页获取总点赞数入参为：==>" + jsonData);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String openId = jsonObject.getString("openId");
        if(StringUtils.isEmpty(openId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        //获取总赞数
        Integer commentPraiseByOpenId = commentService.getCommentPraiseByOpenId(openId);
        Integer replyPraiseByOpenId = replyService.getReplyPraiseByOpenId(openId);
        Integer praiseCount = commentPraiseByOpenId + replyPraiseByOpenId;
        logger.info(openId + "获赞总数为：==>" + praiseCount);

        //获取粉丝数
        Integer fansCount = attentionService.getFansCount(openId);

        //获取总关注数
        Integer attentionCount = attentionService.getAttentionCount(openId);

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("praiseCount",String.valueOf(praiseCount));
        dataMap.put("fansCount",String.valueOf(fansCount));
        dataMap.put("attentionCount",String.valueOf(attentionCount));

        globalResult.setData(dataMap);
        return globalResult;
    }


    /**
     * 用户反馈接口
     */
    @RequestMapping(value = "/feedBack", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult feedBack(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("用户反馈接口入参为：==>" + jsonData);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String openId = jsonObject.getString("openId");
        String content = jsonObject.getString("content");

        if(StringUtils.isEmpty(openId) || StringUtils.isEmpty(content)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        //落库
        Integer addCount = feedbackService.saveFeedback(openId, content);
        if(!globalResult.checkOutAdd(globalResult,addCount)){
            logger.error("用户反馈接口落库失败");
            return globalResult;
        }

        //返回用户性别
        User user = userService.selectUserOne(openId);
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("gender",String.valueOf(user.getGender()));
        globalResult.setData(dataMap);
        return globalResult;
    }


    /**
     * 检查消息接口
     */
    @RequestMapping(value = "/checkMsg", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult checkMsg(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("检查消息接口入参为：==>" + jsonData);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        String openId = jsonObject.getString("openId");

        Map<String, String> newMsg = messageService.getMsgCount(openId);

        globalResult.setData(newMsg);

        logger.info("检查消息接口出参为：==>" + JSONObject.toJSONString(globalResult));
        return globalResult;
    }



    /**
     * 获取被赞消息/被评论消息列表接口
     */
    @RequestMapping(value = "/getMsgList", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getMsgList(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("检查消息接口入参为：==>" + jsonData);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        String openId = jsonObject.getString("openId");
        String messageType = jsonObject.getString("messageType");

        List<Map<String, String>> msgList = messageService.getMsgList(openId, messageType);

        globalResult.setData(msgList);
        return globalResult;
    }


    /**
     * 清空被赞消息/被评论消息列表接口
     */
    @RequestMapping(value = "/delMsgList", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult delMsgList(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("检查消息接口入参为：==>" + jsonData);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);

        String messageType = jsonObject.getString("messageType");
        String openId = jsonObject.getString("openId");

        Integer integer = messageService.delMessageList(messageType, openId);
        if(integer < 0){
            logger.error("清空被赞消息/被评论消息列表接口删除数据失败");
            globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
            return globalResult;
        }
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("isSuccess","yes");
        globalResult.setData(dataMap);
        return globalResult;
    }


    /**
     * 个人中心页面获取用户信息
     */
    @RequestMapping(value  = "/getUser", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getUser(@RequestBody String jsonData){
        GlobalResult globalResult = GlobalResult.success();
        logger.info("个人中心页面获取用户信息接口入参为：==>" + jsonData);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String openId = jsonObject.getString("openId");
        User user = userService.selectUserOne(openId);
        if(null != user){
            HashMap<String, String> userMap = new HashMap<>();
            userMap.put("name",user.getName());
            userMap.put("headPhotoUrl",user.getHeadPhotoUrl());
            userMap.put("gender",String.valueOf(user.getGender()));
            userMap.put("signature",user.getSignature());
            userMap.put("level",String.valueOf(user.getLevel()));
            userMap.put("age",DateTimeUtils.computeAge(user.getBirthday()));
            globalResult.setData(userMap);
            return globalResult;
        }else {
            logger.error("个人中心页面获取用户信息接口获取用户信息失败");
            globalResult.setCode(ResultCode.获取用户信息失败.getCode());
            globalResult.setMessage(ResultCode.获取用户信息失败.getMsg());
            return globalResult;
        }
    }


    /**
     * 用户头像上传接口
     */
    @RequestMapping(value = "/uploadHeadPhoto", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult uploadHeadPhoto(@RequestParam String openId, @RequestParam MultipartFile headPhoto){
        logger.info("用户头像上传接口入参为：==>" + openId);

        try {
            InputStream inputStream = headPhoto.getInputStream();
            String[] splitArr = headPhoto.getOriginalFilename().split("\\.");
            String format = splitArr[splitArr.length - 1];
            //上传用户头像到OOS
            GlobalResult globalResult = OOSUtils.uploadToOOS(inputStream, AliOOS.HEAD_PHOTO, format);

            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("上传用户头像到OOS失败");
                return GlobalResult.error();
            }

            //获取用户头像在OOS的路径
            Map headPhotoMap = JSONObject.parseObject(JSONObject.toJSONString(globalResult.getData()), Map.class);
            String headPhotoUrl = headPhotoMap.get("uploadUrl").toString();

            //将头像路径保存到本地
            User user = new User();
            user.setHeadPhotoUrl(headPhotoUrl);
            user.setOpenId(openId);
            user.setUpdateTime(new Date());
            globalResult = userService.updateUser(user);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("更新用户信息失败");
                return GlobalResult.error();
            }

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("isSuccess", "yes");
            globalResult.setData(hashMap);
            return globalResult;
        }catch (GlobalException e){
            return GlobalResult.error();
        }catch (Exception e){
            logger.error("用户头像上传接口获取输入流失败" + e);
            return GlobalResult.error();
        }
    }



    /**
     * 用户修改资料接口
     */
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult updateUserInfo(@RequestBody String jsonData){
        logger.info("用户修改资料接口入参为：==>" + jsonData);
        GlobalResult globalResult ;
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        //可修改资料有姓名 年龄 性别 生日 国家 省 城市 个性签名
        String openId = jsonObject.getString("openId");
        String name = jsonObject.getString("name");
        String gender = jsonObject.getString("gender");
        String birthday = jsonObject.getString("birthday");
        String signature = jsonObject.getString("signature");

        User user = new User();
        user.setOpenId(openId);
        user.setName(name);
        user.setGender(Integer.valueOf(gender));
        user.setBirthday(DateTimeUtils.transitionDate(birthday));
        user.setSignature(signature);
        user.setUpdateTime(new Date());

        globalResult = userService.updateUser(user);
        if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            logger.error("用户修改资料接口更新数据库失败");
            return GlobalResult.error();
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("isSuccess", "yes");
        globalResult.setData(hashMap);

        return globalResult;
    }


    /**
     * 举报接口上传基本信息
     */
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult report(@RequestBody String jsonData) {
        logger.info("举报接口入参为：==>" + jsonData);
        GlobalResult globalResult = GlobalResult.success();
        try {
            if(StringUtils.isEmpty(jsonData)){
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }
            //入库举报基本信息
            globalResult = reportService.saveReport(jsonData);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用举报信息入库接口失败");
                globalResult = GlobalResult.error();
            }
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("举报接口上传基本信息异常", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }




    /**
     * 用户举报截图上传接口
     */
    @RequestMapping(value = "/uploadReportImg", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult uploadReportImg(@RequestParam String reportId, @RequestParam MultipartFile reportImg){
        logger.info("用户举报截图上传接口入参为：==>" + reportId);
        try {
            InputStream inputStream = reportImg.getInputStream();
            String[] splitArr = reportImg.getOriginalFilename().split("\\.");
            String format = splitArr[splitArr.length - 1];
            //上传举报截图到OOS
            GlobalResult globalResult = OOSUtils.uploadToOOS(inputStream, AliOOS.REPORT_PICTURE, format);

            if (ResultCode.处理成功.getCode().equals(globalResult.getCode())) {
                //获取图片在OOS的地址
                Map jsonDataMap = JSONObject.parseObject(JSONObject.toJSONString(globalResult.getData()), Map.class);
                String uploadUrl = jsonDataMap.get("uploadUrl").toString();

                //举报截图地址入库
                Integer updateCount = reportService.updateReport(reportId, uploadUrl);

                Boolean flag = globalResult.checkOutUpdate(globalResult, updateCount);
                if (!flag) {
                    logger.error("用户举报截图入库失败");
                    globalResult.setCode(ResultCode.更新数据库失败.getCode());
                    globalResult.setMessage(ResultCode.更新数据库失败.getMsg());
                    return globalResult;
                }
            }
            return globalResult;
        } catch (IOException e) {
            logger.error("上传举报截图文件转输入流失败" + e);
            return GlobalResult.error();
        }
    }
}
