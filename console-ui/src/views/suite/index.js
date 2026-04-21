import ApiList from './ApiList.vue';
import RouterNest from '@/views/RouterNest.vue';
import ApiDebug from './ApiDebug.vue';
import SuiteList from './SuiteList.vue';

export const SuiteRoutes = [
  {
    path: 'suite',
    name: 'suite',
    component: RouterNest,
    redirect: () => ({ name: 'suite-list' }),
    meta: { name: 'route.suite' },
    children: [
      {
        path: 'list',
        name: 'suite-list',
        component: SuiteList,
        meta: { name: 'route.suiteList' },
      },
      {
        path: 'api/:suiteCode/:suiteId',
        name: 'api-list',
        component: ApiList,
        meta: { name: 'route.apiList' },
      },
      {
        path: 'debug/:apiId',
        name: 'api-debug',
        component: ApiDebug,
        meta: { name: 'route.apiDebug' },
      },
    ],
  },
];
