package com.mynet.gameserver.model;

import com.mynet.shared.response.ErrorResponse;

public class HandOverResultModel {
    private int code;
    private String errorMessage;
    private String logText;
    private String deckErrorText;
    private int longestTile;
    private ErrorResponse error;

    public HandOverResultModel() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public boolean isOk(){
        return code == 0;
    }

    public String getDeckErrorText() {
        return deckErrorText;
    }

    public void setDeckErrorText(String deckErrorText) {
        this.deckErrorText = deckErrorText;
    }

    public int getLongestTile() {
        return longestTile;
    }

    public void setLongestTile(int longestTile) {
        this.longestTile = longestTile;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "HandOverResultModel{" +
                "code=" + code +
                ", errorMessage='" + errorMessage + '\'' +
                ", logText='" + logText + '\'' +
                ", deckErrorText='" + deckErrorText + '\'' +
                ", longestTile=" + longestTile +
                ", error=" + error +
                '}';
    }
}
