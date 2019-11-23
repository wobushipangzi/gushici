package com.gushici.common.smallprogram;


public enum CommentType {
    古诗词类型("001","gushici"),
    随笔类型("003","suibi");








    private String code;

    private String msg;


    CommentType(String code, String msg) {
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
