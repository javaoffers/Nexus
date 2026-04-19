import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'vue-json-pretty/lib/styles.css'
import 'element-plus/dist/index.css'
import './assets/theme.css'
import './assets/base.css'
import { userService } from './service'

const app = createApp(App)

app.use(ElementPlus)
app.use(router)
app.use(store)

app.config.errorHandler = (err, instance, info) => {
  console.error(err)
}

async function startup() {
  var isLogin = await userService.check()
  if (!isLogin) {
    await router.push({ name: 'login' })
  }
  app.mount('#app')
}

startup()
