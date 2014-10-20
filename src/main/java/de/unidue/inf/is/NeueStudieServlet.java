package de.unidue.inf.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Pharmafirma;
import de.unidue.inf.is.domain.Studie;
import de.unidue.inf.is.stores.LoginStore;
import de.unidue.inf.is.stores.NeueStudieStore;
import de.unidue.inf.is.stores.StoreException;

@WebServlet("/neue_studie")
public class NeueStudieServlet extends HttpServlet {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		/* CHECK LOGIN */
		try(LoginStore loginStore = new LoginStore()) {
			if(!loginStore.checkLogin(request, response)) {
				request.getRequestDispatcher("/neuestudie.ftl").forward(request, response);
				return;
				}
		} catch (StoreException e) {
			request.setAttribute("error", "login error");
		}
		/* CHECK LOGIN */
		
		List<Pharmafirma> pharmaList = new ArrayList<Pharmafirma>();
		List<Studie> studiList = new ArrayList<Studie>();
		
		try(NeueStudieStore store = new NeueStudieStore()) {
			pharmaList = store.getAllPharmas();
			studiList = store.getAllStudien();
			request.setAttribute("pharmas", pharmaList);
			request.setAttribute("studis", studiList);
		} catch (StoreException e) {
			System.out.println("error!!!!!");
			request.setAttribute("error", "etwas konnte nicht geladen werden");
		}

        request.getRequestDispatcher("/neuestudie.ftl").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String sName = request.getParameter("sName");
		String pName = request.getParameter("pharma");
		String aName = request.getParameter("studi");
		String action = request.getParameter("action");

		if (sName != null && !sName.isEmpty()) {
			if("add".equals(action)) {
				try(NeueStudieStore store = new NeueStudieStore()) {
					store.addStudie(sName,pName,aName);
					store.complete();
				} catch (StoreException e) {
					request.setAttribute("error", "error");
				}
			}
		}
		doGet(request, response);
	}
	
}