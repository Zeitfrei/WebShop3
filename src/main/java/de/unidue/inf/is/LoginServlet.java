package de.unidue.inf.is;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.stores.LoginStore;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private boolean loggedIn = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
			request.getRequestDispatcher("/login.ftl").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        
        try(LoginStore loginStore = new LoginStore()) {
        	setLoginStatus(loginStore.checkLogin(login, password));
        	if (getLoginStatus() == true) {
        		Cookie loginCookie = new Cookie("login", login);
        		loginCookie.setMaxAge(3600);
        		response.addCookie(loginCookie);
        		response.sendRedirect("/menu");
        		return;
        	} else {
        		request.setAttribute("error", "Login failed");
        		doGet(request, response);
        	}
        }	
	}
	
	public boolean getLoginStatus() {
		return loggedIn;
	}
	
	public void setLoginStatus(boolean stat) {
		this.loggedIn = stat;
	}
	
}
