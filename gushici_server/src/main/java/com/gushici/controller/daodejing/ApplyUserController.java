package com.gushici.controller.daodejing;


import com.alibaba.fastjson.JSONObject;
import com.gushici.bean.daodejing.ApplyUser;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.controller.product.WordController;
import com.gushici.service.daodejing.ApplyUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@CrossOrigin
@RequestMapping("/daodejing")
public class ApplyUserController {

    private static Logger logger = Logger.getLogger(WordController.class);

    @Autowired
    private ApplyUserService applyUserService;


    @RequestMapping(value = "/xufengqingyu/daodejing")
    public String ddjBaoMing(){

        return "ddjBaoMing";
    }


    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    public GlobalResult submit(@RequestBody String information){

        GlobalResult globalResult = new GlobalResult();

        logger.info("报名人员入参为：" + information);

        ApplyUser applyUser = JSONObject.parseObject(information, ApplyUser.class);

        if(StringUtils.isBlank(applyUser.getName())){
            logger.error("名字不能为空");
            globalResult.setCode(ResultCode.名字不能为空.getCode());
            globalResult.setMessage(ResultCode.名字不能为空.getMsg());
        }

        applyUser.setInsertTime(new Date());
        globalResult = applyUserService.insertApplyUser(applyUser);
        if(ResultCode.处理成功.getCode().equals(globalResult.getCode())){
            logger.info("人员:" +information + "报名成功");
            return globalResult;
        }else {
            logger.error("报名人员入库失败");
            return GlobalResult.error();
        }
    }
}
