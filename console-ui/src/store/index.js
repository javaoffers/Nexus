import { createStore } from 'vuex'
import flow from './modules/flow'

const store = createStore({
  modules: {
    flow,
  },
})

export default store
