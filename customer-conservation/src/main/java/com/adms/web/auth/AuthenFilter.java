package com.adms.web.auth;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.adms.web.bean.login.LoginSession;

@WebFilter(filterName="AuthenFilter", urlPatterns="/*")
public class AuthenFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest reqt = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = reqt.getSession(false);
		
		String reqURI = reqt.getRequestURI();
		if(reqURI.indexOf("login") >= 0
				|| (session != null && session.getAttribute("username") != null)
				|| reqURI.indexOf("/public/") >= 0
				|| reqURI.contains("javax.faces.resource")
				|| reqURI.contains("resources")) {
			chain.doFilter(request, response);
		} else {
			resp.sendRedirect(reqt.getContextPath() + "/login");
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
