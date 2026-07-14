package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.service.FlowRuntimeService;
import com.javaoffers.nexus.core.service.FlowVersionService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 对应 Go core/handler/flow_version_handler.go。 */
@RestController
@RequestMapping("/api/v1/flow/version")
public class FlowVersionController {

    private final FlowVersionService flowVersionService;
    private final FlowRuntimeService flowRuntimeService;

    public FlowVersionController(FlowVersionService flowVersionService, FlowRuntimeService flowRuntimeService) {
        this.flowVersionService = flowVersionService;
        this.flowRuntimeService = flowRuntimeService;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FlowVersionPageParam extends PageParam {
        private Long flowId;
        private Integer flowVersionStatus;
    }

    @Data
    public static class FlowVersionStatusParam {
        private Long flowVersionId;
        private Integer flowVersionStatus;
    }

    @PutMapping("/status")
    public Result<?> updateFlowVersionStatus(@RequestBody FlowVersionStatusParam param) {
        if (param.getFlowVersionId() == null) {
            return R.error(400, "参数错误");
        }
        try {
            flowVersionService.updateFlowVersionStatus(param.getFlowVersionId(), param.getFlowVersionStatus());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(7001, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteFlowVersion(@PathVariable Long id) {
        try {
            flowVersionService.deleteFlowVersion(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(7002, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageFlowVersions(@RequestBody FlowVersionPageParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<FlowVersionService.FlowVersionDTO> p =
                    flowVersionService.pageFlowVersions(param, param.getFlowId(), param.getFlowVersionStatus());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(7003, e.getMessage());
        }
    }

    @GetMapping("/latest/{flowKey}")
    public Result<?> getLatestVersion(@PathVariable String flowKey) {
        return R.ok(flowVersionService.getLatestVersion(flowKey));
    }

    @PostMapping("/trigger/{flowVersion}/{flowKey}")
    public Result<?> triggerFlowVersion(@PathVariable String flowVersion, @PathVariable String flowKey,
                                        @RequestBody TriggerDataParam param) {
        try {
            return R.ok(flowRuntimeService.triggerFlow(flowKey, flowVersion, param.getFlowData()));
        } catch (BizException e) {
            return R.error(7004, e.getMessage());
        }
    }

    @GetMapping("/getAsyncFlowResult/{flowInstanceId}")
    public Result<?> getAsyncFlowResult(@PathVariable String flowInstanceId) {
        try {
            return R.ok(flowRuntimeService.getAsyncFlowResult(flowInstanceId));
        } catch (BizException e) {
            return R.error(7005, e.getMessage());
        }
    }
}
