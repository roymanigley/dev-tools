package ch.bytecrowd.devtools.beans.view;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ch.bytecrowd.devtools.beans.session.GroovyEditorSession;
import ch.bytecrowd.devtools.utils.GroovyUtils;

@Named("groovyEditorView")
@ViewScoped
public class GroovyEditorView implements Serializable {

	private static final Logger LOG = Logger.getLogger(GroovyEditorView.class);
	private static final long serialVersionUID = 5969895348296613997L;
	
	private ExecutorService threadExecutor = null;
	private Future<?> currentThread = null;
	private boolean canceled = Boolean.FALSE;
	
	@Inject
	private GroovyEditorSession editorSession;
	
	private volatile String output = "";
	
	@PostConstruct
	public void init() {
	}
	
	public void executeScript() {
		executeScriptSelection(editorSession.getScript());
	}
	
	public void executeScriptSelection(String script) {
		try {
			canceled = Boolean.FALSE;
			threadExecutor = Executors.newSingleThreadExecutor();
			currentThread = threadExecutor.submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						if (LOG.isDebugEnabled())
							LOG.debug("Executing script:\n" + script);
						output = GroovyUtils.execGroovy(script);
					} catch (Exception e) {
						LOG.error("Script execution failed:\n" + script, e);
						output = e.getMessage();
					}
				}
			});
			
			threadExecutor.shutdown();
			while (!threadExecutor.awaitTermination(1L, TimeUnit.SECONDS)) {  }
			if (!canceled) {
				RequestContext requestCtx = RequestContext.getCurrentInstance();
				requestCtx.update("groovyOutput");
				requestCtx.execute("PF('groovyOutput').show();");
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Script execution done", ""));
			}
		} catch (Exception e) {
			LOG.error("Script execution failed:\n" + script, e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Script execution failed", e.getMessage()));
		}
	}
	
	public void stopScript() {
		System.out.println("Stopping script");
		if (currentThread != null && !currentThread.isCancelled() && !currentThread.isDone()) {
			canceled = Boolean.TRUE;
			try {
				currentThread.cancel(true);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Script stopped successfully", ""));
			} catch (Exception e) {
				LOG.error("Stopping script failed", e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Script execution failed", e.getMessage()));
			}
		}

	}

	public String getScript() {
		return editorSession.getScript();
	}

	public void setScript(String script) {
		this.editorSession.setScript(script);
	}

	public String getOutput() {
		return output;
	}
	
}
