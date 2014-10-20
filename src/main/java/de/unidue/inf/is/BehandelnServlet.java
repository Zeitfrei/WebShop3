package de.unidue.inf.is;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Behandlung;
import de.unidue.inf.is.domain.Krankheit;
import de.unidue.inf.is.domain.Medikament;
import de.unidue.inf.is.domain.Patient;
import de.unidue.inf.is.stores.ApothekeStore;
import de.unidue.inf.is.stores.LoginStore;
import de.unidue.inf.is.stores.PatientStore;
import de.unidue.inf.is.stores.StoreException;

@WebServlet("/patientbehandeln")
public class BehandelnServlet extends HttpServlet {

	private int _id;
	
	private List<Behandlung> behandlungList = new ArrayList<>();
	private List<Krankheit> krankheitList = new ArrayList<>();
	private  List<Medikament> medikamentList = new ArrayList<>();
	private List<Medikament> medikamentList_dia = new ArrayList<>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		/* CHECK LOGIN */
		try(LoginStore loginStore = new LoginStore()) {
			if(!loginStore.checkLogin(request, response)) {
				request.getRequestDispatcher("/patientbehandeln.ftl").forward(request, response);
				return;}
		} catch (StoreException e) {
			request.setAttribute("error", "login error");
		}
		/* CHECK LOGIN */
		
		/* get ID from Patient */
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals("id_no")) {
					_id = Integer.parseInt(cookie.getValue());
				}
			}
		}
		
		// Init
		Patient _patient = null;
		try(ApothekeStore apStore = new ApothekeStore()) {
			medikamentList = apStore.getAllMeds();
			request.setAttribute("medikamente", medikamentList);
			apStore.complete();
		} catch (StoreException e) {
			System.out.println("medsuchen error");
			request.setAttribute("error", "error"); // ERROR SETZEN @ FTL
		}
		// getPatient()
		try(PatientStore patStore = new PatientStore()) {
			_patient = patStore.getPatient(_id);
			request.setAttribute("patient", _patient);
			synchronized(behandlungList) {
			behandlungList = patStore.getAllBehandlungen(_id);
			}
			/* eintraege im table behandelt koennen mehrmals mit der selben id+datum vorkommen */
			List<Behandlung> behandlungOutList = new ArrayList<>();
			for (int i=0; i<behandlungList.size(); i++) {
				boolean exists = false;
				int position = -1;
				for(int j=0; j<behandlungOutList.size(); j++) {
					if( (behandlungList.get(i).getDatum().equals(behandlungOutList.get(j).getDatum())) && (behandlungList.get(i).getId() == behandlungOutList.get(j).getId())) {
						exists = true;
						position = j;
						break;
					}
				}
				String med = PznToString(behandlungList.get(i).getPzn());
				String notizen = patStore.getNotizen(behandlungList.get(i).getId(), behandlungList.get(i).getDatum());
				if(!exists) {
					behandlungOutList.add(new Behandlung(behandlungList.get(i).getArztLogin(), behandlungList.get(i).getDatum(), behandlungList.get(i).getId(), med, behandlungList.get(i).getKrankheit(), notizen));
				} else {
					behandlungOutList.get(position).setMedikamente(med);
					behandlungOutList.get(position).setKrankheit(med);
				}
				
			}
			request.setAttribute("behandlungen", behandlungOutList);
			synchronized(krankheitList) {
			krankheitList = patStore.getAllKrankheit();
			}
			request.setAttribute("krankheiten", krankheitList);
		} catch (StoreException e) {
			request.setAttribute("error", "fehler beim laden von patient");
		}
		
		
		/* dropdown liste mit krankheiten */
		String action = request.getParameter("whatbutton");
		String diagnose = request.getParameter("dia");
		
		if("searchmed".equals(action)) {
			try(ApothekeStore apStore = new ApothekeStore()) {
				synchronized(medikamentList_dia) {
				medikamentList_dia = apStore.findMedikament(diagnose);
				}
				request.setAttribute("medikamente", medikamentList_dia);
				for (int i=0; i<krankheitList.size(); i++) {
					if(krankheitList.get(i).getName().equals(diagnose)) {
						request.setAttribute("place_dia", i);
						break;
					}
				}
				apStore.complete();
			} catch (StoreException e) {
				request.setAttribute("error", "error"); // ERROR SETZEN @ FTL
			}
		} else {
			try(ApothekeStore apStore = new ApothekeStore()) {
				synchronized(medikamentList) {
				medikamentList = apStore.getAllMeds();
				}
				request.setAttribute("medikamente", medikamentList);
			} catch (StoreException e) {
				request.setAttribute("error", "fehler beim laden von patient");
			}
		}

        request.getRequestDispatcher("/patientbehandeln.ftl").forward(request, response);
	}
	

	private String PznToString(int pzn) {
		for (int i=0; i<medikamentList.size(); i++) {
			if(pzn == medikamentList.get(i).getPzn()) return medikamentList.get(i).getName();
		}
		return null;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String action = request.getParameter("whatbutton");
		String stringDate = request.getParameter("datum");
		java.sql.Date sqlDate = null;
		boolean wechselwirkung=false;
		/* beim posten einer neuen diagnose+medikament+notizen ins table behandelt muss vorher geprueft werden */
		/* ob die foreign keys (id + datum) im table behandlungsvorgang vorhanden sind */
		if("adddia".equals(action) || "sub".equals(action)) {
			String krankheit = request.getParameter("dia");
			String medikamentPzn = request.getParameter("med");
			boolean exists = false;
			/* string to sql.date */
			try {
	            SimpleDateFormat sdfToDate = new SimpleDateFormat("dd.MM.yyyy");
	            java.util.Date utilDate = sdfToDate.parse(stringDate);
	            sqlDate = new java.sql.Date(utilDate.getTime());
	        } catch (ParseException ex2) {
	        	request.setAttribute("error", "datum falsch eingetragen");
	            ex2.printStackTrace();
	        }
			/* check if date exists */
			for (int i=0;i<behandlungList.size();i++) {
				if (behandlungList.get(i).getDatum().equals(sqlDate)) {
					exists = true;
					break;
				}
			}
			String notizen = request.getParameter("notizen");
			/* falls der behandlungsvorgang nicht existiert wird er neu erstellt */
			if(!exists) {
				try(PatientStore patStore = new PatientStore()) {
					patStore.addBehandlungsvorgang(_id, sqlDate, notizen);
					patStore.complete();
				} catch (StoreException e) {
					request.setAttribute("error", "fehler beim erstellen von behandlungsvorgang");
				}
			}
			
			/* check Wechselwirkung */
			int medikamentPzn_int = Integer.parseInt(medikamentPzn);
			if(exists) {
				for (int i=0; i<behandlungList.size(); i++) {
					if(behandlungList.get(i).getDatum().equals(sqlDate)) {
						try(ApothekeStore apStore = new ApothekeStore()) {
							if(!apStore.checkMedikament(medikamentPzn_int, behandlungList.get(i).getPzn())) {
								wechselwirkung = true;
								request.setAttribute("error", "wechselwirkung gefunden!!");
								break;
							}
						} catch (StoreException e) {
							request.setAttribute("error", "fehler!");
						}
					}
				}
			}
			if(!wechselwirkung) {
				/* die behandlung wird zum table behandelt hinzugefuegt */ 
				String arztLogin = null;
				Cookie[] cookies = request.getCookies();
				for(Cookie cookie : cookies) {
					if (cookie.getName().equals("login")) arztLogin = cookie.getValue();
				}
				try(PatientStore patStore = new PatientStore()) {
					int pzn = Integer.parseInt(medikamentPzn);
					patStore.addBehandlung(arztLogin, sqlDate, _id, pzn, krankheit);
					patStore.complete();
				} catch (StoreException e) {
					request.setAttribute("error", "fehler!");
				}
				/* falls der behandlungsvorgang existiert, werden die neuen notizen an die alten drangehangen */
				if(exists) {
					try(PatientStore patStore = new PatientStore()) {
						patStore.addNotizen(_id, sqlDate, notizen);
						patStore.complete();
					} catch (StoreException e) {
						request.setAttribute("error", "fehler!");
					}
				}
			}
		}

		if ("sub".equals(action) && !wechselwirkung) {
			response.sendRedirect("/patientenliste");
			return;
		}
		doGet(request, response);
	}
}