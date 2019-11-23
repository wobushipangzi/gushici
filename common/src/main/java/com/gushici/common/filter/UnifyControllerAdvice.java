package com.gushici.common.filter;


import com.gushici.common.result.GlobalException;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;

@ControllerAdvice
public class UnifyControllerAdvice {

    private static Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public GlobalResult handlerException(GlobalException e){
        logger.error("发生未识别异常", e);
        return GlobalResult.error();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GlobalResult handlerException(MethodArgumentNotValidException me){
        GlobalResult globalResult = new GlobalResult();
        BindingResult bindingResult = me.getBindingResult();
        List<ObjectError> allError = bindingResult.getAllErrors();
        globalResult.setCode(ResultCode.系统错误.getCode());
        globalResult.setMessage(allError.get(0).getDefaultMessage());
        return globalResult;
    }

    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public GlobalResult handlerException(Exception e){
        logger.error("未知异常！原因是:", e);
        return GlobalResult.error();
    }

    @ExceptionHandler(value =Throwable.class)
    @ResponseBody
    public GlobalResult handlerException(Throwable e){
        logger.error("发生未知异常:", e);
        return GlobalResult.error();
    }

}
