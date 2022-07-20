package com.mynet.shared.response;

import com.mynet.shared.types.GamePlayStatusType;

public class BasicErrorResponse {
    private int code;
    private String msg;
    // optional second message
    private String m;

    public BasicErrorResponse(GamePlayStatusType error){
        this.code = error.getValue();
        this.msg = error.getTitle();
        this.m = error.getMsg();
    }
    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
