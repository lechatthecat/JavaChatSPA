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
                      <strong class="h4">Boards in this category</strong><br/>
                      Please note that boards are archived when the number of its responds reach 1000.
                    </CCallout>
                  </CCol>
                </CRow>
                <hr class="mt-0">
                <div v-for="board in boards" :key="board[2]" class="progress-group mb-4">
                  <div class="board-line-wrapper">
                    <div class="board-link" v-on:click="boardLinkOnClick(board)">
                      <div class="progress-group-prepend">
                        <span class="line-break-inside progress-group-text">
                          {{ board[0].replace(/(\r\n|\n|\r)/gm, " ") }}
                        </span>
                        <span class="float-right line-break-inside progress-group-text">
                          {{ board[6] }} responses
                        </span>
                      </div>
                      <div class="board-detail-line">
                        <span v-if="board[1].length > 100" class="line-break-inside progress-group-bars board-name">
                          {{ board[1].replace(/(\r\n|\n|\r)/gm, " ").substring(0, 100) }} 
                            <span class="progress-group-text">
                              ...
                            </span>
                        </span>
                        <span v-if="board[1].length < 100" class="line-break-inside progress-group-bars board-name">
                          {{ board[1].replace(/(\r\n|\n|\r)/gm, " ") }}
                        </span>
                      </div>
                    </div>
                    <span class="mb-0 boards-date pull-left">
                      Created at: {{ stringToDate(board[5]) }}
                    </span>
                    <span v-if="usernameNonEmail===board[4]" @click="checkIfDeleteBoard(board)" class="mb-0 delete-btn pull-right">
                      delete
                    </span>
                  </div>
                </div>
                <div v-if="boards !== null && boards.length === 0" class="progress-group mb-4">
                  <div class="progress-group-prepend">
                    <span class="progress-group-text">
                    </span>
                  </div>
                  <div class="progress-group-bars board-name">
                    {{ "No board is created yet." }}
                  </div>
                </div>
                <div v-if="showsLoadingMask" class="loader mt-5" style="opacity 400ms"></div>
              </CCol>
            </CRow>
            <br/>
          </CCardBody>
        </CCard>
      </CCol>
    </CRow>
    <div v-if="showModal">
      <div class="modal-mask" @click="showModal = false"></div>
      <transition name="modal">
        <div class="modal-wrapper">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="line-break-inside modal-body">
                <p>{{modalBody}}</p>
              </div>
              <div v-if="hasButton" class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="showModal = false">No</button>
                <button type="button" class="btn btn-primary" @click="deleteBoard">Yes</button>
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
  name: 'Board',
  data () {
    return {
      category_string_id: "",
      page_number: 0,
      boards: null,
      jwtToken: "",
      xsrfToken: "",
      usernameNonEmail: "",
      showModal: false,
      modalBody: "",
      hasButton: false,
      toDeleteBoard: null,
      showsLoadingMask: false,
      isOpen: false,
      onePageSize: 50,
      isScrolledToBottom: false,
      areAllLoaded: false
    }
  },
  created () {
    this.category_string_id = this.$route.params.category_string_id;
    if (this.isSignedIn()) {
      this.usernameNonEmail = this.$auth.user().usernameNonEmail;
    } 
    this.page_number = this.$route.params.page_number;
    this.$store.commit('set', ['footerShow', true]);
    this.$store.commit('set', ['categoryStringId', this.$route.params.category_string_id]);
    if (this.isSignedIn()) {
      this.$store.commit('addCreateBoard');
    }
  },
  mounted () {
    const thisScope = this;
    this.getBoards()
      .then((res)=>{
        thisScope.isOpen = false;
        thisScope.showsLoadingMask = false;
        thisScope.boards = res.data.content;
        console.log("boards:");
        console.log(thisScope.boards);
        console.log(thisScope.$auth.user());
        thisScope.jwtToken = "Bearer " + this.getCookie("chat_board_login_token");
        thisScope.xsrf = this.getCookie("XSRF-TOKEN");
      })
      .catch((error)=>{
        thisScope.isOpen = false;
        thisScope.showsLoadingMask = false;
        thisScope.modalBody= "Failed to get the boards. Error: " + error.message;
        thisScope.hasButton = false;
        thisScope.toDeleteMsg = null;
        thisScope.showModal = true;
      })

    // Add eventlistener to the scroll event
    window.addEventListener("scroll", this.onScroll);
  },
  beforeDestroy() {
    // Remove eventlistener from the scroll event
    window.removeEventListener("scroll", this.onScroll);
  },
  methods: {
    stringToDate(dateString) {
      const date = new Date(dateString);
      date.minusHours(6); // America Cicago timezone
      return date.yyyymmddHHMMss();
    },
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
    checkIfDeleteBoard(board) {
      this.modalBody = "Are you sure you will delete this board?:\n\r" + board[1] + "\n\rThis cannot be undone.";
      this.hasButton = true;
      this.toDeleteBoard = board;
      this.showModal = true;
    },
    onScroll(e) {
      const out = e.target.documentElement;
      this.isScrolledToBottom = out.scrollHeight - out.clientHeight <= out.scrollTop + 1;
      if (this.isScrolledToBottom) {
        this.page_number = Number(this.page_number) + 1;
        const thisScope = this;
        if (!this.areAllLoaded) {
          this.getBoards()
            .then((res)=>{
              if (res.data.content.length === 0) {
                this.areAllLoaded = true;
              }
              thisScope.isOpen = false;
              thisScope.showsLoadingMask = false;
              thisScope.boards = thisScope.boards.concat(res.data.content);
            })
            .catch((error)=>{
              thisScope.isOpen = false;
              thisScope.showsLoadingMask = false;
            });
        }
      }
    },
    getBoards () {
      const thisScope = this;
      this.isOpen = true;
      setTimeout(
        function() {
          if (thisScope.isOpen) {
            thisScope.showsLoadingMask = true;
          }
        }, 2000
      );
      return axios
        .get("/get_boards/" + this.category_string_id + "/" + this.page_number, {
          headers: {
            "Authorization": this.jwtToken,
            "X-XSRF-TOKEN": this.xsrfToken
          }
        })
    },
    deleteBoard(){
      const thisScope = this;
      axios({
        method: 'post',
        headers: {
            "Authorization": this.jwtToken,
            "X-XSRF-TOKEN": this.xsrfToken
        },
        url: '/delete_board',
        data: {
          tableUrlName: this.toDeleteBoard[2],
        }
      }).then(function (response) {
        const index = thisScope.boards.indexOf(thisScope.toDeleteBoard);
        if (index > -1) {
          thisScope.boards.splice(index, 1);
        }
        thisScope.modalBody = "Board was successfully deleted.";
        thisScope.hasButton = false;
        thisScope.toDeleteMsg = null;
        thisScope.showModal = true;
      })
      .catch(function (error) {
        thisScope.modalBody= "Failed to delete the board.\n\rError: " + error.message;
        thisScope.hasButton = false;
        thisScope.toDeleteMsg = null;
        thisScope.showModal = true;
      });
    },
    boardLinkOnClick (board) {
      const toLink = '/chat/' + board[3] + '/' + board[2] + '/0';
      console.log(toLink);
      this.$router.push({ path: toLink });
    }
  }
}
</script>
