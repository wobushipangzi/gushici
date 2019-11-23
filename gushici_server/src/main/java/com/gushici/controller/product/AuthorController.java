package com.gushici.controller.product;

import com.alibaba.fastjson.JSONObject;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.service.product.AuthorService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController {

    private static Logger logger = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }


    /**
     * 点击朝代获取对应的作者
     */
    @RequestMapping(value = "/getClassify", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getReignAndAuthor(){
        GlobalResult globalResult = authorService.getReignAndAuthor();
        //logger.info("点击朝代获取对应的作者出参为：==>{}", JSONObject.toJSONString(reignAndAuthorMap));
        if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            logger.error("调用点击朝代获取对应作者接口失败{}", JSONObject.toJSONString(globalResult));
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }

    /**
     * 点击作者查询对应的所有作品以及作者简介
     */
    @RequestMapping(value = "/getAuthor/product", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getAuthorAndProduct(@RequestBody String jsonData){
        logger.info("点击作者查询对应的所有作品以及作者简介入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String authorId = jsonObject.getString("authorId");
            String page = jsonObject.getString("page");
            //String openId = jsonObject.getString("openId");
            HashMap<Object, Object> authAndProsMap = authorService.getAuthAndProsByAuthId(authorId, page);

            List productList = JSONObject.parseObject(JSONObject.toJSONString(authAndProsMap.get("productList")),List.class);
            if(productList == null || productList.size() == 0){
                globalResult.setCode(ResultCode.没有更多信息.getCode());
                globalResult.setMessage(ResultCode.没有更多信息.getMsg());
                return globalResult;
            }
            globalResult.setData(authAndProsMap);
        }catch (Exception e){
            logger.error("点击作者查询对应的所有作品以及作者简介接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 搜索作者功能接口
     */
    @RequestMapping(value = "/searchAuthor", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult searchAuthor(@RequestBody String jsonData){
        logger.info("搜索作者功能接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String authorName = jsonObject.getString("authorName");
            String page = jsonObject.getString("page");

            if(StringUtils.isEmpty(authorName)){
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }

            globalResult = authorService.searchAuthor(authorName, page);
            logger.info("搜索作者功能接口出参为：==>{}", JSONObject.toJSONString(globalResult));
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用搜索作者接口异常{}", JSONObject.toJSONString(globalResult));
                globalResult = GlobalResult.error();
            }
        }catch (Exception e){
            logger.error("搜索作者功能接口异常错误{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }
}

