package ch.bytecrowd.devtools.beans.session;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import org.apache.log4j.Logger;

@SessionScoped
public class GroovyEditorSession implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(GroovyEditorSession.class);

	private String script;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
}