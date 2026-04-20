package engine

// ExecutionContext holds the state during flow execution.
type ExecutionContext struct {
	/**
	 * instance id.
	 */
	InstanceID string
	ElementMap map[string]*FlowElement
	Variables  *VariableManager
	Status     string // RUNNING, FINISH, ABORT
	ErrorData  map[string]interface{}
}
