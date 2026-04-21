<script>
export default {
  data() {
    return {
      agreementDialogVisible: false,
    };
  },
  methods: {
    openUserAgreementDialog() {
      this.agreementDialogVisible = true;
    },
  },
};
</script>

<template>
  <el-dialog v-model="agreementDialogVisible" :title="$t('agreement.title')" width="650">
    <div class="content">
      <h2>{{ $t('agreement.intro') }}</h2>
      {{ $t('agreement.introText') }}

      <h2>{{ $t('agreement.terms') }}</h2>
      <h3>{{ $t('agreement.legalUse') }}</h3>
      <ul>
        <li>{{ $t('agreement.legalUseItem1') }}</li>
        <li>{{ $t('agreement.legalUseItem2') }}</li>
      </ul>

      <h3>{{ $t('agreement.noResale') }}</h3>
      <ul>
        <li>{{ $t('agreement.noResaleItem1') }}</li>
        <li>{{ $t('agreement.noResaleItem2') }}</li>
        <li>{{ $t('agreement.noResaleItem3') }}</li>
      </ul>

      <h3>{{ $t('agreement.userConduct') }}</h3>
      <ul>
        <li>{{ $t('agreement.userConductItem1') }}</li>
        <li>{{ $t('agreement.userConductItem2') }}</li>
        <li>{{ $t('agreement.userConductItem3') }}</li>
        <li>{{ $t('agreement.userConductItem4') }}</li>
      </ul>

      <h3>{{ $t('agreement.privacy') }}</h3>
      <ul>
        <li>{{ $t('agreement.privacyItem1') }}</li>
        <li>{{ $t('agreement.privacyItem2') }}</li>
      </ul>

      <h3>{{ $t('agreement.liability') }}</h3>
      <ul>
        <li>{{ $t('agreement.liabilityItem1') }}</li>
        <li>{{ $t('agreement.liabilityItem2') }}</li>
        <li>{{ $t('agreement.liabilityItem3') }}</li>
      </ul>

      <h3>{{ $t('agreement.termination') }}</h3>
      <ul>
        <li>{{ $t('agreement.terminationItem1') }}</li>
        <li>{{ $t('agreement.terminationItem2') }}</li>
      </ul>

      <h3>{{ $t('agreement.updates') }}</h3>
      <ul>
        <li>{{ $t('agreement.updatesItem1') }}</li>
        <li>{{ $t('agreement.updatesItem2') }}</li>
      </ul>

      <h2>{{ $t('agreement.otherTerms') }}</h2>
      <h3>{{ $t('agreement.jurisdiction') }}</h3>
      <ul>
        <li>{{ $t('agreement.jurisdictionItem1') }}</li>
        <li>{{ $t('agreement.jurisdictionItem2') }}</li>
      </ul>
    </div>
  </el-dialog>
</template>

<style scoped>
.content {
  line-height: 25px;
}
.content h2 {
  margin-bottom: 5px;
}
</style>
