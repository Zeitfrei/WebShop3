package de.unidue.inf.is.stores;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.domain.Medikament;
import de.unidue.inf.is.utils.DBUtil;

public class ApothekeStore implements Closeable {

	private Connection connection;
	private boolean complete;
	private List<Medikament> mediList;
	
	public ApothekeStore() {
		try {
			connection = DBUtil.getConnection("hospital");
			connection.setAutoCommit(false);
			mediList = new ArrayList<Medikament>();
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

	public List<Medikament> getAllMeds() {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Medikament");
			rs = prep.executeQuery();
			synchronized(mediList) {
				while (rs.next()) {
					mediList.add(new Medikament(rs.getInt("pzn"), rs.getString("name"), rs.getInt("packungsgroesse"), rs.getInt("mindestbestand"), rs.getInt("lagerbestand"), rs.getString("vonPharmafirmaName")));
					
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
		return mediList;
	}

	public boolean entnehmeMedikament(String pzn, String anzahl) {
		synchronized(mediList) {
		mediList = getAllMeds();
		}
		int _pzn = Integer.parseInt(pzn);
		int _anzahl = -1;
		for (int i=0; i<mediList.size(); i++) {
			if (_pzn == mediList.get(i).getPzn()) {
				_anzahl = mediList.get(i).getLagerbestand() - Integer.parseInt(anzahl);
				break;
			}
		}
		if (_anzahl >= 0) {
			try {
				PreparedStatement prepStatement = connection.prepareStatement("UPDATE Medikament SET lagerbestand = ? WHERE pzn = ?");
				prepStatement.setInt(1, _anzahl);
				prepStatement.setInt(2, _pzn);
				prepStatement.executeUpdate();
			} catch (SQLException e) {
				throw new StoreException(e);
			}
			complete();
			return true;
		} else {
			complete();
			return false;
		}
	}
	
	public boolean checkMedikament(int pzn1, int pzn2) {
		ResultSet rs = null;
				try {
					PreparedStatement prep = connection.prepareStatement("SELECT wirkstoffaname, wirkstoffbname "
							+ "FROM wirkstoffInWechselwirkung WHERE "
							+ "(wirkstoffaname IN (SELECT wirkstoffname FROM wirkstoffInMedikament WHERE medikamentpzn=?) AND "
							+ "wirkstoffbname IN (SELECT wirkstoffname FROM wirkstoffInMedikament WHERE medikamentpzn=?)) OR "
							+ "(wirkstoffaname IN (SELECT wirkstoffname FROM wirkstoffInMedikament WHERE medikamentpzn=?) AND "
							+ "wirkstoffbname IN (SELECT wirkstoffname FROM wirkstoffInMedikament WHERE medikamentpzn=?))");
					
					/* A&B v B&A */
					prep.setInt(1, pzn1);
					prep.setInt(2, pzn2);
					prep.setInt(3, pzn2);
					prep.setInt(4, pzn1);
					rs = prep.executeQuery();
					if (rs.next()) {
						complete();
						return false;
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
		return true;
	}
	
	public List<Medikament> findMedikament(String diagnose) {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Medikament WHERE pzn IN (SELECT medikamentPzn FROM wirkstoffInMedikament WHERE wirkstoffName IN (SELECT wirkstoffName FROM wirkstoffWirktBeiKrankheit WHERE krankheitName = ?))");
			prep.setString(1, diagnose);
			rs = prep.executeQuery();
			synchronized(mediList) {
				while (rs.next()) {
					mediList.add(new Medikament(rs.getInt("pzn"), rs.getString("name"), rs.getInt("packungsgroesse"), rs.getInt("mindestbestand"), rs.getInt("lagerbestand"), rs.getString("vonPharmafirmaName")));
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
		return mediList;
	}
}