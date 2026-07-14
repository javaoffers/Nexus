package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.service.FlowRuntimeService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/** 对应 Go core/handler/flow_runtime_handler.go（Open API，Token 鉴权）。 */
@RestController
@RequestMapping("/open/v1")
public class OpenApiController {

    private final FlowRuntimeService flowRuntimeService;

    public OpenApiController(FlowRuntimeService flowRuntimeService) {
        this.flowRuntimeService = flowRuntimeService;
    }

    @GetMapping("/flow/trigger/{flowVersion}/{flowKey}")
    public Result<?> openTriggerFlowGet(@PathVariable String flowVersion, @PathVariable String flowKey,
                                        HttpServletRequest request) {
        Map<String, Object> flowData = new HashMap<>();
        Map<String, String[]> q = request.getParameterMap();
        for (Map.Entry<String, String[]> e : q.entrySet()) {
            String[] v = e.getValue();
            if (v != null && v.length == 1) {
                flowData.put(e.getKey(), v[0]);
            } else if (v != null) {
                flowData.put(e.getKey(), v);
            }
        }
        return trigger(flowKey, flowVersion, flowData, 8001);
    }

    @PostMapping("/flow/trigger/{flowVersion}/{flowKey}")
    public Result<?> openTriggerFlowPost(@PathVariable String flowVersion, @PathVariable String flowKey,
                                         @RequestBody TriggerDataParam param) {
        Map<String, Object> flowData = (param != null) ? param.getFlowData() : new HashMap<>();
        return trigger(flowKey, flowVersion, flowData, 8001);
    }

    private Result<?> trigger(String flowKey, String flowVersion, Map<String, Object> flowData, int errCode) {
        try {
            return R.ok(flowRuntimeService.triggerFlow(flowKey, flowVersion, flowData));
        } catch (BizException e) {
            return R.error(errCode, e.getMessage());
        }
    }

    @GetMapping("/flow/getAsyncFlowResult/{flowInstanceId}")
    public Result<?> openGetAsyncFlowResult(@PathVariable String flowInstanceId) {
        try {
            return R.ok(flowRuntimeService.getAsyncFlowResult(flowInstanceId));
        } catch (BizException e) {
            return R.error(8002, e.getMessage());
        }
    }
}
