package com.javaoffers.nexus.core.engine;

import lombok.Data;

import java.util.List;

/** 对应 Go engine.ConditionItem：CONDITION 节点的一条分支。 */
@Data
public class ConditionItem {
    private String conditionName;
    private String conditionType;   // DEFAULT or CUSTOM
    private String outgoing;
    private String expression;
    private List<List<ConditionExpr>> conditionExpressions;   // OR(外) / AND(内)
}
