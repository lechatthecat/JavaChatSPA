package com.javachat.controller.user;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javachat.jwt.payload.response.MessageResponse;
import com.javachat.model.User;
import com.javachat.service.UserDetailsServiceImpl;
import com.javachat.service.UserService;
import com.javachat.util.CookieUtil;
import com.javachat.util.JwtUtils;
import com.javachat.validator.UserValidator;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserValidator userValidator;
    @Autowired
    MessageSource messageSource;
	@Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    CookieUtil cookieutil;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping(path = "/create_user", consumes = "application/json")
    public ResponseEntity<?> createUser(HttpServletRequest request, @RequestBody User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        final JSONObject resultJson = new JSONObject();
        if(!bindingResult.hasErrors()){
            User deletedUser = userService.findByEmailIncludesDeleted(user.getEmail());
            if (deletedUser == null) {
                userService.createUser(user);
            } else {
                deletedUser.setUsernameNonEmail(user.getUsernameNonEmail());;
                deletedUser.setPassword(user.getPassword());
                userService.reCreateUser(deletedUser);
            }
            resultJson.put("isSuccess", true);
            resultJson.put("message", "Success. User created. You can login with the email now.");
            return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
        } else {
            final List<FieldError> errors = bindingResult.getFieldErrors();
            final HashMap<String, String> errorList = new HashMap<>();
            for (FieldError error : errors ) {
                final String message = messageSource.getMessage(error, null);
                errorList.put(error.getField(), message);
            }
            resultJson.put("errors", errorList);
            resultJson.put("isSuccess", false);
            return ResponseEntity.badRequest().body(resultJson.toMap());
        }
    }

    @GetMapping(path = "/user")
    public ResponseEntity<?> getUser(HttpServletRequest request, HttpServletResponse response) {
        final JSONObject resultJson = new JSONObject();
        final String jwt = jwtUtils.parseJwt(request);
        try {
            UserDetails userDetails;
            final boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
            if (jwt != null && isJwtValid) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                userDetails = userDetailsService.loadUserByUsername(username);
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
            }
            final User user = (User) userDetails;
            final HashMap<String, String> userInfo = new HashMap<String, String>();
            final String role = user.getRole().getName();
            userInfo.put("jwt", jwt);
            userInfo.put("usernameNonEmail", user.getUsernameNonEmail());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", role);
            userInfo.put("created", user.getUSStringCreated());
            userInfo.put("userMainImage", user.getUserMainImage().getPath());
            resultJson.put("data", userInfo);
            return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
        } catch (UsernameNotFoundException e){
            // Logout
            CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
            final Cookie cookie = cookieutil.deleteCookie("chat_board_login_token", request);
            response.addCookie(cookie);
            cookieClearingLogoutHandler.logout(request, response, null);
            securityContextLogoutHandler.logout(request, response, null);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
        }
    }


    @PostMapping(path = "/close_account", consumes = "application/json")
    public ResponseEntity<?> closeUser(HttpServletRequest request, HttpServletResponse response) {
        final String jwt = jwtUtils.parseJwt(request);
        try {
            UserDetails userDetails;
            final boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
            if (jwt != null && isJwtValid) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                userDetails = userDetailsService.loadUserByUsername(username);
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
            }
            // Delete user
            final User user = (User) userDetails;
            boolean isDeleted = userService.deleteUser(user);
            // If successfully deleted, proceed to logout.
            if (isDeleted) {
                // Logout
                CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
                SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
                final Cookie cookie = cookieutil.deleteCookie("chat_board_login_token", request);
                response.addCookie(cookie);
                cookieClearingLogoutHandler.logout(request, response, null);
                securityContextLogoutHandler.logout(request, response, null);
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
            }
        } catch (UsernameNotFoundException e){
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
        }
    }
}
