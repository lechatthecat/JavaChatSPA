package com.javachat.controller.spa_auth;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javachat.jwt.payload.request.LoginRequest;
import com.javachat.jwt.payload.response.MessageResponse;
import com.javachat.model.User;
import com.javachat.model.payload.PasswordResetRequest;
import com.javachat.model.payload.ProfilePasswordResetRequest;
import com.javachat.service.SecurityService;
import com.javachat.service.UserService;
import com.javachat.util.CookieUtil;
import com.javachat.validator.MyValidateUtil;
import com.javachat.validator.PasswordResetRequestValidator;
import com.javachat.validator.ProfilePasswordResetRequestValidator;
import com.javachat.validator.UserValidator;
import com.javachat.util.JwtUtils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.javachat.service.UserDetailsServiceImpl;
import com.javachat.service.UserForgotemailService;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class AuthApiController {
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	UserService userService;
	@Autowired
	SecurityService securityService;
	@Autowired
	UserValidator userValidator;
	@Autowired
	CookieUtil cookieutil;
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	@Autowired
	UserForgotemailService userForgotemailService;
	@Autowired
	PasswordResetRequestValidator passwordResetRequestValidator;
	@Autowired
	ProfilePasswordResetRequestValidator profilePasswordResetRequestValidator;
	@Autowired
    MessageSource messageSource;
	Logger logger = LoggerFactory.getLogger(AuthApiController.class);

	@PostMapping(path = "/login", consumes = "application/json")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response, HttpServletRequest request) {
		try {
			final User user = userService.findByEmailIncludesUnverified(loginRequest.getEmail());
			final int checkBeforeLoginResult = userService.checkBeforeLogin(user);
			switch (checkBeforeLoginResult) {
				case 2:
					return ResponseEntity.badRequest().body(new MessageResponse("Please verify your email address before login."));
				case 3:
					return ResponseEntity.badRequest().body(new MessageResponse("Sorry, you are banned because you violated term of use."));
				case 4:
					return ResponseEntity.badRequest().body(new MessageResponse("Sorry, your account was locked to protect your account. Please reset your password by using \"Forgot password\""));
			}
			// If user is found and email is verified, try to authorize the account
			final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			// if login succeeds, reset the nomber of failed login attempts.
			user.setFailTimes(0);
			userService.save(user);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			final String jwt = jwtUtils.generateJwtToken(authentication);

			final User userDetails = (User) authentication.getPrincipal();
			final String role = userDetails.getRole().getName();

			final Cookie cookie = cookieutil.makeCookie("chat_board_login_token", jwt, request);
			response.addCookie(cookie);
			final HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer "+jwt);
			final JSONObject resultJson = new JSONObject();
			resultJson.put("jwt", jwt);
			resultJson.put("email", userDetails.getEmail());
			resultJson.put("usernameNonEmail", userDetails.getUsernameNonEmail());
			resultJson.put("role", role);
			return new ResponseEntity<>(resultJson.toMap(), headers, HttpStatus.OK);
		} catch (AuthenticationException authEx) {
			// When auth fails, count up the times of failed login attempt.
			final User user = userService.findByEmailIncludesUnverified(loginRequest.getEmail());
			if (user != null && user.getId() > 0) {
				user.setFailTimes(user.getFailTimes() + 1);
				userService.save(user);
			}
			return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Please note you must use \"Email address\" not username. Your account will be locked after you fail to login 5 times in a row."));
	 	}
	}

	@PostMapping(path = "/logout")
	public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MessageResponse("Sorry, error occured"));
		}
	}

	@GetMapping(path = "/refresh")
	public ResponseEntity<?> refreshUser(HttpServletResponse response, HttpServletRequest request) {
		final String jwt = jwtUtils.parseJwt(request);
		try {
			UserDetails userDetails;
			final boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
			if (jwt != null && isJwtValid) {
				final String username = jwtUtils.getUsernameFromToken(jwt);
				userDetails = userDetailsService.loadUserByUsername(username);
			} else {
				final Cookie cookie = cookieutil.deleteCookie("chat_board_login_token", request);
				response.addCookie(cookie);
				return ResponseEntity.badRequest().body(new MessageResponse("Error: token is empty or invalid."));
			}
			final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			final User user = (User) authentication.getPrincipal();
			final String role = user.getRole().getName();
			final Cookie cookie = cookieutil.makeCookie("chat_board_login_token", jwt, request);
			response.addCookie(cookie);
			final HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer "+jwt);
			final JSONObject resultJson = new JSONObject();
			final HashMap<String, String> userInfo = new HashMap<String, String>();
			userInfo.put("jwt", jwt);
			userInfo.put("usernameNonEmail", user.getUsernameNonEmail());
			userInfo.put("username", user.getUsername());
			userInfo.put("email", user.getEmail());
			userInfo.put("role", role);
			userInfo.put("userMainImage", user.getUserMainImage().getPath());
			resultJson.put("data", userInfo);
			return new ResponseEntity<>(resultJson.toMap(), headers, HttpStatus.OK);
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
			return ResponseEntity.badRequest().body(new MessageResponse("Error: token is empty or invalid."));
		}
	}

	@PostMapping(path = "/send_forgot_email")
    public ResponseEntity<?> resetPassword(
			final @RequestBody HashMap<String, String> userEmail,
            HttpServletRequest request
		) {
		try {
			final String emailAddress = userEmail.get("emailAddress");
			final User user = userService.findByEmailIncludesUnverified(emailAddress);
			boolean isValidEmailAddress = MyValidateUtil.isValidEmailAddress(emailAddress);
			if (!isValidEmailAddress) {
				return ResponseEntity.badRequest().body(new MessageResponse("Email format is not correct."));
			}
			if (user != null) {
				userService.sendForgotPasswordLink(user);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.OK);
			}
		} catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MessageResponse("Verification failed. Please make sure that the URL is correct."));
		}
	}

	@PostMapping(path = "/confirm_forgot_password_token")
    public ResponseEntity<?> confirmUserActivationToken(
			@RequestBody HashMap<String, String> userToken,
            HttpServletRequest request
		) {
		final int verificationStatus = userForgotemailService.validateConfirmationToken(userToken.get("confirmationToken"));
		final JSONObject resultJson = new JSONObject();
		if (verificationStatus == 1) {
			resultJson.put("successStatus", 1);
            resultJson.put("message", "Token was verified.");
			return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
		} else if (verificationStatus == 2) {
			resultJson.put("successStatus", 2);
            resultJson.put("message", "This token is already used. Token can be used only once.");
			return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
		} else if (verificationStatus == 3) {
			resultJson.put("successStatus", 3);
            resultJson.put("message", "This token is already expired. Please make a reset request again.");
			return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
		}
		resultJson.put("isSuccess", false);
		return ResponseEntity.badRequest().body(new MessageResponse("This token is already expired. Please make a reset request again."));
	}

	@PostMapping(path = "/change_password")
    public ResponseEntity<?> changePassword(
			@RequestBody PasswordResetRequest passwordResetRequest,
			BindingResult bindingResult,
            HttpServletRequest request
		) {
		final int verificationStatus = userForgotemailService.validateConfirmationToken(passwordResetRequest.getUserToken());
		final JSONObject resultJson = new JSONObject();
		if (verificationStatus == 1) {
			resultJson.put("successStatus", 1);
			try {
				passwordResetRequestValidator.validate(passwordResetRequest, bindingResult);
				if(!bindingResult.hasErrors()){
					final int verificationStatusReset = userForgotemailService.resetPassword(passwordResetRequest);
					if (verificationStatusReset == 1) {
						return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
					} else {
						return ResponseEntity.badRequest().body(new MessageResponse("This token is already expired. Please make a reset request again."));
					}
				} else {
					final List<FieldError> errors = bindingResult.getFieldErrors();
                    final HashMap<String, String> errorList = new HashMap<>();
                    for (FieldError error : errors ) {
                        final String message = messageSource.getMessage(error, null);
                        errorList.put(error.getField(), message);
                    }
                    resultJson.put("errors", errorList);
                    resultJson.put("successStatus", 4);
                    return ResponseEntity.badRequest().body(resultJson.toMap());
				}
			} catch (Exception e) {
				e.printStackTrace();
				resultJson.put("message", "This token is already expired. Please make a reset request again.");
				resultJson.put("successStatus", 3);
				return ResponseEntity.badRequest().body(resultJson.toMap());
			}
		} else if (verificationStatus == 2) {
			resultJson.put("successStatus", 2);
            resultJson.put("message", "This token is already used. Token can be used only once.");
			return ResponseEntity.badRequest().body(resultJson.toMap());
		} else if (verificationStatus == 3) {
			resultJson.put("successStatus", 3);
            resultJson.put("message", "This token is already expired. Please make a reset request again.");
			return ResponseEntity.badRequest().body(resultJson.toMap());
		}
		resultJson.put("message", "This token is already expired. Please make a reset request again.");
		resultJson.put("successStatus", 5);
		return ResponseEntity.badRequest().body(resultJson.toMap());
	}

	@PostMapping(path = "/change_password_profile")
    public ResponseEntity<?> changePasswordProfile(
			@RequestBody ProfilePasswordResetRequest profilePasswordResetRequest,
			BindingResult bindingResult,
			Principal userPrincipal,
            HttpServletRequest request
		) {

		final JSONObject resultJson = new JSONObject();
		try {
			UserDetails principal = (UserDetails)((Authentication) userPrincipal).getPrincipal();
			User user = userService.findByEmail(principal.getUsername());
			profilePasswordResetRequest.setUser(user);
			profilePasswordResetRequestValidator.validate(profilePasswordResetRequest, bindingResult);
			if(!bindingResult.hasErrors()){
				final int verificationStatusReset = userForgotemailService.resetPassword(profilePasswordResetRequest);
				if (verificationStatusReset == 1) {
					return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
				} else {
					return ResponseEntity.badRequest().body(new MessageResponse("Sorry, server error. Please try again."));
				}
			} else {
				final List<FieldError> errors = bindingResult.getFieldErrors();
				final HashMap<String, String> errorList = new HashMap<>();
				for (FieldError error : errors ) {
					final String message = messageSource.getMessage(error, null);
					errorList.put(error.getField(), message);
				}
				resultJson.put("errors", errorList);
				return ResponseEntity.badRequest().body(resultJson.toMap());
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.put("message", "Sorry, server error. Please try again.");
			return ResponseEntity.badRequest().body(resultJson.toMap());
		}
	}
}
