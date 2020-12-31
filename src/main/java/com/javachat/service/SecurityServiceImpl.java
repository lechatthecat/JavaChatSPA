package com.javachat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javachat.model.User;

@Service
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Override
    @Transactional(rollbackFor=Exception.class)
    public String findLoggedInUsername() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserDetails) {
            return ((UserDetails)userDetails).getUsername();
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean login(String email, String password) throws UsernameNotFoundException, AuthenticationException {
        User user = userService.findByEmail(email);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        
        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            logger.debug(String.format("Login %s successfully!", user.getEmail()));
            return true;
        }
        
        return false;
    }
}