package com.gushici.common.result;

import java.io.Serializable;

public class GlobalException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -1357536603010329504L;

    private String code;

    private String msg;

    public GlobalException(String code, String msg) {
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
