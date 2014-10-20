package de.unidue.inf.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Medikament;
import de.unidue.inf.is.stores.ApothekeStore;
import de.unidue.inf.is.stores.LoginStore;
import de.unidue.inf.is.stores.StoreException;

@WebServlet("/apotheke")
public class ApothekeServlet extends HttpServlet {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Medikament> mediList = new ArrayList<Medikament>();


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		/* CHECK LOGIN */
		try(LoginStore loginStore = new LoginStore()) {
			if(!loginStore.checkLogin(request, response)) {
				request.getRequestDispatcher("/apotheke.ftl").forward(request, response);
				return; 
				}
		} catch (StoreException e) {
			request.setAttribute("error", "login error");
		}
		/* CHECK LOGIN */

		try(ApothekeStore apStore = new ApothekeStore()) {
			synchronized(mediList) {
			mediList = apStore.getAllMeds();
			}
			request.setAttribute("meds", mediList);
		} catch (StoreException e) {
			request.setAttribute("medi_error", "medikamente koennen nicht geladen werden");
		}
		
        request.getRequestDispatcher("/apotheke.ftl").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String pzn = request.getParameter("med");
		String anzahl = request.getParameter("anzahl");
		String action = request.getParameter("action");

		if("remove".equals(action)) {
			boolean success = false;
			if (anzahl != null && !anzahl.isEmpty()) {
				if("remove".equals(action)) {
					try(ApothekeStore apStore = new ApothekeStore()) {
						success = apStore.entnehmeMedikament(pzn, anzahl);
						if(!success) {
							synchronized(mediList) {
							mediList = apStore.getAllMeds();
							}
							for (int i=0; i<mediList.size(); i++) {
								if (Integer.parseInt(pzn) == mediList.get(i).getPzn()) {
									request.setAttribute("place", i);
									break;
								}
							}
							request.setAttribute("error", "fehler beim entnehmen");
						}
						apStore.complete();
					} catch (StoreException e) {
						request.setAttribute("error", "error");
					}
				}
				if(success) {
				response.sendRedirect("/menu");
				return;
				}
			}
		}			
		doGet(request, response);
	}
	
}