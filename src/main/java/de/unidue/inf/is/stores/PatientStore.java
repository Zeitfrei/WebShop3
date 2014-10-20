package de.unidue.inf.is.stores;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.domain.Behandlung;
import de.unidue.inf.is.domain.Krankheit;
import de.unidue.inf.is.domain.Patient;
import de.unidue.inf.is.utils.DBUtil;

public class PatientStore implements Closeable {

	private Connection connection;
	private boolean complete;
	private List<Patient> patientList;
	private List<Behandlung> behandlungList;
	private List<Krankheit> krankheitList;
	
	public PatientStore() {
		try {
			connection = DBUtil.getConnection("hospital");
			connection.setAutoCommit(false);
			patientList = new ArrayList<Patient>();
			behandlungList = new ArrayList<Behandlung>();
			krankheitList = new ArrayList<Krankheit>();
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
	

	
	public Patient getPatient(int id) {
		ResultSet rs = null;
		Patient _patient = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Patient WHERE id = ?");
			prep.setInt(1, id);
			rs = prep.executeQuery();
			while (rs.next()) {
				_patient = new Patient(rs.getInt("id"), rs.getString("name"), rs.getDate("geburtsdatum"));
			}
		} catch (SQLException e) {
			throw new StoreException(e);
		}
		complete();
		return _patient;
	}
	
	public List<Krankheit> getAllKrankheit() {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Krankheit");
			rs = prep.executeQuery();
			synchronized(krankheitList) {
				while (rs.next()) {
					krankheitList.add(new Krankheit(rs.getString("icd10"), rs.getString("name")));
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
		return krankheitList;
	}

	public List<Behandlung> getAllBehandlungen(int id) {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM behandelt WHERE behandlungsPatientId = ?");
			prep.setInt(1, id);
			rs = prep.executeQuery();
			synchronized(krankheitList) {
				while(rs.next()) {
					behandlungList.add(new Behandlung(rs.getString("arztLogin"), rs.getDate("behandlungsDatum"), rs.getInt("behandlungsPatientId"), rs.getInt("medikamntPzn"), rs.getString("krankheitName")));
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
		return behandlungList;
	}
	
	public List<Patient> getAllPatients() {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Patient");
			rs = prep.executeQuery();
			synchronized(patientList) {
				while (rs.next()) {
					patientList.add(new Patient(rs.getInt("id"), rs.getString("name"), rs.getDate("geburtsdatum")));
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
		return patientList;
	}
	

	public void addBehandlungsvorgang(int _id, Date sqlDate, String notizen) {
		try {
			PreparedStatement prep = connection.prepareStatement("INSERT INTO Behandlungsvorgang (patientId, datum, notizen) "
					+ "VALUES ((SELECT id FROM Patient WHERE id = ?),?,?)");
			prep.setInt(1, _id);
			prep.setDate(2, sqlDate);
			prep.setString(3, notizen);
			prep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StoreException(e);
		} 	
		complete();
	}

	public void addBehandlung(String arztLogin, Date sqlDate, int _id, int pzn, String krankheit) {
		try {
			PreparedStatement prep = connection.prepareStatement("INSERT INTO behandelt "
					+ "VALUES (?, (SELECT datum FROM Behandlungsvorgang WHERE datum=? GROUP BY datum),"
					+ "(SELECT patientId FROM Behandlungsvorgang WHERE patientId=? GROUP BY patientId),?,?)");
			prep.setString(1, arztLogin);
			prep.setDate(2, sqlDate);
			prep.setInt(3, _id);
			prep.setInt(4, pzn);
			prep.setString(5, krankheit);
			prep.executeUpdate();
		} catch (SQLException e) {
			throw new StoreException(e);
		} 
		complete();
	}

	public String getNotizen(int id, Date datum) {
		ResultSet rs = null;
		String notizen = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT notizen FROM Behandlungsvorgang WHERE patientId=? AND datum=?");
			prep.setInt(1, id);
			prep.setDate(2, datum);
			rs = prep.executeQuery();
			while (rs.next()) {
				notizen = rs.getString("notizen");
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
		return notizen;
	}

	public void addNotizen(int _id, Date sqlDate, String notizen) {
		String notizen_temp = getNotizen(_id, sqlDate);
		try {
			PreparedStatement prep = connection.prepareStatement("UPDATE Behandlungsvorgang SET notizen=? "
					+ "WHERE patientId=? AND datum=?");
			notizen = notizen_temp + "," + notizen;
			prep.setString(1, notizen);
			prep.setInt(2, _id);
			prep.setDate(3, sqlDate);
			prep.executeUpdate();
		} catch (SQLException e) {
			throw new StoreException(e);
		} 
		complete();
	}

}
