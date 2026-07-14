package com.javaoffers.nexus.core.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaoffers.nexus.core.model.DataSource;
import com.javaoffers.nexus.core.repository.DataSourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 对应 Go engine/engine.go + executor.go：流程解析与节点调度执行。
 */
@Slf4j
@Component
public class FlowEngine {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_ITERATIONS = 1000;

    private final ExpressionService expressionService;
    private final HttpExecutor httpExecutor;
    private final DataSourceMapper dataSourceMapper;

    public FlowEngine(ExpressionService expressionService,
                      HttpExecutor httpExecutor,
                      DataSourceMapper dataSourceMapper) {
        this.expressionService = expressionService;
        this.httpExecutor = httpExecutor;
        this.dataSourceMapper = dataSourceMapper;
    }

    public ExecutionResult execute(FlowDef flowDef, Map<String, Object> flowData) {
        if (flowData == null) {
            flowData = new HashMap<>();
        }

        // 1. 解析 flowContent
        List<FlowElement> elements;
        try {
            elements = MAPPER.readValue(flowDef.getFlowContent(), new TypeReference<List<FlowElement>>() {});
        } catch (Exception e) {
            log.warn("[Engine] Failed to parse flow content: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("error", "解析流程失败: " + e.getMessage());
            return new ExecutionResult("ABORT", err);
        }

        // 2. 构建节点 map，定位 START
        Map<String, FlowElement> elementMap = new HashMap<>();
        FlowElement startNode = null;
        for (FlowElement e : elements) {
            elementMap.put(e.getKey(), e);
            if ("START".equals(e.getElementType())) {
                startNode = e;
            }
        }
        if (startNode == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "流程中没有开始节点");
            return new ExecutionResult("ABORT", err);
        }

        // 3. 初始化变量
        VariableManager vm = new VariableManager();
        if (flowDef.getInputs() != null) {
            for (ParameterDef input : flowDef.getInputs()) {
                String key = "input_" + input.getKey();
                if (flowData.containsKey(input.getKey())) {
                    vm.set(key, flowData.get(input.getKey()));
                }
            }
        }
        if (flowDef.getOutputs() != null) {
            for (ParameterDef output : flowDef.getOutputs()) {
                vm.set("output_" + output.getKey(), null);
            }
        }
        if (flowDef.getVariables() != null) {
            for (VariableDef v : flowDef.getVariables()) {
                vm.set(v.getKey(), null);
            }
        }

        // 4. 执行上下文
        ExecutionContext ctx = new ExecutionContext();
        ctx.setInstanceId(flowDef.getInstanceId());
        ctx.setElementMap(elementMap);
        ctx.setVariables(vm);
        ctx.setStatus("RUNNING");

        // 5. 从 START 执行
        executeFromNode(ctx, startNode);

        // 6. 收集输出变量
        Map<String, Object> outputData = new HashMap<>();
        if (flowDef.getOutputs() != null) {
            for (ParameterDef output : flowDef.getOutputs()) {
                String key = "output_" + output.getKey();
                outputData.put(output.getKey(), vm.get(key));
            }
        }

        if ("ABORT".equals(ctx.getStatus())) {
            return new ExecutionResult("ABORT", ctx.getErrorData());
        }
        return new ExecutionResult("FINISH", outputData);
    }

    private void executeFromNode(ExecutionContext ctx, FlowElement node) {
        if (node == null || "ABORT".equals(ctx.getStatus())) {
            return;
        }
        FlowElement current = node;
        for (int i = 0; i < MAX_ITERATIONS && current != null && !"ABORT".equals(ctx.getStatus()); i++) {
            String nextKey = executeNode(ctx, current);
            if (nextKey == null || nextKey.isEmpty() || "END".equals(current.getElementType())) {
                break;
            }
            FlowElement next = ctx.getElementMap().get(nextKey);
            if (next == null) {
                ctx.setStatus("ABORT");
                Map<String, Object> err = new HashMap<>();
                err.put("error", "找不到节点: " + nextKey);
                ctx.setErrorData(err);
                break;
            }
            current = next;
        }
    }

    private String executeNode(ExecutionContext ctx, FlowElement node) {
        switch (node.getElementType() == null ? "" : node.getElementType()) {
            case "START":
                return executeStartNode(ctx, node);
            case "END":
                return executeEndNode(ctx, node);
            case "METHOD":
                return executeMethodNode(ctx, node);
            case "CONDITION":
                return executeConditionNode(ctx, node);
            case "ASSIGN":
                return executeAssignNode(ctx, node);
            case "CODE":
                return executeCodeNode(ctx, node);
            case "MYSQL":
                return executeMysqlNode(ctx, node);
            default:
                log.warn("[Engine] Unknown node type: {}", node.getElementType());
                return "";
        }
    }

    private String executeStartNode(ExecutionContext ctx, FlowElement node) {
        if (node.getOutgoings() != null && !node.getOutgoings().isEmpty()) {
            return node.getOutgoings().get(0);
        }
        return "";
    }

    private String executeEndNode(ExecutionContext ctx, FlowElement node) {
        ctx.setStatus("FINISH");
        return "";
    }

    private String executeMethodNode(ExecutionContext ctx, FlowElement node) {
        MethodDef method = node.getMethod();
        if (method == null) {
            return nextOutgoing(node);
        }
        VariableManager vm = ctx.getVariables();

        Map<String, Object> headers = new HashMap<>();
        if (method.getHeaderFillRules() != null) {
            for (FillRule rule : method.getHeaderFillRules()) {
                headers.put(rule.getTarget(), vm.resolveValue(rule.getSource(), rule.getSourceType()));
            }
        }
        Map<String, Object> params = new HashMap<>();
        if (method.getInputFillRules() != null) {
            for (FillRule rule : method.getInputFillRules()) {
                params.put(rule.getTarget(), vm.resolveValue(rule.getSource(), rule.getSourceType()));
            }
        }

        Map<String, Object> result = httpExecutor.sendRequest(method, headers, params);
        if (result == null) {
            ctx.setStatus("ABORT");
            Map<String, Object> err = new HashMap<>();
            err.put("error", "HTTP调用失败");
            ctx.setErrorData(err);
            return "";
        }

        if (method.getOutputFillRules() != null) {
            for (FillRule rule : method.getOutputFillRules()) {
                Object val;
                if ("OUTPUT_PARAM".equals(rule.getSourceType())) {
                    val = getNestedMap(result, rule.getSource());
                } else {
                    val = vm.resolveValue(rule.getSource(), rule.getSourceType());
                }
                vm.set(rule.getTarget(), val);
            }
        }
        return nextOutgoing(node);
    }

    private String executeConditionNode(ExecutionContext ctx, FlowElement node) {
        List<ConditionItem> conditions = node.getConditions();
        if (conditions == null) {
            return "";
        }
        String defaultOutgoing = null;
        VariableManager vm = ctx.getVariables();
        for (ConditionItem cond : conditions) {
            if ("DEFAULT".equals(cond.getConditionType())) {
                defaultOutgoing = cond.getOutgoing();
                continue;
            }
            String expression = cond.getExpression();
            if ((expression == null || expression.isEmpty()) && cond.getConditionExpressions() != null && !cond.getConditionExpressions().isEmpty()) {
                expression = expressionService.generateExpression(cond.getConditionExpressions());
            }
            if (expressionService.evalExpression(expression, vm)) {
                return cond.getOutgoing();
            }
        }
        if (defaultOutgoing != null) {
            return defaultOutgoing;
        }
        log.warn("[Engine] No condition matched and no default branch");
        return "";
    }

    private String executeAssignNode(ExecutionContext ctx, FlowElement node) {
        List<FillRule> rules = node.getAssignRules();
        if (rules == null) {
            ctx.setStatus("ABORT");
            return "";
        }
        VariableManager vm = ctx.getVariables();
        for (FillRule rule : rules) {
            vm.set(rule.getTarget(), vm.resolveValue(rule.getSource(), rule.getSourceType()));
        }
        return nextOutgoing(node);
    }

    private String executeCodeNode(ExecutionContext ctx, FlowElement node) {
        if (node.getContent() == null || node.getContent().isEmpty()) {
            return nextOutgoing(node);
        }
        Object result = expressionService.evalCode(node.getContent(), ctx.getVariables());
        if (result instanceof Map) {
            for (Map.Entry<String, Object> e : ((Map<String, Object>) result).entrySet()) {
                ctx.getVariables().set(e.getKey(), e.getValue());
            }
        }
        return nextOutgoing(node);
    }

    @SuppressWarnings("unchecked")
    private String executeMysqlNode(ExecutionContext ctx, FlowElement node) {
        if (node.getDataSourceId() == null || node.getSql() == null || node.getSql().isEmpty()) {
            log.warn("[Engine] MySQL node missing datasource or sql");
            return nextOutgoing(node);
        }
        DataSource ds = dataSourceMapper.getById(node.getDataSourceId());
        if (ds == null) {
            ctx.setStatus("ABORT");
            Map<String, Object> err = new HashMap<>();
            err.put("error", "数据源不存在");
            ctx.setErrorData(err);
            return "";
        }
        String dsn = "jdbc:mysql://" + ds.getAddress() + ":" + ds.getPort() + "/" + ds.getDatabaseName()
                + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";

        // 替换 #{var}
        String sql = node.getSql();
        for (Map.Entry<String, Object> e : ctx.getVariables().getAll().entrySet()) {
            String placeholder = "#{" + e.getKey() + "}";
            if (sql.contains(placeholder)) {
                sql = sql.replace(placeholder, String.valueOf(e.getValue()));
            }
        }

        String op = node.getOperationType() == null ? "" : node.getOperationType();
        try (Connection conn = DriverManager.getConnection(dsn, ds.getUserName(), ds.getPassword());
             Statement stmt = conn.createStatement()) {

            if ("query".equals(op)) {
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int n = meta.getColumnCount();
                    List<Map<String, Object>> results = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= n; i++) {
                            row.put(meta.getColumnLabel(i), rs.getObject(i));
                        }
                        results.add(row);
                    }
                    if (node.getOutputVariableKey() != null && !node.getOutputVariableKey().isEmpty()) {
                        ctx.getVariables().set(node.getOutputVariableKey(), results);
                    }
                }
            } else if ("change".equals(op)) {
                stmt.executeUpdate(sql);
            }
        } catch (Exception e) {
            log.warn("[Engine] SQL error: {}", e.getMessage());
            ctx.setStatus("ABORT");
            Map<String, Object> err = new HashMap<>();
            err.put("error", "SQL执行失败: " + e.getMessage());
            ctx.setErrorData(err);
            return "";
        }
        return nextOutgoing(node);
    }

    private String nextOutgoing(FlowElement node) {
        if (node.getOutgoings() != null && !node.getOutgoings().isEmpty()) {
            return node.getOutgoings().get(0);
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private Object getNestedMap(Map<String, Object> data, String key) {
        if (data == null) {
            return null;
        }
        if (data.containsKey(key)) {
            return data.get(key);
        }
        // 点号嵌套
        String[] parts = key.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
        }
        return current;
    }
}
