package com.xuecheng.base.exception;

import org.yaml.snakeyaml.DumperOptions;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/26 13:14
 */
public class XueChengException extends RuntimeException {
    private String errMsg;


    public XueChengException() {
        super();
    }
    public XueChengException(String errMsg) {
        super(errMsg);
        this.errMsg = errMsg;
    }
    public static void cast(CommonError commonError){
        throw new XueChengException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new XueChengException(errMessage);
    }

    public String getErrMsg() {
        return errMsg;
    }

}
