import AboutView from './AboutView.vue';
import SettingView from './SettingView.vue';
import UserView from './UserView.vue';

export const CommonRoutes = [
  {
    path: 'about',
    name: 'about',
    component: AboutView,
    meta: { name: 'route.about' },
  },
  {
    path: 'setting',
    name: 'setting',
    component: SettingView,
    meta: { name: 'route.setting' },
  },
  {
    path: 'user',
    name: 'user',
    component: UserView,
    meta: { name: 'route.user' },
  },
];
