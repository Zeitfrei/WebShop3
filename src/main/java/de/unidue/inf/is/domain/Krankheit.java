package de.unidue.inf.is.domain;

public class Krankheit {
	
	String icd10;
	String name;
	
	public Krankheit(String icd10, String name) {
		this.icd10 = icd10;
		this.name = name;
	}

	public String getIcd10() {
		return icd10;
	}

	public String getName() {
		return name;
	}
		
}
