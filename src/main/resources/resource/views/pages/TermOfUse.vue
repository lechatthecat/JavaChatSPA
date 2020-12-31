<template>
  <CContainer class="min-vh-100">
    <CRow class="w-100">
      <CCol md="12">
        <div class="w-200">
          <div class="clearfix">
            <h1 class="pt-3">Term of use</h1>
            <p>Please note that this website is managed by <strong>an idividual</strong>, not a company, so this website can be closed anytime without notice. (e.g., when I cannot afford the server fee.)</p>
            <h4>Terms</h4>
            <p>By accessing this website, you are agreeing to be bound by these website Terms and Conditions of Use, our <span class="pointer" style="color: #321fdb;" @click="$router.push({ name: 'PrivacyPolicy' });">Privacy Policy</span>, all applicable laws and regulations, and agree that you are responsible for compliance with any applicable local laws. If you do not agree with any of these terms, you are prohibited from using or accessing this site. The materials contained in this website are protected by applicable copyright and trade mark law.</p>
            <h4>Disclaimer</h4>
            <p>The materials on this website are provided "as is". We makes no warranties, expressed or implied, and hereby disclaims and negates all other warranties, including without limitation, implied warranties or conditions of merchantability, fitness for a particular purpose, or non-infringement of intellectual property or other violation of rights. Further, we does not warrant or make any representations concerning the accuracy, likely results, or reliability of the use of the materials on its Internet website or otherwise relating to such materials or on any sites linked to this site.</p>
            <h4>Limitations</h4>
            <p>In no event shall we or its suppliers be liable for any damages (including, without limitation, damages for loss of data or profit, or due to business interruption,) arising out of the use or inability to use the materials on our Internet site, even if we or an authorized representative has been notified orally or in writing of the possibility of such damage. Because some jurisdictions do not allow limitations on implied warranties, or limitations of liability for consequential or incidental damages, these limitations may not apply to you.</p>
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
