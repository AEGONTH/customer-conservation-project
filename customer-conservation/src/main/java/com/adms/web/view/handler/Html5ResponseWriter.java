package com.adms.web.view.handler;

import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

public class Html5ResponseWriter extends ResponseWriterWrapper {

	private static final String[] HTML5_INPUT_ATTRIBUTES = { "autofocus" };
	
	private ResponseWriter wrapped;
	
	public Html5ResponseWriter(ResponseWriter wrapped) {
        this.wrapped = wrapped;
    }
	
	@Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return new Html5ResponseWriter(super.cloneWithWriter(writer));
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        super.startElement(name, component);

        if ("input".equals(name)) {
            for (String attributeName : HTML5_INPUT_ATTRIBUTES) {
                String attributeValue = (String) component.getAttributes().get(attributeName);

                if (attributeValue != null) {
                    super.writeAttribute(attributeName, attributeValue, null);
                }
            }
        }
    }
	
	@Override
	public ResponseWriter getWrapped() {
		return wrapped;
	}
	
}
