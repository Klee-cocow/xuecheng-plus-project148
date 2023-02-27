package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * @author 咏鹅
 * @version 1.0
 * @description 异常响应
 * @date 2023/2/26 13:31
 */
public class RestErrorResponse implements Serializable {
    private String errMsg;


    public RestErrorResponse(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
