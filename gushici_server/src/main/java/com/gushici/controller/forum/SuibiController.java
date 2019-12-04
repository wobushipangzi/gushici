package com.gushici.controller.forum;

import com.alibaba.fastjson.JSONObject;
import com.gushici.bean.forum.Suibi;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.AliOOS;
import com.gushici.common.smallprogram.Xiaochengxu;
import com.gushici.common.utils.CommonUtils;
import com.gushici.common.utils.OOSUtils;
import com.gushici.common.utils.RedisUtils;
import com.gushici.common.utils.ThreadPool;
import com.gushici.service.suibi.SuibiService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


@RestController
@RequestMapping("/forum")
public class SuibiController {

    private static Logger logger = LoggerFactory.getLogger(SuibiController.class);

    private final SuibiService suibiService;

    @Autowired
    public SuibiController(SuibiService suibiService) {
        this.suibiService = suibiService;
    }

    /**
     * 用户随笔文字图片内容上传接口
     */
    @RequestMapping(value = "/uploadContent", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult uploadContent(@RequestParam String openId, @RequestParam String content, @RequestParam List<MultipartFile> imgList) {
        logger.info("用户{}随笔文字内容上传接口入参为：==>{},imgList{}", openId, content, imgList);
        GlobalResult globalResult = GlobalResult.success();
        try {
            if(StringUtils.isEmpty(content) && (null == imgList || imgList.size() == 0)){
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }
            globalResult = suibiService.saveSuibiContent(openId, content, imgList);

            if (!ResultCode.处理成功.getCode().equals(globalResult.getCode())) {
                logger.error("用户发表随笔接口错误");
                globalResult = GlobalResult.error();
            }
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("用户随笔文字内容上传接口异常", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 新 用户发表随笔接口
     */
    @RequestMapping(value = "/uploadSuibi", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult uploadSuibi(@RequestParam String openId, @RequestParam String content, @RequestParam MultipartFile img,
                                    @RequestParam String suibiId, @RequestParam String imgCount) {
        logger.info("用户{}随笔文字内容上传接口入参为：==>{},img:{},suibiId:{},imgCount:{}", openId, content, img, suibiId, imgCount);
        GlobalResult globalResult = GlobalResult.success();
        try {
            //如果随笔id为空，用户是第一次调用接口，存入openId和内容即可，返回suibiId， 同时将img信息以json的方式存入redis
            if(StringUtils.isEmpty(suibiId)){
                suibiId = String.valueOf(suibiService.saveSuibi(openId, content));

                if(null != img){
                    //将img信息存入redis
                    saveImgToRedis(img, suibiId);
                }
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("suibiId", suibiId);
                globalResult.setData(dataMap);
            }else {
                //如果随笔id不为空，证明不是第一次传入，只将img信息拼接到redis即可
                String key = Xiaochengxu.SUIBI_PERFIX + suibiId;
                String imgsJson = RedisUtils.get(key);
                List imgList = JSONObject.parseObject(imgsJson, List.class);
                Map<String, String> imgMap = new HashMap<>();
                String[] splitArr = img.getOriginalFilename().split("\\.");
                String format = splitArr[splitArr.length - 1];  //文件后缀名
                InputStream imgStream = img.getInputStream();
                String imgBase64 = CommonUtils.getBase64FromInputStream(imgStream);
                imgMap.put("format",format);
                imgMap.put("imgBase64", imgBase64);
                imgList.add(imgMap);
                imgsJson = JSONObject.toJSONString(imgList);
                RedisUtils.setAndTime(key, imgsJson, Xiaochengxu.THREE_MIN_SECONDS);
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("suibiId", suibiId);
                globalResult.setData(dataMap);
            }

            //在redis拿到图片信息，如果长度和imgCount相等，批量操作上传OOS并入库
            if(null != img && StringUtils.isNotEmpty(imgCount)){
                String key = Xiaochengxu.SUIBI_PERFIX + suibiId;
                String imgsJson = RedisUtils.get(key);
                List imgList = JSONObject.parseObject(imgsJson, List.class);
                ExecutorService executorService = ThreadPool.newExecutorInstance();
                if(null != imgList && imgList.size() > 0){
                    if(Integer.valueOf(imgCount) == imgList.size()){
                        ArrayList<Future<GlobalResult>> submitList = new ArrayList<>();
                        //遍历imgList 开线程批量处理
                        for (Object o : imgList) {
                            Map map = JSONObject.parseObject(JSONObject.toJSONString(o), Map.class);
                            String format = String.valueOf(map.get("format"));
                            String imgBase64 = String.valueOf(map.get("imgBase64"));
                            InputStream imgStream = CommonUtils.BaseToInputStream(imgBase64);
                            Future<GlobalResult> submit = executorService.submit(() -> {
                                InputStream inputStream;
                                if (!"MP4".equals(format.toUpperCase()) && !"GIF".equals(format.toUpperCase())) {
                                    //图片添加水印
                                    inputStream = OOSUtils.markImageByMoreIcon(null, null, imgStream, format);
                                } else {
                                    inputStream = imgStream;
                                }
                                GlobalResult result = OOSUtils.uploadToOOS(inputStream, AliOOS.SUIBI_PICTURE, format);
                                if (!ResultCode.处理成功.getCode().equals(result.getCode())) {
                                    logger.error("上传随笔配图至OOS失败");
                                    throw new GlobalException(result.getCode(), result.getMessage());
                                }
                                return result;
                            });
                            submitList.add(submit);
                        }

                        //遍历线程集合 入库
                        ArrayList<String> imgUrlList = new ArrayList<>();
                        for (Future<GlobalResult> resultFuture : submitList) {
                            GlobalResult result = resultFuture.get();
                            //获取随笔图片在OOS的地址
                            Map jsonDataMap = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), Map.class);
                            String imgUrl = jsonDataMap.get("uploadUrl").toString();
                            imgUrlList.add(imgUrl);
                        }
                        String urlJson = JSONObject.toJSONString(imgUrlList);
                        //更新数据库
                        Suibi suibi = new Suibi();
                        suibi.setImgUrl(urlJson);
                        suibi.setSuibiId(Integer.valueOf(suibiId));
                        suibiService.updateSuibi(suibi);
                        RedisUtils.del(key);
                    }
                }
            }
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("用户随笔文字内容上传接口异常", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }

    /**
     * 保存img信息到redis
     * @param img  图片信息
     * @param suibiId  随笔id
     * @throws IOException
     */
    private void saveImgToRedis(@RequestParam MultipartFile img, @RequestParam String suibiId) throws IOException {
        String key = Xiaochengxu.SUIBI_PERFIX + suibiId;
        List<Object> imgList = new ArrayList<>();
        Map<String, String> imgMap = new HashMap<>();
        String[] splitArr = img.getOriginalFilename().split("\\.");
        String format = splitArr[splitArr.length - 1];  //文件后缀名
        InputStream imgStream = img.getInputStream();
        String imgBase64 = CommonUtils.getBase64FromInputStream(imgStream);
        imgMap.put("format",format);
        imgMap.put("imgBase64", imgBase64);
        imgList.add(imgMap);
        String imgsJson = JSONObject.toJSONString(imgList);
        RedisUtils.setAndTime(key, imgsJson, Xiaochengxu.THREE_MIN_SECONDS);
    }


    /**
     * 用户删除随笔接口
     */
    @RequestMapping(value = "/delSuibi", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult delSuibi(@RequestBody String jsonData) {
        logger.info("用户删除随笔接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();

        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String suibiId = jsonObject.getString("suibiId");

            //查询此随笔的所有配图
            Suibi suibi = suibiService.getSuibiBySuibiId(suibiId);
            if (null == suibi) {
                logger.error("没有查到随笔内容");
                globalResult.setCode(ResultCode.该内容不存在.getCode());
                globalResult.setMessage(ResultCode.该内容不存在.getMsg());
                return globalResult;
            }

            List<String> files = new ArrayList<>();
            if(StringUtils.isNotEmpty(suibi.getImgUrl())){
                String imgUrlJson = suibi.getImgUrl();
                List imgUrlList = JSONObject.parseObject(imgUrlJson, List.class);
                for (Object o : imgUrlList) {
                    String imgUrl = String.valueOf(o);
                    if (StringUtils.isNotEmpty(imgUrl) && imgUrl.contains("//gushici.oss-cn-beijing.aliyuncs.com/")) {
                        files.add(imgUrl.split("//gushici.oss-cn-beijing.aliyuncs.com/")[1]);
                    }
                }
            }

            //删除数据库和OOS中的随笔
            globalResult = suibiService.delSuibiBySuibiId(suibiId, files);
            if (!ResultCode.处理成功.getCode().equals(globalResult.getCode())) {
                globalResult = GlobalResult.error();
            }
        }catch (GlobalException e){
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("用户删除随笔接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }



    /**
     * 获取推荐页数据接口
     */
    @RequestMapping(value = "/getRecommend", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getRecommend(@RequestBody String jsonData) {
        logger.info("获取推荐页数据接口入参为：==>{}", jsonData);
        GlobalResult globalResult;
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String lastSuibiId = jsonObject.getString("lastSuibiId");

            //查询10条随笔数据
            globalResult = suibiService.getFifSuibis(lastSuibiId);
            //logger.info("获取推荐页数据接口出参为：==>{}", JSONObject.toJSONString(globalResult));
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用获取推荐页数据失败");
                globalResult = GlobalResult.error();
            }
        }catch (Exception e){
            logger.error("获取推荐页数据接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


}
