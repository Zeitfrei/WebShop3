package de.unidue.inf.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Patient;
import de.unidue.inf.is.stores.LoginStore;
import de.unidue.inf.is.stores.PatientStore;
import de.unidue.inf.is.stores.StoreException;

@WebServlet("/patientenliste")
public class PatientenlisteServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5821756272000400875L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		/* CHECK LOGIN */
		try(LoginStore loginStore = new LoginStore()) {
			if(!loginStore.checkLogin(request, response)) {
				request.getRequestDispatcher("/patientenliste.ftl").forward(request, response);
				return;}
		} catch (StoreException e) {
			request.setAttribute("error", "login error");
		}
		/* CHECK LOGIN */
		System.out.println("HALLO");
		List<Patient> patientList = new ArrayList<>();
		try(PatientStore patStore = new PatientStore()) {
			patientList = patStore.getAllPatients();
			request.setAttribute("patients", patientList);
		} catch (StoreException e) {
			request.setAttribute("error", "patienten koennen nicht geladen werden");
		}
         request.getRequestDispatcher("/patientenliste.ftl").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String id = request.getParameter("_id");
		Cookie cookie = new Cookie("id_no", id);
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
		response.sendRedirect("/patientbehandeln");	
		return;
	}

	
}
