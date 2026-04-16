package engine

import (
	"fmt"
	"strconv"
	"strings"
	"sync"
)

// VariableManager stores and retrieves variables during flow execution.
type VariableManager struct {
	mu   sync.RWMutex
	data map[string]interface{}
}

func NewVariableManager() *VariableManager {
	return &VariableManager{
		data: make(map[string]interface{}),
	}
}

func (vm *VariableManager) Set(key string, value interface{}) {
	vm.mu.Lock()
	defer vm.mu.Unlock()
	vm.data[key] = value
}

func (vm *VariableManager) Get(key string) (interface{}, bool) {
	vm.mu.RLock()
	defer vm.mu.RUnlock()

	// Direct lookup
	if val, ok := vm.data[key]; ok {
		return val, true
	}

	// Support dot-notation for nested access: "varName.field.subfield"
	if strings.Contains(key, ".") {
		return vm.getNestedValue(key)
	}

	return nil, false
}

func (vm *VariableManager) GetAll() map[string]interface{} {
	vm.mu.RLock()
	defer vm.mu.RUnlock()
	cp := make(map[string]interface{}, len(vm.data))
	for k, v := range vm.data {
		cp[k] = v
	}
	return cp
}

func (vm *VariableManager) getNestedValue(key string) (interface{}, bool) {
	parts := strings.Split(key, ".")
	if len(parts) < 2 {
		return nil, false
	}

	val, ok := vm.data[parts[0]]
	if !ok {
		return nil, false
	}

	for _, part := range parts[1:] {
		switch v := val.(type) {
		case map[string]interface{}:
			val, ok = v[part]
			if !ok {
				return nil, false
			}
		default:
			return nil, false
		}
	}
	return val, true
}

// ResolveValue resolves a value from the variable manager or returns the constant.
func (vm *VariableManager) ResolveValue(source string, sourceType string) interface{} {
	switch sourceType {
	case "VARIABLE":
		val, _ := vm.Get(source)
		return val
	case "CONSTANT":
		return source
	default:
		val, ok := vm.Get(source)
		if ok {
			return val
		}
		return source
	}
}

// CoerceValue converts a value to the expected type string.
func CoerceValue(value interface{}, dataType string) interface{} {
	if value == nil {
		return nil
	}
	switch strings.ToLower(dataType) {
	case "integer", "int":
		return toInt64(value)
	case "double", "float":
		return toFloat64(value)
	case "boolean", "bool":
		return toBool(value)
	case "string":
		return fmt.Sprintf("%v", value)
	default:
		return value
	}
}

func toInt64(v interface{}) int64 {
	switch val := v.(type) {
	case int:
		return int64(val)
	case int64:
		return val
	case float64:
		return int64(val)
	case string:
		n, _ := strconv.ParseInt(val, 10, 64)
		return n
	default:
		return 0
	}
}

func toFloat64(v interface{}) float64 {
	switch val := v.(type) {
	case float64:
		return val
	case int:
		return float64(val)
	case int64:
		return float64(val)
	case string:
		f, _ := strconv.ParseFloat(val, 64)
		return f
	default:
		return 0
	}
}

func toBool(v interface{}) bool {
	switch val := v.(type) {
	case bool:
		return val
	case string:
		return val == "true" || val == "1"
	case float64:
		return val != 0
	case int:
		return val != 0
	default:
		return false
	}
}
