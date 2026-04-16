package engine

import (
	"fmt"
	"strings"

	"github.com/expr-lang/expr"
)

// EvalExpression evaluates a boolean expression with variables from the manager.
func EvalExpression(expression string, vm *VariableManager) (bool, error) {
	if expression == "" {
		return true, nil
	}

	env := vm.GetAll()
	program, err := expr.Compile(expression, expr.Env(env), expr.AsBool())
	if err != nil {
		return false, fmt.Errorf("compile expression: %w", err)
	}

	result, err := expr.Run(program, env)
	if err != nil {
		return false, fmt.Errorf("run expression: %w", err)
	}

	if b, ok := result.(bool); ok {
		return b, nil
	}
	return false, fmt.Errorf("expression did not return boolean")
}

// GenerateExpression builds an expression string from condition groups.
// Outer list = OR, inner list = AND.
func GenerateExpression(groups [][]ConditionExpr) string {
	if len(groups) == 0 {
		return "true"
	}

	var orParts []string
	for _, group := range groups {
		var andParts []string
		for _, ce := range group {
			part := buildSingleCondition(ce)
			if part != "" {
				andParts = append(andParts, part)
			}
		}
		if len(andParts) > 0 {
			orParts = append(orParts, "("+strings.Join(andParts, " && ")+")")
		}
	}

	if len(orParts) == 0 {
		return "true"
	}
	return strings.Join(orParts, " || ")
}

func buildSingleCondition(ce ConditionExpr) string {
	varKey := ce.VariableKey
	value := ce.Value

	// Resolve the comparison value
	var valueStr string
	if ce.AssignType == "CONSTANT" {
		valueStr = formatConstant(value)
	} else {
		valueStr = fmt.Sprintf("%v", value)
	}

	switch ce.Operator {
	case "equal":
		return fmt.Sprintf("%s == %s", varKey, valueStr)
	case "notEqual":
		return fmt.Sprintf("%s != %s", varKey, valueStr)
	case "greaterThan":
		return fmt.Sprintf("%s > %s", varKey, valueStr)
	case "greaterThanOrEqual":
		return fmt.Sprintf("%s >= %s", varKey, valueStr)
	case "lessThan":
		return fmt.Sprintf("%s < %s", varKey, valueStr)
	case "lessThanOrEqual":
		return fmt.Sprintf("%s <= %s", varKey, valueStr)
	case "empty":
		return fmt.Sprintf("%s == nil || %s == \"\"", varKey, varKey)
	case "notEmpty":
		return fmt.Sprintf("%s != nil && %s != \"\"", varKey, varKey)
	case "contains":
		return fmt.Sprintf("contains(string(%s), %s)", varKey, valueStr)
	case "notContains":
		return fmt.Sprintf("!contains(string(%s), %s)", varKey, valueStr)
	default:
		return ""
	}
}

func formatConstant(value interface{}) string {
	switch v := value.(type) {
	case string:
		return fmt.Sprintf(`"%s"`, v)
	case nil:
		return "nil"
	default:
		return fmt.Sprintf("%v", v)
	}
}
