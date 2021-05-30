package com.geek;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Rpc响应实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    private String code;

    private String message;

    private Object result;

    public static RpcResponse success(Object result) {
        return new RpcResponse(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), result);
    }

    public static RpcResponse error() {
        return new RpcResponse(ResponseEnum.ERROR.getCode(), ResponseEnum.ERROR.getMessage(), null);
    }

    public static RpcResponse error(String errorMsg) {
        return new RpcResponse(ResponseEnum.ERROR.getCode(), errorMsg, null);
    }
}
