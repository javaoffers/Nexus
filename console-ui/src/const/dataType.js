import i18n from '@/i18n';

export const DataTypeMap = new Proxy({}, {
  get(_, key) {
    const t = i18n.global.t;
    const map = {
      String: t('dataTypeLabel.string'),
      Integer: t('dataTypeLabel.integer'),
      Double: t('dataTypeLabel.double'),
      Boolean: t('dataTypeLabel.boolean'),
      Date: t('dataTypeLabel.date'),
      Time: t('dataTypeLabel.time'),
      Object: t('dataTypeLabel.object'),
      List: t('dataTypeLabel.list'),
    };
    return map[key];
  },
});
