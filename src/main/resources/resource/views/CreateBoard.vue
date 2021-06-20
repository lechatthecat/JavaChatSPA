<template>
  <div class="c-app flex-row h100p">
    <div class="container">
      <b-row class="justify-content-center">
        <b-col md="10" sm="12">
          <b-card no-body class="mx-4">
            <b-card-body class="p-4">
              <b-form>
                <h1>Create New Board</h1>
                <p class="text-muted">in <strong>{{ this.form.body.urlName }}</strong></p>
                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text>
                      <CIcon name="cil-address-book" />
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    type="text"
                    name="name"
                    v-model="form.body.name"
                    class="form-control"
                    placeholder="board title"
                    autocomplete="boardName"
                    maxLength="100"
                  />
                  <div class="error-message">{{ form.errors.name }}</div>
                </b-input-group>

                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text>
                      <CIcon name="cil-comment-square" />
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-textarea
                    type="text"
                    name="detail"
                    v-model="form.body.detail"
                    class="form-control board-detail-textarea"
                    placeholder="Brief explanation of this board"
                    autocomplete="boardDetail"
                    maxLength="700"
                  />
                  <div class="error-message">{{ form.errors.detail }}</div>
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
                <b-button variant="success" @click="register"
                  >Create new board</b-button
                >
              </b-form>
            </b-card-body>
          </b-card>
        </b-col>
      </b-row>
    </div>
    <div v-if="showsLoadingMask">
      <div class="modal-mask"></div>
      <div class="waiting-loader" style="opacity 400ms"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CreateBoard",
  data() {
    return {
      form: {
        body: {
          "name": null,
          "detail": null,
          "urlName": null,
        },
        errors: {},
      },
      showsLoadingMask: false,
    };
  },
  created () {
    this.$store.commit('set', ['footerShow', true]);
    //this.$store.commit('removeCreateBoard');
    if (!this.$store.state.categoryStringId) {
      this.form.body.urlName = "Lounge";
    } else {
      this.form.body.urlName = this.$store.state.categoryStringId;
    }
  },
  methods: {
    errors(res) {
      this.form.errors = res.errors;
      console.log(this.form.errors);
    },
    register() {
      this.showsLoadingMask = true;
      const jwtToken = "Bearer " + this.getCookie("chat_board_login_token");
      const xsrf = this.getCookie("XSRF-TOKEN");
      console.log(this.form.body);
      axios
        .post("/board_create",
          this.form.body,
          {
            "Authorization": jwtToken,
            "X-XSRF-TOKEN": xsrf
          }
        )
        .then((res) => {
          this.showsLoadingMask = false;
          // Redirect to board list
          this.$router.push({ path: "/boards/" + this.form.body.urlName + "/0" });
        })
        .catch((err) => {
          this.showsLoadingMask = false;
          this.errors(err.response.data);
        });
    },
  },
};
</script>
