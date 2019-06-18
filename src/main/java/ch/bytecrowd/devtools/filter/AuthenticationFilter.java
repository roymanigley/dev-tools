package ch.bytecrowd.devtools.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.bytecrowd.devtools.beans.session.UserSessionBean;

@WebFilter("/pages/*")
public class AuthenticationFilter implements Filter {

	@Inject
	public UserSessionBean userSessionBean;
	
	public void destroy() { }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (userSessionBean != null && userSessionBean.isLoggedIn())
			chain.doFilter(request, response);
		else
			((HttpServletResponse)response).sendRedirect(((HttpServletRequest) request).getContextPath() + "/login.jsf");
		
		
	}
	
	public void init(FilterConfig fConfig) throws ServletException { }

}
