<template>
  <CContainer class="min-vh-100">
    <CRow class="w-100">
      <CCol md="12">
        <div class="w-200">
          <div class="clearfix">
            <h4 class="pt-3">About me.</h4>
            <p>Please note that this website is managed by <strong>an idividual</strong>, not a company, so this website can be closed anytime without notice. (e.g., when I cannot afford the server fee.)</p>
            <h4>The email address is as below:</h4>
            <p class="text-muted">chatboard.contact@gmail.com</p>
          </div>
        </div>
      </CCol>
    </CRow>
    <CRow class="w-100">
      <CCol md="12">
        <div class="w-200">
          <div class="clearfix">
            <span class="pointer" style="color: #321fdb;" @click="$router.push({ name: 'PrivacyPolicy' });">Privacy Policy</span>
          </div>
        </div>
      </CCol>
    </CRow>
    <CRow class="w-100">
      <CCol md="12">
        <div class="w-200">
          <div class="clearfix">
            <span class="pointer" style="color: #321fdb;" @click="$router.push({ name: 'TermOfUse' });">Term of use</span>
          </div>
        </div>
      </CCol>
    </CRow>
  </CContainer>
</template>

<script>
export default {
  name: "AboutUs",
};
</script>
