import { ElementType } from '@/views/flow/design/types';
import i18n from '@/i18n';

const t = i18n.global.t;

export const nodeMap = {
  [ElementType.METHOD]: {
    get nodeName() { return t('design.methodNode'); },
    nodeHeaderColor: '#34C759',
  },
  [ElementType.CONDITION]: {
    get nodeName() { return t('design.conditionNode'); },
    nodeHeaderColor: '#FAAD14',
  },
  [ElementType.ASSIGN]: {
    get nodeName() { return t('design.assignNode'); },
    nodeHeaderColor: '#F5A623',
  },
  [ElementType.CODE]: {
    get nodeName() { return t('design.codeNode'); },
    nodeHeaderColor: '#8B5CF6',
  },
  [ElementType.MYSQL]: {
    get nodeName() { return t('design.mysqlNode'); },
    nodeHeaderColor: '#0FC6C2',
  },
  [ElementType.AI]: {
    get nodeName() { return t('design.aiNode'); },
    nodeHeaderColor: '#F53F3F',
  },
};
