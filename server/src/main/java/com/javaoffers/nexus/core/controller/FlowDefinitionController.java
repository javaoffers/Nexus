package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Parameter;
import com.javaoffers.nexus.core.model.VariableInfo;
import com.javaoffers.nexus.core.service.FlowDefinitionService;
import com.javaoffers.nexus.core.service.FlowRuntimeService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 对应 Go core/handler/flow_definition_handler.go。 */
@RestController
@RequestMapping("/api/v1/flow/definition")
public class FlowDefinitionController {

    private final FlowDefinitionService flowDefinitionService;
    private final FlowRuntimeService flowRuntimeService;

    public FlowDefinitionController(FlowDefinitionService flowDefinitionService, FlowRuntimeService flowRuntimeService) {
        this.flowDefinitionService = flowDefinitionService;
        this.flowRuntimeService = flowRuntimeService;
    }

    /**
     * 流程定义. 每个流程就是一个服务.
     * @param param
     * @return
     */
    @PostMapping("/add")
    public Result<?> addFlowDefinition(@RequestBody FlowDefAddParam param) {
        if (param.getFlowName() == null || param.getFlowType() == null) {
            return R.error(400, "参数错误");
        }
        try {
            flowDefinitionService.createFlowDefinition(param.getFlowName(), param.getFlowType(), param.getRemark(),
                    param.getFlowInputParams(), param.getFlowOutputParams());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(5001, e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateFlowDefinition(@RequestBody FlowDefUpdateParam param) {
        if (param.getId() == null) {
            return R.error(400, "参数错误");
        }
        try {
            flowDefinitionService.updateFlowDefinition(param.getId(), param.getFlowName(), param.getFlowType(), param.getRemark(),
                    param.getFlowInputParams(), param.getFlowOutputParams());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(5002, e.getMessage());
        }
    }

    @PutMapping("/save")
    public Result<?> saveFlowDefinitionContent(@RequestBody FlowDefContentParam param) {
        if (param.getId() == null) {
            return R.error(400, "参数错误");
        }
        try {
            flowDefinitionService.saveFlowDefinitionContent(param.getId(), param.getFlowContent(), param.getFlowVariables());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(5003, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteFlowDefinition(@PathVariable Long id) {
        try {
            flowDefinitionService.deleteFlowDefinition(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(5004, e.getMessage());
        }
    }

    @GetMapping("/info/{id}")
    public Result<?> getFlowDefinitionInfo(@PathVariable Long id) {
        try {
            return R.ok(flowDefinitionService.getFlowDefinitionInfo(id));
        } catch (BizException e) {
            return R.error(5005, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageFlowDefinitions(@RequestBody FlowDefPageParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<FlowDefinitionService.FlowDefinitionInfoDTO> p =
                    flowDefinitionService.pageFlowDefinitions(param, param.getFlowName(), param.getFlowType());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(5006, e.getMessage());
        }
    }

    @PostMapping("/deploy")
    public Result<?> deployFlowDefinition(@RequestBody FlowDefDeployParam param) {
        if (param.getFlowDefinitionId() == null || param.getFlowDeployVersion() == null) {
            return R.error(400, "参数错误");
        }
        try {
            flowDefinitionService.deployFlowDefinition(param.getFlowDefinitionId(), param.getFlowDeployVersion(), param.getFlowVersionRemark());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(5007, e.getMessage());
        }
    }

    @PostMapping("/debug/{flowKey}")
    public Result<?> debugFlowDefinition(@PathVariable String flowKey, @RequestBody TriggerDataParam param) {
        try {
            return R.ok(flowRuntimeService.triggerFlowByDefinition(flowKey, param.getFlowData()));
        } catch (BizException e) {
            return R.error(5008, e.getMessage());
        }
    }
}
