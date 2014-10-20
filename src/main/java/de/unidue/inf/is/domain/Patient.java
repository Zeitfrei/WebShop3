package de.unidue.inf.is.domain;

import java.sql.Date;

public class Patient {
	
	private int id;
	private String name;
	private Date geburtstag;

	public Patient(int id, String name, Date geburtstag) {
		this.id = id;
		this.name = name;
		this.geburtstag = geburtstag;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getGeburtstag() {
		return geburtstag;
	}

}
