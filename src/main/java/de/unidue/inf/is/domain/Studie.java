package de.unidue.inf.is.domain;

public class Studie {
	
	private String name;
	private String firmaName;
	
	public Studie(String name, String firmaName){
		this.name = name;
		this.firmaName = firmaName;
	}
	
	/**
	 * GET/SET
	 */
	public void setName (String name){this.name = name;}
	public void setFirmaName (String firmaName){this.firmaName = firmaName;}
	
	public String getName (){return name;}
	public String getFirmaName (){return firmaName;}

}