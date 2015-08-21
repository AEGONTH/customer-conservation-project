package com.adms.web.view.handler;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class MyViewHandlerWrapper extends ViewHandlerWrapper {

	private ViewHandler wrapped;
	
	public MyViewHandlerWrapper() {
		
	}
	
	public MyViewHandlerWrapper(ViewHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ViewHandler getWrapped() {
		return wrapped;
	}
	
	@Override
	public String calculateRenderKitId(FacesContext context) {
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
        String userAgent = req.getHeader("user-agent");
        String accept = req.getHeader("Accept");

        if (userAgent != null && accept != null) {
//            UserAgentInfo agent = new UserAgentInfo(userAgent, accept);
//            if (agent.isMobileDevice()) {
//                return "PRIMEFACES_MOBILE";
//            }
        }

        return this.wrapped.calculateRenderKitId(context);
	}
}
