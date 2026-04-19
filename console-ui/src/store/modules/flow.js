export default {
  namespaced: true,
  state() {
    return {
      flowKey: '',
      flowName: '',
      flowType: '',
      flowContent: [],
      flowInputParams: [],
      flowOutputParams: [],
      flowVariables: [],
      id: null,
      remark: '',
    }
  },
  getters: {
    getFlowNodes(state) {
      return state.flowContent
    },
    getFlowNode: (state) => (key) => {
      return state.flowContent.find(node => node.key === key)
    },
  },
  mutations: {
    SET_FLOW_DATA(state, data) {
      Object.assign(state, data)
    },
    UPDATE_FLOW_CONTENT(state, updater) {
      updater(state)
    },
    UPDATE_FLOW_NODE(state, { key, data }) {
      const node = state.flowContent.find(n => n.key === key)
      if (node) {
        Object.assign(node, data)
      }
    },
    SET_FLOW_CONTENT(state, content) {
      state.flowContent = content
    },
  },
}
