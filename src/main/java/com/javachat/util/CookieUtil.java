package com.javachat.util;

import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class CookieUtil {

	@Value("${chat.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	@Value("${chat.server.cookie.domain}")
	private String cookie_domain;

    public Cookie makeCookie (String cookieName, String body, HttpServletRequest request) {
        final Cookie cookie = new Cookie(cookieName, body);
		cookie.setDomain(cookie_domain);
		String scheme = request.getScheme();
		boolean isHttps = scheme.equals("https");
		cookie.setPath("/");
		cookie.setSecure(isHttps);
		//cookie.setHttpOnly(true);
		cookie.setMaxAge(jwtExpirationMs);
		return cookie;
	}
	
	public Cookie deleteCookie (String cookieName, HttpServletRequest request) {
        final Cookie cookie = new Cookie(cookieName, "");
		cookie.setDomain(cookie_domain);
		String scheme = request.getScheme();
		boolean isHttps = scheme.equals("https");
		cookie.setPath("/");
		cookie.setSecure(isHttps);
		//cookie.setHttpOnly(true);
		cookie.setMaxAge(-100000);
		return cookie;
    }
}
