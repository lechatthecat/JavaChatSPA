package com.javachat.service;

import java.security.SecureRandom;
import java.time.ZonedDateTime;

import com.javachat.model.UserForgotemail;
import com.javachat.model.payload.PasswordResetRequest;
import com.javachat.model.payload.ProfilePasswordResetRequest;
import com.javachat.repository.UserForgotemailRepository;
import com.javachat.repository.UserRepository;
import com.javachat.util.Utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class UserForgotemailService {
    @Autowired
    UserForgotemailRepository userForgotemailRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    Utility utility;
    @Autowired
    UserRepository userRepository;
    long expiredTime = 60l;

    @Transactional(rollbackFor=Exception.class)
    public String getUniqueConfirmationToken() {
        String randomString = RandomStringUtils.random(73, 0, 0, true, true, null, new SecureRandom());
        if (!userForgotemailRepository.existsByConfirmationToken(randomString)) {
            return randomString;
        } else {
            return this.getUniqueConfirmationToken();
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public int validateConfirmationToken(String confirmationToken) {
        try {
            ZonedDateTime now = utility.getCurrentLocalTime();
            UserForgotemail userConfirmation = userForgotemailRepository.findUserConfirmationByConfirmation(confirmationToken);
            if (userConfirmation  == null) {
                return 2;
            }
            long minutesDiff = utility.zonedDateTimeDifference(userConfirmation.getCreated(), now);
            if (minutesDiff > this.expiredTime || userConfirmation.getIsUsed()) {
                return 3;
            }
            return 1;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public int resetPassword(PasswordResetRequest passwordResetRequest) {
        try {
            ZonedDateTime now = utility.getCurrentLocalTime();
            UserForgotemail userConfirmation = userForgotemailRepository.findUserConfirmationByConfirmation(passwordResetRequest.getUserToken());
            if (userConfirmation  == null) {
                return 2;
            }
            long minutesDiff = utility.zonedDateTimeDifference(userConfirmation.getCreated(), now);
            if (minutesDiff > this.expiredTime || userConfirmation.getIsUsed()) {
                return 3;
            }
            userConfirmation.setIsUsed(true);
            userConfirmation.setUpdated(now);
            userConfirmation.getUser().setPassword(bCryptPasswordEncoder.encode(passwordResetRequest.getPassword()));
            userForgotemailRepository.save(userConfirmation);
            return 1;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public int resetPassword(ProfilePasswordResetRequest profilePasswordResetRequest) {
        try {
            profilePasswordResetRequest.getUser().setPassword(bCryptPasswordEncoder.encode(profilePasswordResetRequest.getPassword()));
            userRepository.save(profilePasswordResetRequest.getUser());
            return 1;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }
    }
}
