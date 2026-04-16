package engine

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"

	"github.com/expr-lang/expr"
	"github.com/nexus-flow/nexus/internal/repository"
)

func executeStartNode(ctx *ExecutionContext, node *FlowElement) string {
	if len(node.Outgoings) > 0 {
		return node.Outgoings[0]
	}
	return ""
}

func executeEndNode(ctx *ExecutionContext, node *FlowElement) string {
	ctx.Status = "FINISH"
	return ""
}

func executeMethodNode(ctx *ExecutionContext, node *FlowElement) string {
	var method MethodDef
	if err := json.Unmarshal(node.Method, &method); err != nil {
		log.Printf("[Engine] Failed to parse method node: %v", err)
		ctx.Status = "ABORT"
		ctx.ErrorData = map[string]interface{}{"error": fmt.Sprintf("解析方法节点失败: %v", err)}
		return ""
	}

	// Build headers from fill rules
	headers := make(map[string]interface{})
	for _, rule := range method.HeaderFillRules {
		headers[rule.Target] = ctx.Variables.ResolveValue(rule.Source, rule.SourceType)
	}

	// Build request params from fill rules
	params := make(map[string]interface{})
	for _, rule := range method.InputFillRules {
		params[rule.Target] = ctx.Variables.ResolveValue(rule.Source, rule.SourceType)
	}

	// Send HTTP request
	result, err := SendHTTPRequest(method, headers, params)
	if err != nil {
		log.Printf("[Engine] HTTP request failed: %v", err)
		ctx.Status = "ABORT"
		ctx.ErrorData = map[string]interface{}{"error": fmt.Sprintf("HTTP调用失败: %v", err)}
		return ""
	}

	// Map output fill rules: response fields → variables
	for _, rule := range method.OutputFillRules {
		var val interface{}
		if rule.SourceType == "OUTPUT_PARAM" {
			// Read from response
			val = getNestedMap(result, rule.Source)
		} else {
			val = ctx.Variables.ResolveValue(rule.Source, rule.SourceType)
		}
		ctx.Variables.Set(rule.Target, val)
	}

	if len(node.Outgoings) > 0 {
		return node.Outgoings[0]
	}
	return ""
}

func executeConditionNode(ctx *ExecutionContext, node *FlowElement) string {
	var conditions []ConditionItem
	if err := json.Unmarshal(node.Conditions, &conditions); err != nil {
		log.Printf("[Engine] Failed to parse condition node: %v", err)
		ctx.Status = "ABORT"
		return ""
	}

	var defaultOutgoing string
	for _, cond := range conditions {
		if cond.ConditionType == "DEFAULT" {
			defaultOutgoing = cond.Outgoing
			continue
		}

		// Build expression
		expression := cond.Expression
		if expression == "" && len(cond.ConditionExpressions) > 0 {
			expression = GenerateExpression(cond.ConditionExpressions)
		}

		matched, err := EvalExpression(expression, ctx.Variables)
		if err != nil {
			log.Printf("[Engine] Expression eval error: %v", err)
			continue
		}
		if matched {
			return cond.Outgoing
		}
	}

	if defaultOutgoing != "" {
		return defaultOutgoing
	}

	log.Printf("[Engine] No condition matched and no default branch")
	return ""
}

func executeAssignNode(ctx *ExecutionContext, node *FlowElement) string {
	var rules []FillRule
	if err := json.Unmarshal(node.AssignRules, &rules); err != nil {
		log.Printf("[Engine] Failed to parse assign rules: %v", err)
		ctx.Status = "ABORT"
		return ""
	}

	for _, rule := range rules {
		val := ctx.Variables.ResolveValue(rule.Source, rule.SourceType)
		ctx.Variables.Set(rule.Target, val)
	}

	if len(node.Outgoings) > 0 {
		return node.Outgoings[0]
	}
	return ""
}

func executeCodeNode(ctx *ExecutionContext, node *FlowElement) string {
	if node.Content == "" {
		if len(node.Outgoings) > 0 {
			return node.Outgoings[0]
		}
		return ""
	}

	// Execute as an expression using expr-lang
	env := ctx.Variables.GetAll()
	program, err := expr.Compile(node.Content, expr.Env(env))
	if err != nil {
		log.Printf("[Engine] Code node compile error: %v", err)
		// Non-fatal: continue execution
	} else {
		result, err := expr.Run(program, env)
		if err != nil {
			log.Printf("[Engine] Code node run error: %v", err)
		} else {
			// If the result is a map, merge into variables
			if m, ok := result.(map[string]interface{}); ok {
				for k, v := range m {
					ctx.Variables.Set(k, v)
				}
			}
		}
	}

	if len(node.Outgoings) > 0 {
		return node.Outgoings[0]
	}
	return ""
}

func executeMysqlNode(ctx *ExecutionContext, node *FlowElement) string {
	if node.DataSourceID == nil || node.SQL == "" {
		log.Printf("[Engine] MySQL node missing datasource or SQL")
		if len(node.Outgoings) > 0 {
			return node.Outgoings[0]
		}
		return ""
	}

	ds, err := repository.GetDataSourceByID(*node.DataSourceID)
	if err != nil {
		log.Printf("[Engine] DataSource not found: %v", err)
		ctx.Status = "ABORT"
		ctx.ErrorData = map[string]interface{}{"error": "数据源不存在"}
		return ""
	}

	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?charset=utf8mb4&parseTime=True",
		ds.UserName, ds.Password, ds.Address, ds.Port, ds.DatabaseName)

	db, err := sql.Open("mysql", dsn)
	if err != nil {
		log.Printf("[Engine] Failed to connect: %v", err)
		ctx.Status = "ABORT"
		return ""
	}
	defer db.Close()

	// Replace variables in SQL
	sqlStr := node.SQL
	vars := ctx.Variables.GetAll()
	for k, v := range vars {
		sqlStr = replaceAll(sqlStr, "#{"+k+"}", fmt.Sprintf("%v", v))
	}

	switch node.OperationType {
	case "query":
		rows, err := db.Query(sqlStr)
		if err != nil {
			log.Printf("[Engine] SQL query error: %v", err)
			ctx.Status = "ABORT"
			ctx.ErrorData = map[string]interface{}{"error": fmt.Sprintf("SQL查询失败: %v", err)}
			return ""
		}
		defer rows.Close()

		columns, _ := rows.Columns()
		var results []map[string]interface{}
		for rows.Next() {
			values := make([]interface{}, len(columns))
			valuePtrs := make([]interface{}, len(columns))
			for i := range values {
				valuePtrs[i] = &values[i]
			}
			rows.Scan(valuePtrs...)
			row := make(map[string]interface{})
			for i, col := range columns {
				val := values[i]
				if b, ok := val.([]byte); ok {
					row[col] = string(b)
				} else {
					row[col] = val
				}
			}
			results = append(results, row)
		}

		if node.OutputVariableKey != "" {
			ctx.Variables.Set(node.OutputVariableKey, results)
		}

	case "change":
		_, err := db.Exec(sqlStr)
		if err != nil {
			log.Printf("[Engine] SQL exec error: %v", err)
			ctx.Status = "ABORT"
			ctx.ErrorData = map[string]interface{}{"error": fmt.Sprintf("SQL执行失败: %v", err)}
			return ""
		}
	}

	if len(node.Outgoings) > 0 {
		return node.Outgoings[0]
	}
	return ""
}

func getNestedMap(data map[string]interface{}, key string) interface{} {
	if val, ok := data[key]; ok {
		return val
	}
	// Try dot notation
	parts := splitDot(key)
	var current interface{} = data
	for _, part := range parts {
		if m, ok := current.(map[string]interface{}); ok {
			current = m[part]
		} else {
			return nil
		}
	}
	return current
}

func splitDot(s string) []string {
	var parts []string
	current := ""
	for _, c := range s {
		if c == '.' {
			if current != "" {
				parts = append(parts, current)
				current = ""
			}
		} else {
			current += string(c)
		}
	}
	if current != "" {
		parts = append(parts, current)
	}
	return parts
}

func replaceAll(s, old, new string) string {
	for {
		i := indexOf(s, old)
		if i < 0 {
			return s
		}
		s = s[:i] + new + s[i+len(old):]
	}
}

func indexOf(s, substr string) int {
	for i := 0; i+len(substr) <= len(s); i++ {
		if s[i:i+len(substr)] == substr {
			return i
		}
	}
	return -1
}
