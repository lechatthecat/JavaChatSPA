<template>
  <div class="c-app flex-row align-items-center">
    <div v-if="showsLoadingMask">
      <div class="modal-mask justify-content-center align-items-center"></div>
      <div class="waiting-loader" style="opacity 400ms"></div>
    </div>
    <div class="container">
      <b-row class="justify-content-center">
        <b-col md="12" sm="12">
          <b-card no-body class="mx-4">
            <b-card-body class="p-4">
              <b-form>
                <h1>Change Password</h1>

                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text
                      ><i class="icon-lock"></i
                    ></b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    ref="password"
                    type="text"
                    v-model="form.body.password"
                    class="form-control"
                    placeholder="Password"
                    autocomplete="new-password"
                  />
                  <span 
                    ref="passwordEye"
                    class="fa fa-fw fa-eye field-icon toggle-password"
                    @click="togglePasswordVisibility('password', 'passwordEye')">
                  </span>
                  <div class="error-message">{{ form.errors.password }}</div>
                </b-input-group>

                <b-input-group class="mb-4">
                  <b-input-group-prepend>
                    <b-input-group-text
                      ><i class="icon-lock"></i
                    ></b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    ref="passwordConfirm"
                    type="text"
                    v-model="form.body.passwordConfirm"
                    class="form-control"
                    placeholder="Password for confimation"
                    autocomplete="new-password"
                  />
                  <span 
                    ref="passwordConfirmEye"
                    class="fa fa-fw fa-eye field-icon toggle-password"
                    @click="togglePasswordVisibility('passwordConfirm', 'passwordConfirmEye')">
                  </span>
                  <div class="error-message">{{ form.errors.passwordConfirm }}</div>
                </b-input-group>

                <b-button variant="success" @click="register" block>Change Password</b-button>
              </b-form>
            </b-card-body>
          </b-card>
        </b-col>
      </b-row>
    </div>
    <div v-if="showModal">
      <div class="modal-mask" @click="showModal = false"></div>
      <transition name="modal">
        <div class="forgot-email-modal-wrapper">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="line-break-inside modal-body">
                <p>{{modal_comment}}</p>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
export default {
  name: "ChangePassword",
  data() {
    return {
      form: {
        body: {
            userToken: "",
            password: "",
            passwordConfirm: "",
        },
        errors: {},
      },
      showModal: false,
      modal_comment: "",
      showsLoadingMask: false,
    };
  },
  created () {
    this.form.body.userToken = this.$route.params.user_token;
  },
  methods: {
    errors(res) {
      this.form.errors = res.errors;
      console.log(this.form.errors);
    },
    register() {
      this.showsLoadingMask = true;
      axios({
          method: 'post',
          url: '/change_password',
          data: {
              userToken: this.form.body.userToken,
              password: this.form.body.password,
              passwordConfirm: this.form.body.passwordConfirm,
          }
      }).then((response) => {
        if (response.data.successStatus == 1) {
          this.$router.push({ path: "/login?status=password_changed" });
        } else if (response.data.successStatus == 3) {
          this.showsLoadingMask = false;
          this.comment = "This token is already expired."
        } else {
          this.showsLoadingMask = false;
          this.comment = "Failed to Validate the token. This token is invalid."
        }
        console.log(response);
      })
      .catch((error) => {
        this.showsLoadingMask = false;
        if (error.response.status === 400) {
          if (error.response.data.successStatus === 4) {
            this.errors(
              error.response.data // Axios
            );
          } else {
            this.showModal = true;
            this.modal_comment = error.response.data.message;
          }
        } else {
          this.showsLoadingMask = false;
          this.showModal = true;
          this.modal_comment = "Sorry, server error. Error:" + error.response.message;
        }
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
