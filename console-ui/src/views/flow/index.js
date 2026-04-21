import FlowDefineList from './FlowDefineList.vue';
import FlowList from './FlowList.vue';
import FlowVersionList from './FlowVersionList.vue';
import FlowDebug from './FlowDebug.vue';
import RouterNest from '@/views/RouterNest.vue';

export const FlowRoutes = [
  {
    path: 'flow',
    name: 'flow',
    component: RouterNest,
    redirect: () => ({ name: 'flow-define' }),
    meta: { name: 'route.flow' },
    children: [
      {
        path: 'define',
        name: 'flow-define',
        component: FlowDefineList,
        meta: { name: 'route.flowDefine' },
      },
      {
        path: 'list',
        name: 'flow-api',
        component: FlowList,
        meta: { name: 'route.flowList' },
      },
      {
        path: 'version/:flowId',
        name: 'flow-version',
        component: FlowVersionList,
        meta: { name: 'route.flowVersion' },
      },
      {
        path: 'debug/:flowDefinitionId/:flowKey',
        name: 'flow-debug',
        component: FlowDebug,
        meta: { name: 'route.flowDebug' },
      },
    ],
  },
];
