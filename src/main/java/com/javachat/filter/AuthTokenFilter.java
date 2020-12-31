package com.javachat.filter;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.web.filter.OncePerRequestFilter;

import com.javachat.util.CookieUtil;
import com.javachat.util.JwtUtils;
import com.javachat.model.User;
import com.javachat.service.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
	private JwtUtils jwtUtils;
	@Autowired
	CookieUtil cookieutil;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			if (jwtUtils == null) {
				filterChain.doFilter(request, response);
				return;
			}
			String jwt = jwtUtils.parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUsernameFromToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				User user = (User) userDetails;
				if (user.getIsBanned()) {
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
				} else {
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}
		filterChain.doFilter(request, response);
	}
}
