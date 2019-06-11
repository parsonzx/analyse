package com.example.wordanalyze.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable {

    public  final static Integer SUCCESS=0;
    public final static Integer FAIL=1;
    private String descriptions;
    private Integer ret=SUCCESS;

    private String msg;

    public static Integer getSUCCESS() {
        return SUCCESS;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public Integer getRet() {
        return ret;
    }

    public void setRet(Integer ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
