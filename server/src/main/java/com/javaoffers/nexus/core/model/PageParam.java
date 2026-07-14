package com.javaoffers.nexus.core.model;

import lombok.Data;

/**
 * 分页请求基类，对应 Go 的 model.PageParam。页码/页大小在 service 层做兜底。
 */
@Data
public class PageParam {
    private int pageNum;
    private int pageSize;
}
