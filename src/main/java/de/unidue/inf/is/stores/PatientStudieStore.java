package de.unidue.inf.is.stores;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.domain.Patient;
import de.unidue.inf.is.domain.Studie;
import de.unidue.inf.is.utils.DBUtil;

public class PatientStudieStore implements Closeable {

	private Connection connection;
	private boolean complete;
	private List<Patient> patientenList;
	private List<Studie> studiList;
	
	public PatientStudieStore() {
		try {
			connection = DBUtil.getConnection("hospital");
			connection.setAutoCommit(false);
			patientenList = new ArrayList<Patient>();
			studiList = new ArrayList<Studie>();
		} catch (SQLException e) {
			throw new StoreException(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		 if (connection != null) {
	            try {
	                if (complete) {
	                    connection.commit();
	                }
	                else {
	                    connection.rollback();
	                }
	            }
	            catch (SQLException e) {
	                throw new StoreException(e);
	            }
	            finally {
	                try {
	                    connection.close();
	                }
	                catch (SQLException e) {
	                    throw new StoreException(e);
	                }
	            }
	        }		
	}
	
	public void complete() {
        complete = true;
    }

	public List<Patient> getAllPatienten() {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Patient");
			rs = prep.executeQuery();
			synchronized (patientenList) {
				while (rs.next()) {
					patientenList.add(new Patient(rs.getInt("ID"),rs.getString("name"),rs.getDate("Geburtsdatum")));
				}
			}
		} catch (SQLException e) {
			throw new StoreException(e);
		} finally {
			if (rs != null) {
    			try {
    				rs.close();
    			} catch (SQLException e) {
    				throw new StoreException(e);
    			}
    		}
		}
		complete();
		return patientenList;
	}
	
	public List<Studie> getAllStudien() {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Studie");
			rs = prep.executeQuery();
			synchronized(studiList) {
				while (rs.next()) {
					studiList.add(new Studie(rs.getString("name"),rs.getString("firmaName")));
				}
			}
		} catch (SQLException e) {
			throw new StoreException(e);
		} finally {
			if (rs != null) {
    			try {
    				rs.close();
    			} catch (SQLException e) {
    				throw new StoreException(e);
    			}
    		}
		}
		complete();
		return studiList;
	}

	public void addToStudie(String studie, String patient, String pseudo) {
		
		studiList = getAllStudien();
		patientenList = getAllPatienten();
		String pharmaFirma = null;
		int patientId = -1;
		
		for(Studie s : studiList){
			if(s.getName().equals(studie))
				pharmaFirma = s.getFirmaName();
		}
		
		for(Patient p : patientenList){
			if(p.getName().equals(patient))
				patientId = p.getId();
		}
		
		//trigger einfügen: kümmert der sich um alles oder hier checken?
		
		//if patientid mit ausschließender studie in fake table drin dann diese weg
		
		try {
			PreparedStatement prepStatement = 
			connection.prepareStatement("INSERT INTO fake "
					+ "(name, studienname, studienpharmafirmaname, patientid) "
					+ "values (?,?,?,?)");
			prepStatement.setString(1, pseudo);
			prepStatement.setString(2, studie);
			prepStatement.setString(3, pharmaFirma);
			prepStatement.setInt(4, patientId);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			throw new StoreException(e);
		}
		complete();
	}
}