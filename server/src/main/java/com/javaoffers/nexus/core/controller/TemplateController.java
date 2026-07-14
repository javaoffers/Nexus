package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.service.FlowDefinitionService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.javaoffers.nexus.core.model.PageParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 对应 Go core/handler/flow_definition_handler.go 的 TemplateMarketPage（简化为流程定义分页）。 */
@RestController
@RequestMapping("/api/v1/template")
public class TemplateController {

    private final FlowDefinitionService flowDefinitionService;

    public TemplateController(FlowDefinitionService flowDefinitionService) {
        this.flowDefinitionService = flowDefinitionService;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TemplatePageParam extends PageParam {
        private String flowName;
        private String flowType;
    }

    @PostMapping("/market")
    public Object templateMarketPage(@RequestBody TemplatePageParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<FlowDefinitionService.FlowDefinitionInfoDTO> p =
                    flowDefinitionService.pageFlowDefinitions(param, param.getFlowName(), param.getFlowType());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(5009, e.getMessage());
        }
    }

    @PostMapping("/market/classify")
    public Object templateMarketClassify(@RequestBody TemplatePageParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<FlowDefinitionService.FlowDefinitionInfoDTO> p =
                    flowDefinitionService.pageFlowDefinitions(param, param.getFlowName(), param.getFlowType());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(5009, e.getMessage());
        }
    }
}
