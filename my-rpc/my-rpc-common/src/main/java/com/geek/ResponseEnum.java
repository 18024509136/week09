package com.geek;

/**
 * 响应枚举
 */
public enum ResponseEnum {

    /**
     * 成功
     */
    SUCCESS("0000", "成功"),

    /**
     * 异常
     */
    ERROR("0001", "异常");

    ResponseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;

    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
