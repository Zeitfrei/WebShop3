package de.unidue.inf.is.stores;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.domain.Pharmafirma;
import de.unidue.inf.is.domain.Studie;
import de.unidue.inf.is.utils.DBUtil;

public class NeueStudieStore implements Closeable {

	private Connection connection;
	private boolean complete;
	private List<Pharmafirma> pharmaList;
	private List<Studie> studiList;
	
	public NeueStudieStore() {
		try {
			connection = DBUtil.getConnection("hospital");
			connection.setAutoCommit(false);
			pharmaList = new ArrayList<Pharmafirma>();
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

	public List<Pharmafirma> getAllPharmas() {
		ResultSet rs = null;
		try {
			PreparedStatement prep = connection.prepareStatement("SELECT * FROM Pharmafirma");
			rs = prep.executeQuery();
			synchronized(pharmaList) {
				while (rs.next()) {
					pharmaList.add(new Pharmafirma(rs.getString("name")));
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
		return pharmaList;
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

	public void addStudie(String sName, String pName, String aName) {
		
		studiList = getAllStudien();
		String aFirmaName=null;
		for(Studie s : studiList){
			if(s.getName().equals(aName))
				aFirmaName = s.getFirmaName();
		}

		try {
			PreparedStatement prepStatement = 
			connection.prepareStatement("INSERT INTO studie "
					+ "values (?,?)");
			prepStatement.setString(1, sName);
			prepStatement.setString(2, pName);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			throw new StoreException(e);
		}
		
		try {
			PreparedStatement prepStatement = 
			connection.prepareStatement("INSERT INTO studieSchliesstStudieAus "
					+ "VALUES (?,?,?,?)");
			prepStatement.setString(1, pName);
			prepStatement.setString(2, sName);
			prepStatement.setString(3, aFirmaName);
			prepStatement.setString(4, aName);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			throw new StoreException(e);
		}
		complete();
	}
}