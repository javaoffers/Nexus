export const ElementType = {
  START: 'START',
  METHOD: 'METHOD',
  CONDITION: 'CONDITION',
  CODE: 'CODE',
  ASSIGN: 'ASSIGN',
  MYSQL: 'MYSQL',
  AI: 'AI',
  END: 'END',

  // 前端创建的
  BRANCH: 'BRANCH',
} as const;

export type ElementType = (typeof ElementType)[keyof typeof ElementType];

export const FlowVariableType = {
  INPUT: 1,
  OUTPUT: 2,
  TEMP: 3,
};

export interface RawData {
  key: string;
  name: string;
  elementType: string;
  outgoings: string[];
  incomings: string[];
  desc?: string;
  conditions?: Array<{
    conditionName: string;
    conditionType: string;
    expression: string;
    outgoing: string;
    conditionExpressions: any[];
  }>;
  [key: string]: any;
}

export type MyOptional<T, K extends keyof T> = Required<Pick<T, K>> & Partial<Omit<T, K>>;

export interface FlowContext {
  data: { value: any };
  update: (updater: (state: any) => void) => void;
  getFlowNode: (key: string) => any;
  getFlowNodes: () => any[];
  updateFlowNode: (key: string, data: any) => void;
}
