<template>
  <div class="c-app flex-row align-items-center h100p">
    <div class="container">
      <b-row class="justify-content-center">
        <b-col md="9" sm="12">
          <b-card no-body class="mx-4">
            <b-card-body class="p-4">
              <b-form>
                <h1>Sign up</h1>
                <p class="text-muted">Create new account</p>
                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text>
                      <CIcon name="cil-contact" />
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    type="text"
                    v-model="form.body.usernameNonEmail"
                    class="form-control"
                    placeholder="User name"
                    autocomplete="usernameNonEmail"
                  />
                  <div class="error-message">{{ form.errors.username }}</div>
                </b-input-group>

                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text>
                      <CIcon name="cib-gmail" />
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    type="text"
                    v-model="form.body.email"
                    class="form-control"
                    placeholder="Email address"
                    autocomplete="email"
                  />
                  <div class="error-message">{{ form.errors.email }}</div>
                </b-input-group>

                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text>
                      <CIcon name="cil-lock-locked" />
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    ref="password"
                    type="password"
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
                    <b-input-group-text>
                      <CIcon name="cil-lock-locked" />
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    ref="passwordConfirm"
                    type="password"
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
                <b-input-group class="mb-4">
                  <div class="checkbox icheck">
                    <label>
                      <input
                      type="checkbox"
                      name="agreesTerm"
                      v-model="form.body.agreesTerm"
                      > I read and agree to the <a href="/privacy_policy" target="_blank" >Privacy Policy</a> and <a href="/term_of_use" target="_blank" >Term Of Use</a>
                    </label>
                    <div class="form-group">
                      <div class="error-message">{{ form.errors.agreesTerm }}</div>
                    </div>
                  </div>
                </b-input-group>

                <b-button variant="success" @click="register" block>Create your account</b-button>
              </b-form>
            </b-card-body>
            <!-- <b-card-footer class="p-4">
              <b-row>
                <b-col cols="6">
                  <b-button block class="btn btn-facebook"><span>facebook</span></b-button>
                </b-col>
                <b-col cols="6">
                  <b-button block class="btn btn-twitter" type="button"><span>twitter</span></b-button>
                </b-col>
              </b-row>
            </b-card-footer>-->
          </b-card>
        </b-col>
      </b-row>
    </div>
    <div v-if="showsLoadingMask">
      <div class="modal-mask justify-content-center align-items-center"></div>
      <div class="waiting-loader" style="opacity 400ms"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CreateUser",
  data() {
    return {
      form: {
        body: {
          email: "",
          usernameNonEmail: "",
          password: "",
          passwordConfirm: "",
          agreesTerm: false,
        },
        errors: {},
        remember: false,
        fetchUser: false,
        autoLogin: false,
        staySignedIn: false,
      },
      showsLoadingMask: false,
    };
  },
  methods: {
    errors(res) {
      this.form.errors = res.errors;
      console.log(this.form.errors);
    },
    register() {
      this.showsLoadingMask = true;
      this.$auth
        .register({
          data: this.form.body, // Axios
          remember: this.remember,
          fetchUser: this.fetchUser,
          autoLogin: this.autoLogin,
          staySignedIn: this.staySignedIn,
        })
        .then(null, (res) => {
          this.showsLoadingMask = false;
          if (res.response.status === 400) {
            this.errors(
              res.response.data // Axios
            );
          } else {
            this.showsLoadingMask = false;
            alert("Sorry, server error. Error:" + res.response.message);
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
