package com.gushici.controller.product;

import com.alibaba.fastjson.JSONObject;
import com.gushici.bean.user.Collect;
import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.utils.BaiDuSample;
import com.gushici.common.utils.ParseWordUtils;
import com.gushici.service.product.AuthorService;
import com.gushici.service.product.ClassTwoService;
import com.gushici.service.product.ProductService;
import com.gushici.service.product.CollectService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {

    private static Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    private final CollectService collectService;

    private final ClassTwoService classTwoService;

    private final AuthorService authorService;

    @Autowired
    public ProductController(ProductService productService, CollectService collectService, ClassTwoService classTwoService, AuthorService authorService) {
        this.productService = productService;
        this.collectService = collectService;
        this.classTwoService = classTwoService;
        this.authorService = authorService;
    }


    /**
     * 查询作品分类
     */
    @RequestMapping(value = "/getClassify", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getClassify() {
        GlobalResult globalResult = productService.getClassify();
        //logger.info("查询作品分类的出参为==>" + classify.toString());
        if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            logger.error("调用查询作品分类接口失败{}", JSONObject.toJSONString(globalResult));
            globalResult = GlobalResult.error();
        }
        return globalResult;

    }


    /**
     * 点击二级分类获取对应作品
     */
    @RequestMapping(value = "/getproduct", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getproduct(@RequestBody String json) {
        GlobalResult globalResult;
        try {
            logger.info("点击二级分类获取对应作品入参为：==>{}", json);

            JSONObject jsonObject = JSONObject.parseObject(json);
            String catlogTwoId = jsonObject.getString("catlogTwoId");
            String page = jsonObject.getString("page");   //第几页，默认第一调用为0
            globalResult = productService.getProductByName(catlogTwoId, page);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                globalResult = GlobalResult.error();
            }
        }catch (Exception e){
            logger.error("点击二级分类获取对应作品接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 点击更多获取更多二级分类接口
     */
    @RequestMapping(value = "/getMore", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getMore(@RequestBody String json) {
        GlobalResult globalResult = GlobalResult.success();
        logger.info("点击更多获取更多二级分类接口入参为：==>{}", json);

        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            String catlogOneId = jsonObject.getString("catlogOneId");
            String reignId = jsonObject.getString("reignId");

            if (StringUtils.isEmpty(catlogOneId) && StringUtils.isEmpty(reignId)) {
                globalResult.setData(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }

            if (StringUtils.isNotEmpty(catlogOneId) && StringUtils.isEmpty(reignId)) {
                globalResult = classTwoService.getClassTwoList(catlogOneId);
                if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                    globalResult = GlobalResult.error();
                }
            }

            if (StringUtils.isEmpty(catlogOneId) && StringUtils.isNotEmpty(reignId)) {
                globalResult = authorService.getAuthorList(reignId);
                if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                    globalResult = GlobalResult.error();
                }
            }
            logger.info("点击更多获取更多二级分类接口出参为：==>{}", JSONObject.toJSONString(globalResult));
        }catch (GlobalException e){
            logger.error("点击更多获取更多二级分类接口异常{}",e);
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("系统错误异常{}",e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 点击作品标题获取作品详情
     */
    @RequestMapping(value = "/getproduct/details", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult getProductDetails(@RequestBody String json) {
        logger.info("点击作品标题获取作品详情入参为：==>{}", json);
        GlobalResult globalResult = new GlobalResult();

        JSONObject jsonObject = JSONObject.parseObject(json);
        String productId = jsonObject.getString("productId");
        String openId = jsonObject.getString("openId");
        Map<String, Object> productDetails = productService.getProductDetailsByProId(productId);
        if (null == productDetails) {
            logger.error("该诗词不存在");
            globalResult.setCode(ResultCode.该内容不存在.getCode());
            globalResult.setMessage(ResultCode.该内容不存在.getMsg());
            return globalResult;
        }

        //判断该作品是否被该用户收藏
        boolean isCollect = collectService.checkCollectProduct(productId, openId);
        if (isCollect) {
            productDetails.put("isCollect", true);
        } else {
            productDetails.put("isCollect", false);
        }
        logger.info("点击作品标题获取作品详情出参为：==>{}", productDetails.toString());

        globalResult = GlobalResult.packageResult(productDetails);

        return globalResult;
    }


    /**
     * 作品、作者添加收藏接口
     */
    @RequestMapping(value = "/addCollect", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult addCollect(@RequestBody String json) {
        GlobalResult globalResult = GlobalResult.success();
        logger.info("作品、作者添加收藏接口入参为：==>{}", json);
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            String productId = jsonObject.getString("productId");
            String authorId = jsonObject.getString("authorId");
            String openId = jsonObject.getString("openId");

            //如果全部为空，传入参数不合法
            if (StringUtils.isEmpty(productId) && StringUtils.isEmpty(authorId) || StringUtils.isEmpty(openId)) {
                globalResult.setData(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }

            Collect collect = new Collect();
            //如果传入的作品id，保存作品、用户、收藏时间
            if (StringUtils.isNotEmpty(productId) && StringUtils.isEmpty(authorId)) {
                collect.setProductId(productId);
                collect.setOpen_id(openId);
                collect.setCollectType(1);
                collect.setCollectTime(new Date());
            }else if (StringUtils.isEmpty(productId) && StringUtils.isNotEmpty(authorId)) {
                //如果传入的作者id，保存作品、用户、收藏时间
                collect.setAuthorId(authorId);
                collect.setOpen_id(openId);
                collect.setCollectType(2);
                collect.setCollectTime(new Date());
            }else {
                globalResult.setData(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }
            globalResult = collectService.saveCollect(collect);
            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                globalResult = GlobalResult.error();
            }
        }catch (Exception e){
            logger.error("作品、作者添加收藏接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 作品、作者取消收藏接口
     */
    @RequestMapping(value = "/delCollect", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public GlobalResult delCollect(@RequestBody String json) {
        GlobalResult globalResult = GlobalResult.success();
        logger.info("作品、作者取消收藏接口入参为：==>{}", json);

        JSONObject jsonObject = JSONObject.parseObject(json);
        String productId = jsonObject.getString("productId");
        String authorId = jsonObject.getString("authorId");
        String openId = jsonObject.getString("openId");

        //如果全部为空，传入参数不合法
        if (StringUtils.isEmpty(productId) && StringUtils.isEmpty(authorId) || StringUtils.isEmpty(openId)) {
            globalResult.setData(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }

        Collect collect = new Collect();
        //如果传入的作品id，通过作品、用户删除记录
        if (StringUtils.isNotEmpty(productId) && StringUtils.isEmpty(authorId)) {
            collect.setProductId(productId);
            collect.setOpen_id(openId);
        }

        //如果传入的作者id，通过作者、用户删除记录
        if (StringUtils.isEmpty(productId) && StringUtils.isNotEmpty(authorId)) {
            collect.setAuthorId(authorId);
            collect.setOpen_id(openId);
        }

        globalResult = collectService.delCollect(collect);
        if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            logger.error("作品、作者取消收藏接口删除数据失败");
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 搜索作品功能接口
     */
    @RequestMapping(value = "/searchProduct", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult searchProduct(@RequestBody String jsonData) {
        logger.info("搜索作品功能接口入参为：==>{}", jsonData);
        GlobalResult globalResult = GlobalResult.success();
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String productName = jsonObject.getString("productName");
            String page = jsonObject.getString("page");

            if (StringUtils.isEmpty(productName)) {
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
                return globalResult;
            }

            //解析文字
            productName = ParseWordUtils.parseWord(productName);
            if(StringUtils.isNotBlank(productName)){
                globalResult = productService.searchProduct(productName, page);
                logger.info("搜索作品功能接口出参为：==>{}", JSONObject.toJSONString(globalResult));
            }

            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("搜索作品功能接口异常{}", JSONObject.toJSONString(globalResult));
                globalResult = GlobalResult.error();
            }
        }catch (Exception e){
            logger.error("搜索作品功能接口异常{}", e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }


    /**
     * 拍照识诗词接口
     */
    @RequestMapping(value = "/discernShici", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult discernShici(@RequestParam MultipartFile file, @RequestParam String page) {

        GlobalResult globalResult = GlobalResult.success();

        try {
            if(null == file){
                logger.error("拍照识诗词接口传入图片不能为空");
                globalResult.setCode(ResultCode.传入参数不合法.getCode());
                globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            }

            logger.info("开始识别图片中的文字......");
            String text = BaiDuSample.imgToText(file);
            logger.info("识别到图片中的文字为：==>{}", text);

            if(StringUtils.isEmpty(text)){
                logger.error("没有识别到图片中的文字");
                globalResult.setCode(ResultCode.识别图片文字失败.getCode());
                globalResult.setMessage(ResultCode.识别图片文字失败.getMsg());
            }

            globalResult = productService.getProductsByPhoto(text, page);
            logger.info("拍照识诗词接口出参为：==>{}", JSONObject.toJSONString(globalResult));

            if(!ResultCode.处理成功.getCode().equals(globalResult.getCode())){
                logger.error("调用通过图片解析到的文字查询相关作品失败{}", JSONObject.toJSONString(globalResult));
                globalResult = GlobalResult.error();
            }
        } catch (IOException e) {
            logger.error("调用识别图片文字接口异常==>{}", e);
            globalResult = GlobalResult.error();
        }catch (Exception e){
            logger.error("拍照识诗词接口异常{}",e);
            globalResult = GlobalResult.error();
        }
        return globalResult;
    }




    /**
     * 识别文字接口
     */
    @RequestMapping(value = "/shibiewenzi", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult shibiewenzi(@RequestParam MultipartFile file ) throws IOException {
        logger.info("开始调用文字识别接口......");

        GlobalResult globalResult = GlobalResult.success();

        if(null == file){
            logger.error("识别文字接口传入图片不能为空");
            globalResult.setCode(ResultCode.传入参数不合法.getCode());
            globalResult.setMessage(ResultCode.传入参数不合法.getMsg());
            return globalResult;
        }
        System.out.println("Name" + file.getName());
        System.out.println("InputStream" + file.getInputStream());
        System.out.println("contentType" + file.getContentType());

        try {
            logger.info("开始识别图片中的文字......");
            String text = BaiDuSample.imgToText(file);
            logger.info("识别到图片中的文字为：==>{}", text);

            Map map = JSONObject.parseObject(text, Map.class);
            List words_result = (List) map.get("words_result");
            String words = "";
            ArrayList<Integer> countList = new ArrayList<>();
            HashMap<String, String> dataMap = new HashMap<>();
            for (Object o : words_result) {
                Map parse = JSONObject.parseObject(JSONObject.toJSONString(o), Map.class);
                int count = String.valueOf(parse.get("words")).toCharArray().length;
                countList.add(count);
                words = words + "<br/>" + parse.get("words").toString();
            }
            countList.sort((o1, o2) -> o1 > o2 ? -1 : 1);
            dataMap.put("words",words);
            dataMap.put("max",String.valueOf(countList.get(0) * 16));
            globalResult.setData(dataMap);
            System.out.println(JSONObject.toJSONString(globalResult));
            if (StringUtils.isEmpty(text)) {
                logger.error("没有识别到文字");
                globalResult.setCode(ResultCode.识别图片文字失败.getCode());
                globalResult.setMessage(ResultCode.识别图片文字失败.getMsg());
                return globalResult;
            }
        }catch (IOException e){
            logger.error("调用识别图片文字接口失败==>{}", e);
            globalResult.setCode(ResultCode.识别图片文字失败.getCode());
            globalResult.setMessage(ResultCode.识别图片文字失败.getMsg());
            return globalResult;
        }
        return globalResult;
    }


}