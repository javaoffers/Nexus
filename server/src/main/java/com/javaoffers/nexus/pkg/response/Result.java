package com.javaoffers.nexus.pkg.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 对应 Go pkg/response/response.go 的 Result。
 * JSON 字段名保持一致：success / errorCode / errorMsg / result。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private boolean success;
    private int errorCode;
    private String errorMsg;
    private T result;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.success = true;
        r.result = data;
        return r;
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.success = false;
        r.errorCode = code;
        r.errorMsg = msg;
        return r;
    }
}
