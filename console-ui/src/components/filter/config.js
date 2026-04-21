import i18n from '@/i18n';

export const BaseDataType = {
  String: 'String',
  Integer: 'Integer',
  Double: 'Double',
  Boolean: 'Boolean',
  Date: 'Date',
  Time: 'Time',
  List: 'List',
  Object: 'Object',
};

export const Operator = {
  Empty: 'empty',
  NotEmpty: 'notEmpty',
  Equal: 'equal',
  NotEqual: 'notEqual',
  GreaterThan: 'greaterThan',
  LessThan: 'lessThan',
  GreaterThanOrEqual: 'greaterThanOrEqual',
  LessThanOrEqual: 'lessThanOrEqual',
  Contains: 'contains',
  NotContains: 'notContains',
};

export const OperatorNameMap = new Proxy({}, {
  get(target, prop) {
    const t = i18n.global.t;
    const map = {
      [Operator.Empty]: t('filter.empty'),
      [Operator.NotEmpty]: t('filter.notEmpty'),
      [Operator.Equal]: t('filter.equal'),
      [Operator.NotEqual]: t('filter.notEqual'),
      [Operator.GreaterThan]: t('filter.greaterThan'),
      [Operator.LessThan]: t('filter.lessThan'),
      [Operator.GreaterThanOrEqual]: t('filter.greaterThanOrEqual'),
      [Operator.LessThanOrEqual]: t('filter.lessThanOrEqual'),
      [Operator.Contains]: t('filter.contains'),
      [Operator.NotContains]: t('filter.notContains'),
    };
    return map[prop];
  },
});

export const DataTypeOperatorMap = {
  [BaseDataType.String]: [Operator.Equal, Operator.NotEqual, Operator.Empty, Operator.NotEmpty, Operator.Contains, Operator.NotContains],
  [BaseDataType.Integer]: [
    Operator.Equal,
    Operator.NotEqual,
    Operator.GreaterThan,
    Operator.GreaterThanOrEqual,
    Operator.LessThan,
    Operator.LessThanOrEqual,
  ],
  [BaseDataType.Double]: [
    Operator.Equal,
    Operator.NotEqual,
    Operator.GreaterThan,
    Operator.GreaterThanOrEqual,
    Operator.LessThan,
    Operator.LessThanOrEqual,
  ],
  [BaseDataType.Boolean]: [Operator.Equal, Operator.NotEqual],
  [BaseDataType.Date]: [
    Operator.Equal,
    Operator.NotEqual,
    Operator.GreaterThan,
    Operator.GreaterThanOrEqual,
    Operator.LessThan,
    Operator.LessThanOrEqual,
  ],
  [BaseDataType.Time]: [
    Operator.Equal,
    Operator.NotEqual,
    Operator.GreaterThan,
    Operator.GreaterThanOrEqual,
    Operator.LessThan,
    Operator.LessThanOrEqual,
  ],
  [BaseDataType.List]: [Operator.Empty, Operator.NotEmpty],
  [BaseDataType.Object]: [Operator.Empty, Operator.NotEmpty],
};
