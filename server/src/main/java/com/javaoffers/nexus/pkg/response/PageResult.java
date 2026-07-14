package com.javaoffers.nexus.pkg.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 对应 Go 的 PaginationResult：success / errorCode / errorMsg / total / result。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> {
    private boolean success;
    private int errorCode;
    private String errorMsg;
    private long total;
    private T result;

    public static <T> PageResult<T> ok(long total, T data) {
        PageResult<T> r = new PageResult<>();
        r.success = true;
        r.total = total;
        r.result = data;
        return r;
    }
}
