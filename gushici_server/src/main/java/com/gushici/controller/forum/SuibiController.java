package com.gushici.controller.forum;

import com.alibaba.fastjson.JSONObject;
import com.gushici.bean.forum.Suibi;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.service.suibi.SuibiService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


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
        logger.info("用户{}随笔文字内容上传接口入参为：==>{},imgList{}", openId, content, JSONObject.toJSONString(imgList));
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
            String imgUrlJson = suibi.getImgUrl();
            List imgUrlList = JSONObject.parseObject(imgUrlJson, List.class);
            for (Object o : imgUrlList) {
                String imgUrl = String.valueOf(o);
                if (StringUtils.isNotEmpty(imgUrl) && imgUrl.contains("//gushici.oss-cn-beijing.aliyuncs.com/")) {
                    files.add(imgUrl.split("//gushici.oss-cn-beijing.aliyuncs.com/")[1]);
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
            logger.info("获取推荐页数据接口出参为：==>{}", JSONObject.toJSONString(globalResult));
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
