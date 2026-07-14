package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.service.FlowInfoService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 对应 Go core/handler/flow_handler.go（FlowInfo 的删除/分页）。 */
@RestController
@RequestMapping("/api/v1/flow")
public class FlowController {

    private final FlowInfoService flowInfoService;

    public FlowController(FlowInfoService flowInfoService) {
        this.flowInfoService = flowInfoService;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FlowInfoPageParam extends PageParam {
        private String flowName;
        private String flowType;
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteFlow(@PathVariable Long id) {
        try {
            flowInfoService.deleteFlowInfo(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(6001, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageFlows(@RequestBody FlowInfoPageParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<FlowInfoService.FlowInfoDTO> p =
                    flowInfoService.pageFlowInfos(param, param.getFlowName(), param.getFlowType());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(6002, e.getMessage());
        }
    }
}
