package ch.bytecrowd.devtools.beans.session;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named("userSessionBean")
public class UserSessionBean implements Serializable {
	
	private static final long serialVersionUID = 602401084402780347L;
	
	private boolean loggedIn = false;

	public String logout() {
		loggedIn = false;
		return "/login.jsf?faces-redirect=true&includeViewParams=true";
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}
}
