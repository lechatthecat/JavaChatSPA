package com.javachat.controller.user_confirm;

import javax.servlet.http.HttpServletRequest;

import com.javachat.jwt.payload.response.MessageResponse;
import com.javachat.model.UserConfirmation;
import com.javachat.service.UserConfirmationService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class UserConfirmController {
	@Autowired
	UserConfirmationService userConfirmationService;
	Logger logger = LoggerFactory.getLogger(UserConfirmController.class);
    
	@PostMapping(path = "/confirm_account")
    public ResponseEntity<?> confirmUserActivationToken(
			@RequestBody UserConfirmation userToken,
            HttpServletRequest request
		) {
		final int verificationStatus = userConfirmationService.validateConfirmationToken(userToken.getConfirmationToken());
		final JSONObject resultJson = new JSONObject();
		if (verificationStatus == 1) {
			resultJson.put("successStatus", 1);
            resultJson.put("message", "Email was verified. You can login with the email now.");
			return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
		} else if (verificationStatus == 2) {
			resultJson.put("successStatus", 2);
            resultJson.put("message", "Email is already verified.");
			return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
		} else if (verificationStatus == 3) {
			resultJson.put("successStatus", 3);
            resultJson.put("message", "This token is already expired. Please register your account again.");
			return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
		} else if (verificationStatus == 4) {
			resultJson.put("successStatus", 4);
            resultJson.put("message", "This token is already expired. Please register your account again.");
			return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
		}
		resultJson.put("isSuccess", false);
		return ResponseEntity.badRequest().body(new MessageResponse("This token is already expired. Please register your account again."));
	}
}
