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
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebFilter(filterName="AuthenFilter", urlPatterns="/*")
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
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(false);
		String reqURI = req.getRequestURI();
		
		if(!allowedURIs.stream().filter(p -> reqURI.contains(p)).collect(Collectors.toList()).isEmpty()
				|| (session != null && session.getAttribute("loginSession") != null)) {
			chain.doFilter(request, response);
		} else {
			doLogin(request, response, req);
		}
	}
	
	@Override
	public void destroy() {

	}

}
