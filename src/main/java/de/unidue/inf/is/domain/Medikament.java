package de.unidue.inf.is.domain;

public class Medikament {

		private int pzn;
		private String name;
		private int packungsgroesse;
		private int mindestbestand;
		private int lagerbestand;
		private String madeby;
		
		public Medikament(int pzn, String name, int packungsgroesse, int mindestbestand, int lagerbestand, String madeby) {
			this.pzn = pzn;
			this.name = name;
			this.packungsgroesse = packungsgroesse;
			this.mindestbestand = mindestbestand;
			this.lagerbestand = lagerbestand;
			this.madeby = madeby;
		}

		public int getPzn() {
			return pzn;
		}

		public void setPzn(int pzn) {
			this.pzn = pzn;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getPackungsgroesse() {
			return packungsgroesse;
		}

		public void setPackungsgroesse(int packungsgroesse) {
			this.packungsgroesse = packungsgroesse;
		}

		public int getMindestbestand() {
			return mindestbestand;
		}

		public void setMindestbestand(int mindestbestand) {
			this.mindestbestand = mindestbestand;
		}

		public int getLagerbestand() {
			return lagerbestand;
		}

		public void setLagerbestand(int lagerbestand) {
			this.lagerbestand = lagerbestand;
		}

		public String getMadeby() {
			return madeby;
		}

		public void setMadeby(String madeby) {
			this.madeby = madeby;
		}
}
