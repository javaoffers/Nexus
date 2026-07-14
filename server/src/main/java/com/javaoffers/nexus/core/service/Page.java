package com.javaoffers.nexus.core.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** 分页结果持有者（total + 记录），由 handler 包装为 PageResult 响应。 */
@Data
@AllArgsConstructor
public class Page<T> {
    private long total;
    private List<T> records;
}
