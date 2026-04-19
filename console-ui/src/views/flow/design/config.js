import { ElementType } from '@/views/flow/design/types';

export const nodeMap = {
  [ElementType.METHOD]: {
    nodeName: '方法节点',
    nodeHeaderColor: '#34C759',
  },
  [ElementType.CONDITION]: {
    nodeName: '判断节点',
    nodeHeaderColor: '#FAAD14',
  },
  [ElementType.ASSIGN]: {
    nodeName: '赋值节点',
    nodeHeaderColor: '#F5A623',
  },
  [ElementType.CODE]: {
    nodeName: '代码节点',
    nodeHeaderColor: '#8B5CF6',
  },
  [ElementType.MYSQL]: {
    nodeName: 'MySql节点',
    nodeHeaderColor: '#0FC6C2',
  },
  [ElementType.AI]: {
    nodeName: 'AI节点',
    nodeHeaderColor: '#F53F3F',
  },
};
