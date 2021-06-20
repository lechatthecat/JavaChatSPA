package com.javachat.service;

import java.security.SecureRandom;
import java.time.ZonedDateTime;

import com.javachat.model.UserConfirmation;
import com.javachat.repository.UserConfirmationRepository;
import com.javachat.repository.UserRepository;
import com.javachat.util.Utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class UserConfirmationService {
    @Autowired
    UserConfirmationRepository userConfirmationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    Utility utility;
    long expiredTime = 1440l;

    @Transactional(rollbackFor=Exception.class)
    public String getUniqueConfirmationToken() {
        String randomString = RandomStringUtils.random(73, 0, 0, true, true, null, new SecureRandom());
        if (!userConfirmationRepository.existsByConfirmationToken(randomString)) {
            return randomString;
        } else {
            return this.getUniqueConfirmationToken();
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public int validateConfirmationToken(String confirmationToken) {
        try {
            ZonedDateTime now = utility.getCurrentLocalTime();
            UserConfirmation userConfirmation = userConfirmationRepository.findUserConfirmationIncludesVerifiedByConfirmation(confirmationToken);
            if (userConfirmation  == null) {
                return 4;
            }
            if (userConfirmation.getIsVerified()) {
                return 2;
            }
            long minutesDiff = utility.zonedDateTimeDifference(userConfirmation.getCreated(), now);
            if (minutesDiff > this.expiredTime) {
                // This user cannot be activated anymore, so delete it
                // so that user can be created again.
                userRepository.delete(userConfirmation.getUser());
                return 3;
            }
            userConfirmation.setIsVerified(true);
            userConfirmation.setUpdated(now);
            userConfirmation.getUser().setVerified(true);
            userConfirmationRepository.save(userConfirmation);
            return 1;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }
    }
}
