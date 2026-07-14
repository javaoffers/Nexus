package com.javaoffers.nexus.pkg.exception;

/**
 * 业务异常：service 抛出，由 handler 捕获后映射为对应错误码的 R.error。
 */
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
