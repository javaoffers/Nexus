package com.javaoffers.nexus.core.engine;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 对应 Go engine/expression.go：用 Aviator 求值布尔表达式，并按结构化条件生成表达式串。
 * 注册自定义 contains / string 函数以兼容前端 expr-lang 语法（contains(string(x),"y")）。
 */
@Slf4j
@Component
public class ExpressionService {

    static {
        try {
            AviatorEvaluator.getInstance().addFunction(new ContainsFunction());
            AviatorEvaluator.getInstance().addFunction(new StringFunction());
        } catch (Exception e) {
            // 静态注册失败不影响启动，运行时求值会按异常处理
        }
    }

    /** 求值布尔表达式；空串视为 true；失败返回 false（与 Go 行为一致：记日志后跳过该分支）。 */
    public boolean evalExpression(String expression, VariableManager vm) {
        if (expression == null || expression.isEmpty()) {
            return true;
        }
        try {
            Object r = AviatorEvaluator.execute(expression, vm.getAll());
            if (r instanceof Boolean) {
                return (Boolean) r;
            }
            return false;
        } catch (Exception e) {
            log.warn("[Engine] Expression eval error: {} | expr={}", e.getMessage(), expression);
            return false;
        }
    }

    /** CODE 节点求值；失败返回 null（非致命，继续执行）。 */
    public Object evalCode(String content, VariableManager vm) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            return AviatorEvaluator.execute(content, vm.getAll());
        } catch (Exception e) {
            log.warn("[Engine] Code node run error: {} | content={}", e.getMessage(), content);
            return null;
        }
    }

    /** 对应 Go GenerateExpression：外层 OR、内层 AND。 */
    public String generateExpression(List<List<ConditionExpr>> groups) {
        if (groups == null || groups.isEmpty()) {
            return "true";
        }
        StringBuilder orParts = new StringBuilder();
        for (List<ConditionExpr> group : groups) {
            StringBuilder andParts = new StringBuilder();
            for (ConditionExpr ce : group) {
                String part = buildSingleCondition(ce);
                if (!part.isEmpty()) {
                    if (andParts.length() > 0) andParts.append(" && ");
                    andParts.append(part);
                }
            }
            if (andParts.length() > 0) {
                if (orParts.length() > 0) orParts.append(" || ");
                orParts.append("(").append(andParts).append(")");
            }
        }
        if (orParts.length() == 0) {
            return "true";
        }
        return orParts.toString();
    }

    private String buildSingleCondition(ConditionExpr ce) {
        String varKey = ce.getVariableKey();
        Object value = ce.getValue();
        String valueStr;
        if ("CONSTANT".equals(ce.getAssignType())) {
            valueStr = formatConstant(value);
        } else {
            valueStr = String.valueOf(value);
        }
        switch (ce.getOperator()) {
            case "equal":
                return varKey + " == " + valueStr;
            case "notEqual":
                return varKey + " != " + valueStr;
            case "greaterThan":
                return varKey + " > " + valueStr;
            case "greaterThanOrEqual":
                return varKey + " >= " + valueStr;
            case "lessThan":
                return varKey + " < " + valueStr;
            case "lessThanOrEqual":
                return varKey + " <= " + valueStr;
            case "empty":
                return varKey + " == nil || " + varKey + " == \"\"";
            case "notEmpty":
                return varKey + " != nil && " + varKey + " != \"\"";
            case "contains":
                return "contains(string(" + varKey + "), " + valueStr + ")";
            case "notContains":
                return "!contains(string(" + varKey + "), " + valueStr + ")";
            default:
                return "";
        }
    }

    private String formatConstant(Object value) {
        if (value == null) {
            return "nil";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return String.valueOf(value);
    }

    /** contains(a, b)：a 是否包含子串 b。 */
    public static class ContainsFunction extends AbstractFunction {
        @Override
        public String getName() {
            return "contains";
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject a, AviatorObject b) {
            Object av = a.getValue(env);
            Object bv = b.getValue(env);
            if (av == null) {
                return AviatorBoolean.FALSE;
            }
            String as = String.valueOf(av);
            String bs = bv == null ? "" : String.valueOf(bv);
            return AviatorBoolean.valueOf(as.contains(bs));
        }
    }

    /** string(x)：转字符串，null 视为空串。 */
    public static class StringFunction extends AbstractFunction {
        @Override
        public String getName() {
            return "string";
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject a) {
            Object v = a.getValue(env);
            return AviatorRuntimeJavaType.valueOf(v == null ? "" : String.valueOf(v));
        }
    }
}
