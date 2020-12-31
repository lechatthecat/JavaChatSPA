<template>
  <div class="c-app flex-row h100p">
    <div class="container">
      <b-row class="justify-content-center">
        <b-col md="10" sm="12">
          <b-card no-body class="mx-4">
            <b-card-body class="p-4">
              <b-form>
                <h1>Create New Board</h1>
                <p class="text-muted">in <strong>{{ this.form.body.url_name }}</strong></p>
                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text>
                      <i
                        class="fa fa-address-card board-create-icon"
                        aria-hidden="true"
                      ></i>
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-input
                    type="text"
                    name="name"
                    v-model="form.body.name"
                    class="form-control"
                    placeholder="board title"
                    autocomplete="boardName"
                  />
                  <div class="error-message">{{ form.errors.name }}</div>
                </b-input-group>

                <b-input-group class="mb-3">
                  <b-input-group-prepend>
                    <b-input-group-text>
                      <i
                        class="fa fa-commenting-o board-create-icon"
                        aria-hidden="true"
                      ></i>
                    </b-input-group-text>
                  </b-input-group-prepend>
                  <b-form-textarea
                    type="text"
                    name="detail"
                    v-model="form.body.detail"
                    class="form-control board-detail-textarea"
                    placeholder="Brief explanation of this board"
                    autocomplete="boardDetail"
                  />
                  <div class="error-message">{{ form.errors.detail }}</div>
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
    <div v-if="showsloadingMark">
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
          "url_name": null,
        },
        errors: {},
      },
      showsloadingMark: false,
    };
  },
  created () {
    this.$store.commit('set', ['footerShow', true]);
    this.$store.commit('removeCreateBoard');
    if (!this.$store.state.categoryStringId) {
      this.form.body.url_name = "free";
    } else {
      this.form.body.url_name = this.$store.state.categoryStringId;
    }
  },
  methods: {
    errors(res) {
      this.form.errors = res.errors;
      console.log(this.form.errors);
    },
    register() {
      this.showsloadingMark = true;
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
          this.showsloadingMark = false;
          // Redirect to board list
          this.$router.push({ path: "/boards/" + this.form.body.url_name + "/0" });
        })
        .catch((err) => {
          this.showsloadingMark = false;
          this.errors(err.response.data);
        });
    },
  },
};
</script>
