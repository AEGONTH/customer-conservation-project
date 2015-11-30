package com.adms.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.omnifaces.util.Faces;

import com.adms.web.bean.login.LoginSession;


public class AdminFilter extends AbstractFilter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(true);
		
		if(session == null) {
			doLogin(request, response, req);
			return;
		}
//		LoginSession ls = Faces.evaluateExpressionGet("#{loginSession}");
		LoginSession loginSession = (LoginSession) session.getAttribute("loginSession");
		if(loginSession == null || (loginSession != null && !loginSession.getDistinctPrivileges().contains("CS_ADMIN"))) {
			accessDenied(request, response, req);
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}

}
