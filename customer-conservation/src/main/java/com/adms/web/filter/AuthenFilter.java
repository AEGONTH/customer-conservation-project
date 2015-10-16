package com.adms.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.adms.web.bean.login.LoginSession;

public class AuthenFilter extends AbstractFilter {

	private static List<String> allowedURIs;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if(allowedURIs == null) {
			allowedURIs = new ArrayList<>();
			allowedURIs.add("login");
			allowedURIs.add("errors");
			allowedURIs.add("/public/");
			allowedURIs.add("javax.faces.resource");
			allowedURIs.add("resources");
			allowedURIs.add("authen-ws");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("Authen Filter");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		String reqURI = req.getRequestURI();
		
		if(session.isNew()) {
			doLogin(request, response, req);
			return;
		}
		
		LoginSession loginSession = (LoginSession) session.getAttribute("loginSession");
		if((loginSession == null 
				|| (loginSession != null && StringUtils.isEmpty(loginSession.getUsername())))
				&& allowedURIs.stream().filter(p -> reqURI.contains(p)).collect(Collectors.toList()).isEmpty()) {
			doLogin(request, response, req);
			return;
		}
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {

	}

}
