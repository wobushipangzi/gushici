package com.gushici.common.result;


public enum ResultCode {


    处理成功("000","处理成功"),
    系统错误("999","系统错误"),

    服务端请求微信服务器失败("A100","服务端请求微信服务器失败"),

    数据库新增数据失败("B100","数据库新增数据失败"),
    更新数据库失败("B101","更新数据库失败"),
    数据库删除数据失败("B102","数据库删除数据失败"),
    查询数据库失败("B103","查询数据库失败"),
    OOS删除数据失败("B104","OOS删除数据失败"),

    传入登录态不能为空("B100","传入登录态不能为空"),
    解密用户信息失败("B101","解密用户信息失败"),
    传入参数不合法("B102","传入参数不合法"),

    获取用户信息失败("C101","获取用户信息失败"),
    该内容不存在("C100","该内容不存在"),

    上传文件到OOS失败("D100","上传文件到OOS失败"),
    识别图片文字失败("D101","识别图片文字失败"),

    没有更多信息("E100","没有更多信息"),
    名字不能为空("E101","名字不能为空");


    private String code;

    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
