package com.gushici.controller.user;


import com.alibaba.fastjson.JSONObject;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.service.user.AttentionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attention")
public class AttentionController {

    private static Logger logger = Logger.getLogger(AttentionController.class);

    private final AttentionService attentionService;

    @Autowired
    public AttentionController(AttentionService attentionService) {
        this.attentionService = attentionService;
    }

    /**
     * 点击关注接口
     */
    @RequestMapping(value = "/addAttention", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult addAttention(@RequestBody String jsonData){
        logger.info("点击关注接口入参为：==>" + jsonData);
        GlobalResult globalResult = GlobalResult.success();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String attentionOpenId = jsonObject.getString("attentionOpenId");
        String fansOpenId = jsonObject.getString("fansOpenId");

        if(StringUtils.isEmpty(attentionOpenId) && StringUtils.isEmpty(fansOpenId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        Integer addCount = attentionService.addAttention(attentionOpenId, fansOpenId);
        if(!globalResult.checkOutAdd(globalResult,addCount)){
            logger.error("关注用户接口入库失败");
            return globalResult;
        }
        HashMap<String, String> dateMap = new HashMap<>();
        dateMap.put("isSuccess", "yes");
        globalResult.setData(dateMap);
        return globalResult;
    }


    /**
     * 我的关注列表接口
     */
    @RequestMapping(value = "/getAttentions", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getAttentions(@RequestBody String jsonData){
        logger.info("我的关注列表接口入参为：==>" + jsonData);
        GlobalResult globalResult = GlobalResult.success();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String openId = jsonObject.getString("openId");

        if(StringUtils.isEmpty(openId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        List<Map<String, String>> attentionList = attentionService.getAttentions(openId);
        globalResult.setData(attentionList);
        return globalResult;
    }


    /**
     * 我的粉丝列表接口
     */
    @RequestMapping(value = "/getFans", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getFans(@RequestBody String jsonData){
        logger.info("我的粉丝列表接口入参为：==>" + jsonData);
        GlobalResult globalResult = GlobalResult.success();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String openId = jsonObject.getString("openId");

        if(StringUtils.isEmpty(openId)){
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        List<Map<String, String>> fansList = attentionService.getFans(openId);
        globalResult.setData(fansList);
        return globalResult;
    }
}
