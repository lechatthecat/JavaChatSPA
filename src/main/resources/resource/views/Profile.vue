<template>
  <div>
    <CRow>
      <CCol md="12">
        <CCard>
          <CCardBody>
            <CRow>
              <CCol sm="12" lg="12">
                <CRow>
                  <CCol sm="12">
                    <CCallout color="info">
                      <strong class="h4">User profile</strong>
                    </CCallout>
                  </CCol>
                </CRow>
                <hr class="mt-0">
                <p class="text-muted">This page is invisible from other users.</p>
                <div class="progress-group mb-4">
                  <div class="progress-group-prepend profile-title">
                    <span class="progress-group-text">
                      User name
                    </span>
                  </div>
                  <div class="progress-group-bars board-name">
                    {{ user.usernameNonEmail }}
                  </div>
                </div>
                <div class="progress-group mb-4">
                  <div class="progress-group-prepend profile-title">
                    <span class="progress-group-text">
                      Email address
                    </span>
                  </div>
                  <div class="progress-group-bars board-name">
                    {{ user.email }}
                  </div>
                </div>
                <div class="progress-group mb-4">
                  <div class="progress-group-prepend profile-title">
                    <span class="progress-group-text">
                      Account created at
                    </span>
                  </div>
                  <div class="progress-group-bars board-name">
                    {{ user.created }}
                  </div>
                </div>
              </CCol>
            </CRow>
            <br/>
          </CCardBody>
        </CCard>
      </CCol>
    </CRow>
    <CButton @click="collapse = !collapse" color="primary" class="mb-2 pull-right">
      Danger zone
    </CButton>
    <div class="clearfix"></div>
    <CRow>
      <CCol md="12" sm="12" lg="12">
        <CCollapse :show="collapse" :duration="400">
          <CCard body-wrapper>
            <CCardText class="">
              <span class="btn btn-info" @click="showModalPasswordChange=true">Change your password</span>
              <span class="btn btn-danger" @click="showCloseAccountModal">Close your account</span>
            </CCardText>
          </CCard>
        </CCollapse>
      </CCol>
    </CRow>
    <div v-if="showModal">
      <div class="modal-mask" @click="showModal = false"></div>
      <transition name="modal">
        <div class="forgot-email-modal-wrapper">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="line-break-inside modal-body">
                <p>{{comment}}</p>
              </div>
              <div v-if="hasButtons" class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="showModal = false">No</button>
                <button type="button" class="btn btn-primary" @click="closeAccount">Yes</button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
    <div v-if="showModalPasswordChange">
      <div class="modal-mask" @click="showModalPasswordChange = false"></div>
      <transition name="modal">
        <div class="forgot-email-modal-wrapper">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
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
                            type="password"
                            v-model="form.body.currentPassword"
                            class="form-control"
                            placeholder="Current Password"
                            autocomplete="password"
                          />
                          <div class="error-message">{{ form.errors.currentPassword }}</div>
                        </b-input-group>

                        <b-input-group class="mb-3">
                          <b-input-group-prepend>
                            <b-input-group-text
                              ><i class="icon-lock"></i
                            ></b-input-group-text>
                          </b-input-group-prepend>
                          <b-form-input
                            type="password"
                            v-model="form.body.password"
                            class="form-control"
                            placeholder="New Password"
                          />
                          <div class="error-message">{{ form.errors.password }}</div>
                        </b-input-group>

                        <b-input-group class="mb-4">
                          <b-input-group-prepend>
                            <b-input-group-text
                              ><i class="icon-lock"></i
                            ></b-input-group-text>
                          </b-input-group-prepend>
                          <b-form-input
                            type="password"
                            v-model="form.body.passwordConfirm"
                            class="form-control"
                            placeholder="Password for confimation"
                          />
                          <div class="error-message">{{ form.errors.passwordConfirm }}</div>
                        </b-input-group>

                        <b-button variant="success" @click="changePassword" block>Change Password</b-button>
                      </b-form>
                    </b-card-body>
                  </b-card>
                </b-col>
              </b-row>
            </div>
          </div>
        </div>
      </transition>
    </div>
    <div v-if="showsloadingMark">
      <div class="modal-mask-transparent justify-content-center align-items-center" style="z-index:1050;"></div>
      <div class="waiting-loader" style="opacity 400ms"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data () {
    return {
      form: {
        body: {
            currentPassword: "",
            password: "",
            passwordConfirm: "",
        },
        errors: {},
      },
      user: {},
      jwtToken: "",
      xsrf: "",
      collapse: false,
      showModal: false,
      showsloadingMark: false,
      showModalPasswordChange: false,
      hasButtons:true,
      comment: "You are sure that you will close your account?"
    }
  },
  async created () {
    this.jwtToken = "Bearer " + this.getCookie("chat_board_login_token");
    this.xsrf = this.getCookie("XSRF-TOKEN");
    const res = await this.getUserInfo();
    console.log(res);
    this.user = res.data.data; 
    console.log(this.user);
    this.$store.commit('removeCreateBoard');
    this.$store.commit('set', ['categoryStringId', null]);
    this.$store.commit('set', ['footerShow', true]);
  },
  methods: {
    color (value) {
      let $color
      if (value <= 25) {
        $color = 'info'
      } else if (value > 25 && value <= 50) {
        $color = 'success'
      } else if (value > 50 && value <= 75) {
        $color = 'warning'
      } else if (value > 75 && value <= 100) {
        $color = 'danger'
      }
      return $color
    },
    getUserInfo() {
      return axios
        .get("/user", {
          headers: {
            "Authorization": this.jwtToken,
            "X-XSRF-TOKEN": this.xsrfToken
          }
        });
    },
    showCloseAccountModal(){
      this.hasButtons = true;
      this.comment = "You are sure that you will close your account?";
      this.showModal = true;
    },
    closeAccount() {
      this.showsloadingMark = true;
      axios
        .post("/close_account", {
          headers: {
            "Authorization": this.jwtToken,
            "X-XSRF-TOKEN": this.xsrfToken
          }
        }).then((res)=>{
          this.$router.push({ name: "Login" });
        }).catch((err)=>{
          this.showsloadingMark = false;
          this.comment = "Sorry, error occured: " + err.message;
        });
    },
    changePassword() {
      this.hasButtons = false;
      this.showsloadingMark = true;
      axios({
          method: 'post',
          url: '/change_password_profile',
          data: {
              currentPassword: this.form.body.currentPassword,
              password: this.form.body.password,
              passwordConfirm: this.form.body.passwordConfirm,
          }
      }).then((response) => {
        this.showModalPasswordChange=false;
        this.hasButtons = false;
        this.comment = "Password was successfully changed.";
        this.showModal = true;
        for (let key of Object.keys(this.form.body)) {
          this.form.body[key] = "";
        }
        this.form.errors = {};
      })
      .catch((error) => {
        this.showsloadingMark = false;
        if (error.response.status === 400) {
          this.errors(
            error.response.data // Axios
          );
        } else {
          this.showModalPasswordChange=false;
          this.hasButtons = false;
          this.comment = "Server error. Please try again.";
          this.showModal = true;
        }
      });
    },
    errors(res) {
      this.form.errors = res.errors;
      console.log(this.form.errors);
    },
  }
}
</script>
