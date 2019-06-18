package ch.bytecrowd.devtools.beans.auth;

import java.io.Serializable;
import java.util.Objects;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ch.bytecrowd.devtools.beans.session.UserSessionBean;

@ViewScoped
@Named("authenticationBean")
public class AuthenticationBean implements Serializable {

	private static final long serialVersionUID = -9136603282115434241L;

	@Inject
	private UserSessionBean userSessionBean;
	
	private String username;
	private String password;
	
	public String authenticate() {
        // real login will be implemented when needed
		if (password != null && Objects.equals(username, password)) {
			userSessionBean.setLoggedIn(true);
			return "/pages/index.jsf?faces-redirect=true&includeViewParams=true";
		} else {
			return "/login.jsf?faces-redirect=true&includeViewParams=true";
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
