package com.mynet.shared.response;

import com.mynet.gameserver.enums.ErrorCode;

public class ErrorResponse {
    ErrorCode code;
    String data;
    String message;

    public ErrorResponse(ErrorCode code, String data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ErrorResponse(ErrorCode code, String data) {
        this.code = code;
        this.data = data;
    }

    public ErrorResponse(ErrorCode code) {
        this.code = code;
    }

    public ErrorResponse( String message,ErrorCode code) {
        this.code = code;
        this.message = message;
    }

}
