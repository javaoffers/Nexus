package com.javaoffers.nexus.core.controller;

import lombok.Data;

import java.util.Map;

/** 触发流程的入参（flowData），供多个 controller 复用。 */
@Data
public class TriggerDataParam {
    private Map<String, Object> flowData;
}
