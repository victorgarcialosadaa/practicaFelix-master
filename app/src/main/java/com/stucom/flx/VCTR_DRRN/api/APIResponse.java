package com.stucom.flx.VCTR_DRRN.api;

public class APIResponse<T> {
    private T data;
    private int errorCode;
    private String errorMsg;

    public APIResponse(T data, int errorCode, String errorMsg) {
        this.data = data;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}


