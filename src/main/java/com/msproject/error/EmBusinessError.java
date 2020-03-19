package com.msproject.error;

public enum EmBusinessError implements CommonError{
    //10001通用错误类型
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知参数"),

    //20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_ERROR(20002,"用户名或密码错误"),
    USER_NOT_LOGIN(20003,"用户未登录"),

    //30000开头为订单信息相关错误定义
    NOT_ENOUGH_AMOUNT(30001,"商品库存不足")
    ;

    private EmBusinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    private int errCode;
    private String errMsg;
    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
