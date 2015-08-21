package com.adms.web.view.handler;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

public class Html5RenderKitFactory extends RenderKitFactory {

	private RenderKitFactory wrapped;

	public Html5RenderKitFactory(RenderKitFactory wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		wrapped.addRenderKit(renderKitId, renderKit);
	}

	@Override
	public RenderKit getRenderKit(FacesContext context, String renderKitId) {
		RenderKit renderKit = wrapped.getRenderKit(context, renderKitId);
		return (HTML_BASIC_RENDER_KIT.equals(renderKitId)) ? new Html5RenderKit(renderKit) : renderKit;
	}

	@Override
	public Iterator<String> getRenderKitIds() {
		return wrapped.getRenderKitIds();
	}

}
