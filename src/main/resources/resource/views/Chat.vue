<template>
  <div>
    <section id="board-content" class="content board-content">
      <div class="box-header with-border">
        <h3 v-if="messageReceived[0].isFirst" ref="board_name" class="box-title line-break-inside">
          {{ board.name }}
        </h3>
      </div>
      <div v-if="showsLoadingMark" class="loader" style="opacity 400ms"></div>
      <div
        class="chatbox box_content box box-warning direct-chat direct-chat-warning"
      >
        <div id="warning" ref="warning" class="disconnected" style="visibility: hidden;">
          <span class="text-danger">You are not connected to the server.</span>
        </div>
        <div class="box-body msg-box">
          <div
            :id="'messageArea-'+index"
            class="direct-chat-messages board-height board-margin"
            v-for="(msg, index) in messageReceived" :key="msg.id"
          >
            <CAlert show color="secondary">
              <div v-bind:id="'boardresponse-'+msg.id" v-bind:msg-id="msg.id" class="chat-msg-text alert-heading">{{msg.sender}}</div>
              <p>
                <pre class="pre-line-break chat-msg-text">{{msg.response}}</pre>
              </p>
              <hr>
              <p v-if="!msg.isFirst && usernameNonEmail===msg.sender" @click="checkIfDeleteComment(msg)" class="mb-0 delete-btn msg-time pull-left">
                delete
              </p>
              <p class="mb-0 msg-time pull-right">
                {{msg.usstringCreated}}
              </p>
            </CAlert>
            <span v-bind:id="'jump-to-'+msg.id"></span> 
          </div>
        </div>
      </div>
    </section>
    <b-row v-if="isSignedIn()" class="chat-text-area-wrapper no-gutters">
      <textarea v-model="textareaValue" @keydown="onKeyDown" @keyup="onKeyUp" id="chat-input-box"/>
      <button @click="send" id="submit-button">
        <i class="chat-submit-button fa fa-arrow-circle-up" aria-hidden="true"></i>
      </button>
    </b-row>
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
                <button type="button" class="btn btn-primary" @click="deleteComment">Yes</button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>
<script>
import * as Stomp from "@stomp/stompjs";
export default {
  name: "Chat",
  data () {
    return {
      stompClient: null,
      messageReceived: [{isFirst:false,response:"",sender:null}],
      username: "",
      usernameNonEmail: "",
      board: {},
      board_id: null,
      page_number: 0,
      isConnected: false,
      isScrolledToBottom: false,
      isScrolledToTop: false,
      isFirstScrolled: false,
      jwtToken: "",
      xsrfToken: "",
      textareaMaxCharNum: 700,
      textareaValue: "",
      ctrlDown: false,
      showModal: false,
      modalBody: "",
      hasButton: false,
      toDeleteMsg: null,
      showsLoadingMark: false,
      isOpen: false,
      onePageSize: 100,
      messageLoaded: false,
    }
  },
  async created() {
    this.$store.commit('set', ['footerShow', false]);
    if (this.isSignedIn()) {
      this.$store.commit('addCreateBoard');
      this.usernameNonEmail = this.$auth.user().usernameNonEmail;
    }
    this.board_id = this.$route.params.board_string_id;
    this.page_number = this.$route.params.page_number;
    this.jwtToken = "Bearer " + this.getCookie("chat_board_login_token");
    this.xsrf = this.getCookie("XSRF-TOKEN");

    // Get board information
    const res_board = await this.getTableInfo(this.board_id);
    this.board = res_board.data;
    console.log(this.board);
    // Create breadcrumb which should be already rendered as outer component 
    this.createBreadCrumb(this.board);

    // Get responses data
    this.getPastMessages(this.board_id, this.page_number)
      .then((res)=>{
        this.messageReceived = res.data;
        this.$nextTick(() => {
          this.messageLoaded = true;
          document.getElementById("board-content").style.visibility = "visible";
          this.scrollToBottom();
        });
        this.isOpen = false;
        this.showsLoadingMark = false;
        console.log("past messages:");
        console.log(res.data);
      }).catch(()=>{
        this.isOpen = false;
        this.showsLoadingMark = false;
      });
  },
  mounted() {
    // Add eventlistener to the scroll event
    window.addEventListener("scroll", this.onScroll);
    // Connect to websocket
    if (!this.isConnected) {
      this.connect();
    }
  },
  beforeDestroy() {
    this.$store.commit('set', ['footerShow', true]);
    window.removeEventListener("scroll", this.onScroll);
    document.getElementById("board-content").style.visibility = "hidden";
    this.deleteElement("chat-breadcrumb");
    if (this.isConnected) {
      this.stompClient.unsubscribe();
      this.stompClient.deactivate();
    }
  },
  updated() {
    if (this.isScrolledToBottom) {
      this.scrollToBottom();
    } else if (this.isScrolledToTop) {
      this.scrollTo("messageArea-"+this.onePageSize);
    }
  },
  methods: {
    shouldAdBeDisplayed(index) {
      return (index !==0 && index%this.adFrequency===0);
    },
    addGoogleAd(msg) {
      this.$nextTick(() => {
        // Google ad
        (adsbygoogle = window.adsbygoogle || []).push({});
        msg.isAdShowed = true;
      });
    },
    checkIfDeleteComment(msg) {
      this.modalBody = "Are you sure you will delete this message?:\n\r" + msg.response + "\n\rThis cannot be undone.";
      this.hasButton = true;
      this.toDeleteMsg = msg;
      this.showModal = true;
    },
    deleteComment(){
      axios({
        method: 'post',
        headers: {
            "Authorization": this.jwtToken,
            "X-XSRF-TOKEN": this.xsrfToken
        },
        url: '/delete_board_response',
        data: {
          id: this.toDeleteMsg.id,
        }
      }).then((response) => {
        const index = this.messageReceived.indexOf(this.toDeleteMsg);
        if (index > -1) {
          this.messageReceived.splice(index, 1);
        }
        this.modalBody = "Message was successfully deleted.";
        this.hasButton = false;
        this.toDeleteMsg = null;
        this.showModal = true;
      })
      .catch((error) => {
        this.modalBody= "Failed to delete the message.\n\rError: " + error.message;
        this.hasButton = false;
        this.toDeleteMsg = null;
        this.showModal = true;
      });
    },
    onKeyDown(e){
      if (this.textareaValue.replace(/(\r\n|\n|\r)/g," ").length >= this.textareaMaxCharNum)  {
        if (e.keyCode == 17 || e.keyCode == 91) this.ctrlDown = true;
        if ((this.ctrlDown && e.keyCode == 67) 
          || (this.ctrlDown && e.keyCode == 65)
          || (this.ctrlDown && e.keyCode == 88)
        ) return;
        if ((this.ctrlDown && e.keyCode == 86)
          || e.keyCode == 13 || e.keyCode == 32 
          || (e.keyCode >= 48 && e.keyCode <= 90)) {
          e.preventDefault();
          this.modalBody = "Sorry, you cannot write more than 700 letters.";
          this.hasButton = false;
          this.showModal = true;
          return;
        }
      }
    },
    onKeyUp(e){
      if (e.keyCode == 17 || e.keyCode == 91) this.ctrlDown = false;
    },
    createBreadCrumb(board) {
      // Add chat title to the breakcrumbs
      const hyphenWrapperNode = document.createElement("span");
      const hyphenNode = document.createTextNode("-");
      hyphenWrapperNode.appendChild(hyphenNode);
      hyphenWrapperNode.classList.add("mr-1");
      hyphenWrapperNode.classList.add("ml-1");
      hyphenWrapperNode.classList.add("chat-breadcrumb");
      const titleWrapperNode = document.createElement("span");
      const titleNode = document.createTextNode(board.name);
      titleWrapperNode.classList.add("chat-breadcrumb");
      titleWrapperNode.appendChild(titleNode);
      document.getElementsByClassName("breadcrumb-item")[0].appendChild(hyphenWrapperNode);
      document.getElementsByClassName("breadcrumb-item")[0].appendChild(titleWrapperNode);
    },
    scrollToBottom() {
      window.scrollTo(0, document.body.scrollHeight || document.documentElement.scrollHeight);
    },
    deleteElement(className) {
      if (document.getElementsByClassName(className).length > 0) {
        document.getElementsByClassName(className).forEach(element => {
          console.log(element);
          element.remove();
        });
        this.deleteElement(className);
      }
    },
    scrollTo (elemId) {
      // 104px is for header height
      window.scroll(0,Number(this.findPos(document.getElementById(elemId)))-104);
    },
    findPos(obj) {
      let curtop = 0;
      if (obj.offsetParent) {
          do {
              curtop += obj.offsetTop;
          } while (obj = obj.offsetParent);
      return [curtop];
      }
    },
    onScroll(e) {
      const out = e.target.documentElement;
      this.isScrolledToTop = (out.scrollTop === 0);
      this.isScrolledToBottom = out.scrollHeight - out.clientHeight <= out.scrollTop + 1;
      if (!this.messageReceived[0].isFirst && this.isConnected && this.isScrolledToTop) {
        this.page_number = Number(this.page_number) + 1;
        this.getPastMessages(this.board_id, this.page_number)
          .then((res)=>{
            this.isOpen = false;
            this.showsLoadingMark = false;
            this.messageReceived = res.data.concat(this.messageReceived);
            if (this.messageReceived[0].isFirst) {
              this.$refs.board_name.style.visibility = "visible";
              this.$el.querySelector("#first_ad").style.visibility = "visible";
            }
          })
          .catch((error)=>{
            this.isOpen = false;
            this.showsLoadingMark = false;
            if (this.messageReceived[0].isFirst) {
              this.$refs.board_name.style.visibility = "visible";
              this.$el.querySelector("#first_ad").style.visibility = "visible";
            }
            // this.modalBody = "Sorry, couldn't open this board.";
            // this.hasButton = false;
            // this.showModal = true;
          });
      }
    },
    getTableInfo (table_url_name) {
      return axios
        .get("/get_board_info/" + table_url_name, {
          headers: {
            "Authorization": this.jwtToken,
            "X-XSRF-TOKEN": this.xsrfToken
          }
        }).catch(function(error){
          this.modalBody = "Sorry, couldn't open this board.<br/>Maybe this board is already deleted.";
          this.hasButton = false;
          this.showModal = true;
        });
    },
    getPastMessages: function (table_url_name, page_number) {
      this.isOpen = true;
      setTimeout(
        function() {
          if (this.isOpen) {
            this.showsLoadingMark = true;
          }
        }, 2000
      );
      return axios
        .get("/get_board_responses/" + table_url_name + "/" + page_number, {
          headers: {
            "Authorization": this.jwtToken,
            "X-XSRF-TOKEN": this.xsrfToken
          }
        });
    },
    connect: function (event) {
      if (!this.isConnected) {
        this.isConnected = true;
        
        // https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html#create-a-stomp-client
        // https://stomp-js.github.io/guide/stompjs/rx-stomp/ng2-stompjs/using-stomp-with-sockjs.html#example-with-stompjs
        const wsUri = (window.location.protocol === 'https:' ? 'wss://' : 'ws://') +
          window.location.host + '/ws/websocket';

        console.log(wsUri);
        
        const jwtToken = this.getCookie("chat_board_login_token");
        const xsrf = this.getCookie("XSRF-TOKEN");
        const connectionOption = {
          brokerURL: wsUri,
          connectHeaders: {
            "X-XSRF-TOKEN": xsrf
          },
          debug: function (str) {
            console.log(str);
          },
          reconnectDelay: 10000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
        };
        if (jwtToken) {
          connectionOption.connectHeaders["Authorization"] = "Bearer " + jwtToken;
        }
        this.stompClient = new Stomp.Client(connectionOption);
        // Fallback code
        // if (typeof WebSocket !== 'function') {
        //   // For SockJS you need to set a factory that creates a new SockJS instance
        //   // to be used for each (re)connect
        //   this.stompClient.webSocketFactory = function () {
        //     // Note that the URL is different from the WebSocket URL
        //     const sockjsUrl = window.location.protocol + '//' + window.location.host;
        //     console.log(sockjsUrl);
        //     return new SockJS(sockjsUrl + '/ws');
        //   };
        // }
        this.stompClient.onConnect = this.onConnected;
        this.stompClient.onStompError = this.onError;
        this.stompClient.activate();
      }
    },
    onConnected: function (res) {
      this.$el.querySelector("#warning").style.display = "none";
      console.log("Connected.");
      this.username = res.headers["user-name"];
      // Subscribe to the specific and public topic
      this.stompClient.subscribe("/board/public/" + this.board_id, this.onMessageReceived);
      const jwtToken = this.getCookie("chat_board_login_token");
      const connectionOption = {
        destination: "/app/chat.addUser/" + this.board_id,
        body: JSON.stringify({ sender: this.username, type: "JOIN", board_id: this.board_id }),
        headers: { sender: this.username, type: "JOIN", board_id: this.board_id }
      };
      if (jwtToken) {
        connectionOption.headers["Authorization"] = "Bearer " + jwtToken;
      }
      // Tell your username to the server
      this.stompClient.publish(connectionOption);
    },
    onError: function (error) {
      this.isConnected = false;
      this.$el.querySelector("#warning").textContent =
        "Could not connect to WebSocket server. Please refresh this page to try again!";
      this.$el.querySelector("#warning").style.color = "red";
    },
    onClose: function (event) {
      this.modalBody = "Connection closed. Please refresh the page.";
      this.hasButton = false;
      this.showModal = true;
    },
    onMessageReceived: function (payload) {
      console.log(payload);
      console.log("Message received.");
      let message = JSON.parse(payload.body);
      console.log(message);
      if (message.msg_type === "JOIN") {
        // Do something when user joins
      } else if (message.msg_type === "LEAVE") {
        // Do something when user leaves
      } else {
        this.messageReceived.push(message);
      }
    },
    send: function (event) {
      if (event) event.preventDefault();
      const messageContent = this.textareaValue.trim();
      if (messageContent && this.stompClient) {
        const jwtToken = "Bearer " + this.getCookie("chat_board_login_token");
        const xsrf = this.getCookie("XSRF-TOKEN");
        const boardResponse = {
          sender: this.username,
          response: this.textareaValue,
          board_id: this.board_id,
          type: "CHAT",
        };
        const connectionOption = {
          destination: "/app/chat.sendMessage/" + this.board_id,
          body:JSON.stringify(boardResponse),
          headers:{
            "X-XSRF-TOKEN": xsrf,
            "Authorization": jwtToken
          },
        };
        this.stompClient.publish(connectionOption);
        this.textareaValue = "";
      }
      event.preventDefault();
    },
  },
};
</script>