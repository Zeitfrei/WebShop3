package de.unidue.inf.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Patient;
import de.unidue.inf.is.domain.Studie;
import de.unidue.inf.is.stores.LoginStore;
import de.unidue.inf.is.stores.PatientStudieStore;
import de.unidue.inf.is.stores.StoreException;

@WebServlet("/patient_zu_studie")
public class PatientStudieServlet extends HttpServlet {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		/* CHECK LOGIN */
		try(LoginStore loginStore = new LoginStore()) {
			if(!loginStore.checkLogin(request, response)) {
				request.getRequestDispatcher("/patientstudie.ftl").forward(request, response);
				return;
				}
		} catch (StoreException e) {
			request.setAttribute("error", "login error");
		}
		/* CHECK LOGIN */
		
		List<Patient> patientenList = new ArrayList<Patient>();
		List<Studie> studiList = new ArrayList<Studie>();
		
		try(PatientStudieStore store = new PatientStudieStore()) {
			patientenList = store.getAllPatienten();
			studiList = store.getAllStudien();
			request.setAttribute("patienten", patientenList);
			request.setAttribute("studis", studiList);
		} catch (StoreException e) {
			System.out.println("error!!!!!");
			request.setAttribute("error", "etwas konnte nicht geladen werden");
		}
		
        request.getRequestDispatcher("/patientstudie.ftl").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String studie = request.getParameter("studi");
		String patient = request.getParameter("patient");
		String pseudo = request.getParameter("pseudo");
		String action = request.getParameter("action");

		if (pseudo != null && !pseudo.isEmpty()) {
			if("addpatient".equals(action)) {
				try(PatientStudieStore store = new PatientStudieStore()) {
					store.addToStudie(studie,patient,pseudo);
					store.complete();
					response.sendRedirect("/menu");
					return;
				} catch (StoreException e) {
					request.setAttribute("error", "error");
				}
			}
		}
		doGet(request, response);
	}
	
}