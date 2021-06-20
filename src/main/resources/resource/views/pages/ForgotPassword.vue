<template>
  <CContainer class="d-flex align-items-center min-vh-100">
    <CRow class="w-100 justify-content-center">
      <CCol md="6">
        <div class="w-200">
            <div class="clearfix">
                <h4 class="pt-3 d-flex justify-content-center">{{comment}}</h4>
                <div v-if="showsLoadingMask" class="waiting-loader-token-verification" style="opacity 400ms"></div>
            </div>
      </div>
      </CCol>
    </CRow>
  </CContainer>
</template>

<script>
export default {
  name: 'ForgotPassword',
   data() {
    return {
        user_token: null,
        showsLoadingMask: true,
        jwtToken: null,
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
    this.showsLoadingMask = true;
    axios({
        method: 'post',
        headers: {
            "X-XSRF-TOKEN": this.xsrfToken
        },
        url: '/confirm_forgot_password_token',
        data: {
            confirmationToken: this.user_token,
        }
    }).then((response) => {
        if (response.data.successStatus == 1) {
          this.$router.push({ path: "/change_password/" + this.user_token });
        } else if (response.data.successStatus == 3) {
          this.comment = "This token is already expired."
        } else {
          this.comment = "Failed to Validate the token. This token is invalid."
        }
        this.showsLoadingMask = false;
        console.log(response);
    })
    .catch((error) => {
        this.showsLoadingMask = false;
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
