package com.mynet.shared.builders;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.shared.response.ErrorResponse;

public class ErrorResponseBuilder {
    private ErrorCode code;
    private String data;
    private String message;

    public ErrorResponseBuilder setCode(ErrorCode code) {
        this.code = code;
        return this;
    }

    public ErrorResponseBuilder setData(String data) {
        this.data = data;
        return this;
    }

    public ErrorResponseBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorResponse createErrorResponse() {
        return new ErrorResponse(code, data, message);
    }
}