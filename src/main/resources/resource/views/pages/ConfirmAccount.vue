<template>
  <CContainer class="d-flex align-items-center min-vh-100">
    <CRow class="w-100 justify-content-center">
      <CCol md="6">
        <div class="w-200">
            <div class="clearfix">
                <h4 class="pt-3 d-flex justify-content-center">{{comment}}</h4>
                <div v-if="showsLoadingMark" class="waiting-loader-token-verification" style="opacity 400ms"></div>
            </div>
      </div>
      </CCol>
    </CRow>
  </CContainer>
</template>

<script>
export default {
  name: 'ConfirmAccount',
   data() {
    return {
        user_token: null,
        showsLoadingMark: true,
        jwtToken: null,
        xsrfToken: null,
        comment: "Please wait. Checking the token."
    };
  },
  created () {
    this.user_token = this.$route.params.user_token;
    this.xsrf = this.getCookie("XSRF-TOKEN");
    if (this.isSignedIn()){
      this.$router.push({ name: "Profile" });
      return;
    }
  },
  mounted () {
    axios({
        method: 'post',
        headers: {
            "X-XSRF-TOKEN": this.xsrfToken
        },
        url: '/confirm_account',
        data: {
            confirmationToken: this.user_token,
        }
    }).then((response) => {
        if (response.data.successStatus == 1) {
          this.$router.push({ path: "/login?status=verification_success" });
        } else if (response.data.successStatus == 2) {
          this.$router.push({ path: "/login?status=verification_exists" });
        } else if (response.data.successStatus == 3) {
          this.showsLoadingMark = false;
          this.comment = "This token is already expired. Please register again."
        } else if (response.data.successStatus == 4) {
          this.showsLoadingMark = false;
          this.comment = "Failed to Validate the token. This token is invalid."
        }
        console.log(response);
    })
    .catch((error) => {
        this.showsLoadingMark = false;
        this.comment = "Failed to Validate the token. Please try again later."
    });
  },
  methods: {
    errors(res) {
      this.form.errors = res.errors;
      console.log(this.form.errors);
    },
  },
}
</script>
