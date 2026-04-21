import { createRouter, createWebHashHistory } from 'vue-router';
import LayoutView from '../views/LayoutView.vue';
import LoginView from '../views/LoginView.vue';
import NotFound from '../views/NotFound.vue';
import { FlowRoutes } from '../views/flow';
import { CommonRoutes } from '../views/common';
import ObjectList from '@/views/object/ObjectList.vue';
import { SystemRoutes } from '@/views/system';
import { SuiteRoutes } from '@/views/suite';
import FlowDesign from '@/views/flow/FlowDesign.vue';

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { name: 'route.login' },
    },
    {
      path: '/design/:flowDefinitionId/:flowKey',
      name: 'flow-design',
      component: FlowDesign,
      meta: { name: 'route.flowDesign' },
    },
    {
      path: '/',
      name: 'index',
      component: LayoutView,
      redirect: () => ({ name: 'flow-define' }),
      meta: { name: 'route.home' },
      children: [
        /*{
          path: '',
          name: 'home',
          component: HomeView,
          meta: { name: 'route.home' },
        },*/
        ...CommonRoutes,
        ...FlowRoutes,
        ...SuiteRoutes,
        {
          path: 'object/list',
          name: 'object-list',
          component: ObjectList,
          meta: { name: 'route.object' },
        },
        ...SystemRoutes,
        {
          path: '/:pathMatch(.*)*',
          name: 'notfound',
          component: NotFound,
          meta: { name: 'route.notFound' },
        },
      ],
    },
  ],
});

export default router;
