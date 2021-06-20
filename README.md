## SPA Chat app (web app) that works on desktop/mobile UI

Desktop UI<br/>
<img src="https://github.com/lechatthecat/JavaChatSpa/blob/master/chat_picture.png" width="50%"><br/>
Mobile UI<br/>
<img src="https://github.com/lechatthecat/JavaChatSpa/blob/master/mobile_chat.png" width="50%">

## How to run the project

### Prerequisites
- Java 11
- Maven 3.*
- Docker 20.10.6
- Docker-compose 1.29.1

How to run:

```
$ git clone https://github.com/lechatthecat/JavaChatSpa.git
$ cd JavaChatSpa
$ docker-compose up -d --build
$ npm i
$ npm run build
$ mvn clean package
```

Now the project should be ready. Please make sure DB in Docker "Postgress" has tables for this project. 
If tables are not initialied in DB, you can create them by using /docker/sql/init.sql.

If you are not using vscode, please the following:

```
$ mvn spring-boot:run
```

If you have vscode, this project contains `.vscode` folder, so you can hit F5 to start the debug mode.

Once the project is started, go to `http://localhost:8080/` from your browser.

