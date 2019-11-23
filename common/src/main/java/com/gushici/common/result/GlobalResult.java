package com.gushici.common.result;

import org.springframework.stereotype.Component;

public class GlobalResult {

    private String code;   //返回码

    private String message;  //返回消息

    private Object data;  //返回数据

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static GlobalResult success() {
        GlobalResult globalResult = new GlobalResult();
        globalResult.setCode(ResultCode.处理成功.getCode());
        globalResult.setMessage(ResultCode.处理成功.getMsg());
        return globalResult;
    }

    public static GlobalResult error() {
        GlobalResult globalResult = new GlobalResult();
        globalResult.setCode(ResultCode.系统错误.getCode());
        globalResult.setMessage(ResultCode.系统错误.getMsg());
        return globalResult;
    }

    public static GlobalResult packageResult(Object obj) {
        GlobalResult globalResult;
        if (obj != null ) {
            globalResult = GlobalResult.success();
            globalResult.setData(obj);
            return globalResult;
        } else {
            globalResult = GlobalResult.error();
            return globalResult;
        }
    }

    /**
     * 检查是否保存成功
     * @param globalResult
     * @param addCount
     * @return
     */
    public Boolean checkOutAdd(GlobalResult globalResult, Integer addCount) {
        if(1 != addCount){
            globalResult.setCode(ResultCode.数据库新增数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库新增数据失败.getMsg());
            return false;
        }
        return true;
    }

    /**
     * 检查是否新增成功
     * @param globalResult
     * @return
     */
    public Boolean checkOutUpdate(GlobalResult globalResult, Integer updateCount) {
        if(1 != updateCount){
            globalResult.setCode(ResultCode.更新数据库失败.getCode());
            globalResult.setMessage(ResultCode.更新数据库失败.getMsg());
            return false;
        }
        return true;
    }


    /**
     * 检查是否删除成功
     * @param globalResult
     * @param delCount
     * @return
     */
    public Boolean checkDelCount(GlobalResult globalResult, Integer delCount) {
        if(1 > delCount){
            globalResult.setCode(ResultCode.数据库删除数据失败.getCode());
            globalResult.setMessage(ResultCode.数据库删除数据失败.getMsg());
            return false;
        }
        return true;
    }
}
