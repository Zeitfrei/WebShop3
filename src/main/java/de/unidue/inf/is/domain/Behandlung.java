package de.unidue.inf.is.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Behandlung {

	private String arztLogin;
	private Date datum;
	private int id;
	private int pzn;
	private String notizen; 
	
	private List<String> mediList = new ArrayList<>();
	private List<String> krankheitList = new ArrayList<>();
	
	public Behandlung(String arztLogin, Date datum, int id, int pzn, String krankheit) {
		this.arztLogin = arztLogin;
		this.datum = datum;
		this.id = id;
		this.pzn = pzn;
		this.krankheitList.add(krankheit);
	}
	
	/* Konstruktor fuer output @ patientbehandeln.ftl */
	public Behandlung(String arztLogin, Date datum, int id, String medikamente, String krankheit, String notizen) {
		this.arztLogin = arztLogin;
		this.datum = datum;
		this.id = id;
		this.mediList.add(medikamente);
		this.krankheitList.add(krankheit);
		this.notizen = notizen;
	}
	
	public void setMedikamente(String med) {
		this.mediList.add(med);
	}
	
	public String getMedikamente() {
		String out = "";
		for (int i=0; i<mediList.size(); i++) {
			out += mediList.get(i);
			if(i!=mediList.size()-1) out += ", ";
		}
		return out;
	}
	
	public void setNotizen(String notiz) {
		this.notizen += notiz;
	}
	
	public String getNotizen() {
		return notizen;
	}
		
	public String getArztLogin() {
		return arztLogin;
	}

	public Date getDatum() {
		return datum;
	}

	public int getId() {
		return id;
	}

	public int getPzn() {
		return pzn;
	}
	
	public void setKrankheit(String krank) {
		krankheitList.add(krank);
	}
	public String getKrankheit() {
		String out = "";
		for (int i=0; i<krankheitList.size(); i++) {
			out += krankheitList.get(i);
			if(i!=krankheitList.size()-1) out += ", ";
		}
		return out;
	}
	
	
}
