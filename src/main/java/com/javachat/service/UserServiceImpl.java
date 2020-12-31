package com.javachat.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import com.javachat.model.User;
import com.javachat.model.UserConfirmation;
import com.javachat.model.UserForgotemail;
import com.javachat.model.Role;
import com.javachat.repository.UserConfirmationRepository;
import com.javachat.repository.UserForgotemailRepository;
import com.javachat.repository.UserRepository;
import com.javachat.util.Constants;
import com.javachat.util.Utility;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @Autowired
    EmailSenderService emailSender;
    @Value("${chat.server.domain}")
    private String domain;
    @Value("${chat.server.host}")
    private String host;
    @Autowired
    UserConfirmationService userConfirmationService;
    @Autowired
    UserConfirmationRepository userConfirmationRepository;
    @Autowired
    UserForgotemailService userForgotemailService;
    @Autowired
    UserForgotemailRepository userForgotemailRepository;
    @Autowired
    Utility utility;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean deleteUser(User user) {
        try {
            user.setIsDeleted(true);
            user.setIsVerified(false);
            this.userRepository.save(user);
        } catch (DataAccessException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean save(User user) {
        try { 
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean createUser(User user) {
        try {
            final ZonedDateTime now = utility.getCurrentSystemLocalTime();
            user.setCreated(now);
            user.setUpdated(now);
            user.setIsDeleted(false);
            user.setRole(new Role());
            user.getRole().setId(Constants.USER_ROLE);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            final String userToken = userConfirmationService.getUniqueConfirmationToken();
            UserConfirmation userConfirmation = new UserConfirmation();
            userConfirmation.setConfirmationToken(userToken);
            userConfirmation.setUser(user);
            userConfirmation.setUpdated(now);
            userConfirmation.setCreated(now);
            userConfirmationRepository.save(userConfirmation);
            SimpleMailMessage message = new SimpleMailMessage(); 
            message.setFrom("noreply@chatboard.com");
            message.setTo(user.getEmail()); 
            message.setSubject("Please verify your email address");
            String mailText = 
                "Hi " 
                + user.getUsernameNonEmail() +"!"
                + "\r\n\r\nThis is ChatBoard."
                + "\r\nPlease verify your email by clicking on the URL."
                + "\r\nIf you haven't requested the verification, please don't click the URL."
                + "\r\n" + host + "://" + domain + "/confirm_account/" + userToken
                + "\r\nThis link will be disabled 1 day later."
                + "\r\n\r\nBest Regards,"
                + "\r\nChatBoard Contact";
            message.setText(mailText);
            emailSender.sendEmail(message);
            return true;
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean reCreateUser(User user) {
        try {
            final ZonedDateTime now = utility.getCurrentSystemLocalTime();
            //user.setCreated(now);
            user.setUpdated(now);
            user.setIsDeleted(false);
            user.setRole(new Role());
            user.getRole().setId(Constants.USER_ROLE);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            final String userToken = userConfirmationService.getUniqueConfirmationToken();
            UserConfirmation userConfirmation = new UserConfirmation();
            userConfirmation.setConfirmationToken(userToken);
            userConfirmation.setUser(user);
            userConfirmation.setUpdated(now);
            userConfirmation.setCreated(now);
            userConfirmationRepository.save(userConfirmation);
            SimpleMailMessage message = new SimpleMailMessage(); 
            message.setFrom("noreply@chatboard.com");
            message.setTo(user.getEmail()); 
            message.setSubject("Please verify your email address");
            String mailText = 
                "Hi " 
                + user.getUsernameNonEmail() +"!"
                + "\r\n\r\nThis is ChatBoard."
                + "\r\nPlease verify your email by clicking on the URL."
                + "\r\nIf you haven't requested the verification, please don't click the URL."
                + "\r\n" + host + "://" + domain + "/confirm_account/" + userToken
                + "\r\nThis link will be disabled 1 day later."
                + "\r\n\r\nBest Regards,"
                + "\r\nChatBoard Contact";
            message.setText(mailText);
            emailSender.sendEmail(message);
            return true;
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean sendForgotPasswordLink(User user) {
        try {
            final ZonedDateTime now = utility.getCurrentSystemLocalTime();
            final String userToken = userForgotemailService.getUniqueConfirmationToken();
            UserForgotemail userForgotemail = new UserForgotemail();
            userForgotemail.setConfirmationToken(userToken);
            userForgotemail.setUser(user);
            userForgotemail.setUpdated(now);
            userForgotemail.setCreated(now);
            userForgotemailRepository.save(userForgotemail);
            SimpleMailMessage message = new SimpleMailMessage(); 
            message.setFrom("noreply@chatboard.com");
            message.setTo(user.getEmail()); 
            message.setSubject("Password reset");
            String mailText = 
                "Hi " 
                + user.getUsernameNonEmail() +"!"
                + "\r\n\r\nThis is ChatBoard."
                + "\r\nPlease reset your password by clicking on the URL."
                + "\r\nIf you haven't requested the password reset, please don't click on the URL."
                + "\r\n" + host + "://" + domain + "/forgot_password/" + userToken
                + "\r\nThis link will be disabled 1 hour later."
                + "\r\n\r\nBest Regards,"
                + "\r\nChatBoard Contact";
            message.setText(mailText);
            emailSender.sendEmail(message);
            return true;
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public User findByEmailIncludesUnverified(String email) {
        return userRepository.findByEmailIncludesUnverified(email);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public User findByEmailIncludesDeleted(String email) {
        return userRepository.findByEmailIncludesDeleted(email);
    }

    @Override
    @Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRES_NEW)
    public User findByEmailDetached(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public User findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public List<User> findUsersByName(String name) {
        return userRepository.findUsersByName(name);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public List<User> findUsersByEmail(String email) {
        return userRepository.findUsersByEmail(email);
    }

    @Override
    public List<String> getUsersOfTopic(){
        if(simpUserRegistry != null){
            if(simpUserRegistry.getUsers() != null){
                List<String> usersOfTopic = simpUserRegistry.getUsers().parallelStream()
                .map(u -> {
                    return u.getName();
                }).collect(Collectors.toList());
                return usersOfTopic;
            }
        }
        return null;
    }

    @Override
    public List<String> getUsersOfTopic(String boardId){
        if(simpUserRegistry != null){
            if(simpUserRegistry.getUsers() != null){
                List<String> usersOfTopic = simpUserRegistry.getUsers().parallelStream()
                .filter(u -> u.getSessions().iterator().next().getSubscriptions().iterator().next().getDestination().equals("/board/public/"+boardId))
                .map(u -> {
                    return u.getName();
                }).collect(Collectors.toList());
                return usersOfTopic;
            }
        }
        return null;
    }
}