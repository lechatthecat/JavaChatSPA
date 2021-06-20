<template>
  <div class="c-app flex-row align-items-center h100p">
    <CContainer>
      <b-row class="justify-content-center">
        <b-col md="8">
          <b-card-group>
            <b-card no-body class="p-4">
              <b-card-body>
                <b-form>
                  <h1>Sign in</h1>
                  <p class="text-muted">Sign in to your account</p>
                  <div
                    :class="'alert ' + alert_class"
                    role="alert"
                    v-show="shows_comment"
                  >
                    <p class="mb-0" v-if="hasErrors">Sign in failed.</p>
                    <p class="mb-0">{{comment}}</p>
                  </div>
                  <b-input-group class="mb-3">
                    <b-input-group-prepend
                      ><b-input-group-text style="width:35px"
                        ><CIcon name="cib-gmail"/></b-input-group-text
                      ></b-input-group-prepend
                    >
                    <b-form-input
                      type="text"
                      class="form-control"
                      placeholder="Email Adress"
                      autocomplete="email"
                      v-model="form.body.email"
                      v-on:keyup.enter="login"
                    />
                  </b-input-group>
                  <b-input-group class="mb-4">
                    <b-input-group-prepend
                      ><b-input-group-text style="width:35px"
                        ><CIcon name="cil-lock-locked" /></b-input-group-text
                    ></b-input-group-prepend>
                    <b-form-input
                      ref="loginPasswordConfirm"
                      type="password"
                      class="form-control"
                      placeholder="Password"
                      autocomplete="current-password"
                      v-model="form.body.password"
                      v-on:keyup.enter="login"
                    />
                    <span 
                      ref="passwordEye"
                      class="fa fa-fw fa-eye field-icon toggle-password"
                      @click="togglePasswordVisibility('loginPasswordConfirm', 'passwordEye')">
                    </span>
                  </b-input-group>
                  <b-row>
                    <b-col cols="12">
                      <b-button
                        variant="primary"
                        class="px-4 text-nowrap"
                        @click="login"
                        >Sign in</b-button
                      >
                      <CLink
                        variant="info d-lg-none"
                        href="/create_user"
                        class="btn btn-info text-nowrap px-4 d-lg-none sign-up-button"
                        >Sign up</CLink
                      >
                    </b-col>
                    <div class="text-right forgot-link">
                      <b-button @click="$router.push({ path: '/boards/lounge/0' });" variant="link" class="px-0">Boards</b-button>
                    </div>
                    <div class="text-right forgot-link">
                      <b-button @click="showForgotPasswordModal" variant="link" class="px-0">Forgot password?</b-button>
                    </div>
                  </b-row>
                </b-form>
              </b-card-body>
            </b-card>
            <b-card
              no-body
              class="text-white bg-primary py-5 d-md-down-none"
              style="width: 44%"
            >
              <b-card-body class="text-center">
                <div>
                  <h2>Sign up for your account</h2>
                  <p>Create your account from here</p>
                  <CLink
                    variant="info"
                    href="/create_user"
                    class="btn btn-info text-nowrap active px-4 mt-3"
                    >Sign up</CLink
                  >
                </div>
              </b-card-body>
            </b-card>
          </b-card-group>
        </b-col>
      </b-row>
    </CContainer>
    <div v-if="showModal">
      <div class="modal-mask" @click="showModal = false"></div>
      <transition name="modal">
        <div class="forgot-email-modal-wrapper">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="line-break-inside modal-body">
                <p>{{modal_comment}}</p>
                <input v-if="hasButton" v-model="emailAddressLarge" class="form-control form-control input-large w100p" placeholder="Please write your emaill address here" type="text" />
                <input v-if="hasButton" v-model="emailAddressSmall" class="form-control form-control input-small w100p" placeholder="Your emaill address here" type="text" />
                <div class="error-message" v-if="typeof forgot_email_error_message !== 'undefined' && forgot_email_error_message !== null && forgot_email_error_message.length>0">{{forgot_email_error_message}}</div>
              </div>
              <div v-if="hasButton" class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="showModal = false">No</button>
                <button type="button" class="btn btn-primary" @click="sendForgotPasswordEmail">Yes</button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
    <div v-if="showsLoadingMask">
      <div class="modal-mask-transparent justify-content-center align-items-center" style="z-index:1050;"></div>
      <div class="waiting-loader" style="opacity 400ms"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      form: {
        body: {
          email: null,
          password: null,
        },
        remember: true,
        fetchUser: true,
        staySignedIn: true,
        errors: {},
      },
      hasErrors: false,
      shows_comment: false,
      error_message: "",
      alert_class: "alert-danger",
      showModal: false,
      forgot_email_error_message: "",
      emailAddressLarge: null,
      emailAddressSmall: null,
      comment: null,
      modal_comment: "",
      showsLoadingMask: false,
      hasButton: false,
    };
  },
  created() {
    const isLogout = this.$route.meta.logout;
    if (isLogout) {
      this.$auth.logout();
    }
    this.xsrf = this.getCookie("XSRF-TOKEN");
  },
  mounted() {
    if (this.$route.query.status === "verification_success") {
      this.alert_class = "alert-info";
      this.comment = "Thank you for verification! You can login with your email now.";
      this.hasErrors = false;
      this.shows_comment = true;
    } else if (this.$route.query.status === "verification_exists") {
      this.alert_class = "alert-info";
      this.comment = "Your email address is already verified.";
      this.hasErrors = false;
      this.shows_comment = true;
    } else if (this.$route.query.status === "password_changed") {
      this.alert_class = "alert-info";
      this.comment = "Your password was successfully changed.";
      this.hasErrors = false;
      this.shows_comment = true;
    }
  },
  methods: {
    showForgotPasswordModal () {
      this.showModal = true;
      this.hasButton = true;
      this.modal_comment = "Do you want to send password reset link to your email?";
    },
    login() {
      this.showsLoadingMask = true;
      this.shows_comment = false;
      this.hasErrors = false;
      console.log("Trying to login...");
      console.log("auth:");
      console.log(this.$auth);
      this.$auth
        .login({
          data: this.form.body,
          rememberMe: this.form.remember,
          fetchUser: this.form.fetchUser,
          staySignedIn: this.form.staySignedIn,
        })
        .then(
          (res) => {
            console.log("server replied successfully.");
            console.log(res);
            this.success(res);
          },
          (res) => {
            this.showsLoadingMask = false;
            console.log("Errors found in form data.");
            console.log(res);
            this.errors(res);
          }
        );
    },
    success: function (res) {
      console.log("Login success");
      const jwtToken = "Bearer " + this.getCookie("chat_board_login_token");
      const xsrf = this.getCookie("XSRF-TOKEN");
      axios.defaults.headers.common["Authorization"] = jwtToken;
      axios.defaults.headers.common["X-XSRF-TOKEN"] = xsrf;
      // handle redirection
      const redirectTo = "Profile";
      // ? redirect.from.name
      // : this.$auth.user().role === 2
      // ? "admin.dashboard"
      // : "profile";
      console.log(this.$auth.user());
      this.$router.push({ name: redirectTo });
    },
    errors: function (error) {
      this.alert_class = "alert-danger";
      this.hasErrors = true;
      console.log("error");
      this.shows_comment = true;
      if (error.response.data.message) {
        this.comment = error.response.data.message
      } else {
        this.comment = "Maybe a server issue. Please try again later."
      }
    },
    sendForgotPasswordEmail () {
      let emailAddress = this.emailAddressLarge;
      if (!this.emailAddressLarge) {
        emailAddress = this.emailAddressSmall;
      }
      if (emailAddress === null || emailAddress.length === 0) {
        return;
      }
      this.showsLoadingMask = true;
      axios({
        method: 'post',
        url: '/send_forgot_email',
        data: {
          emailAddress: emailAddress,
        }
      }).then((res)=>{
        this.forgot_email_error_message = "";
        this.has_error_email = false;
        this.hasButton = false;
        this.showsLoadingMask = false;
        this.modal_comment = "If this email was already registered, an email to reset password was sent to it.";
      }).catch((res)=>{
        this.forgot_email_error_message = "Email couldn't be sent. Please check your email address format.";
        this.showsLoadingMask = false;
      });
    },
    togglePasswordVisibility(elemName, passwordEye) {
      if(this.$refs[elemName].type === "password") {
        this.$refs[elemName].type = "text";
        this.$refs[elemName].$parent.$refs[passwordEye].classList.value = "fa fa-fw fa-eye-slash field-icon toggle-password";
      } else {
        this.$refs[elemName].type = "password";
        this.$refs[elemName].$parent.$refs[passwordEye].classList.value = "fa fa-fw fa-eye field-icon toggle-password";
      }
    }
  },
};
</script>
