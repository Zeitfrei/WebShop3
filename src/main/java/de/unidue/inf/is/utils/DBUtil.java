package de.unidue.inf.is.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.ibm.db2.jcc.DB2Driver;



public final class DBUtil {

    private DBUtil() {
    }


    static {
        com.ibm.db2.jcc.DB2Driver driver = new DB2Driver();
        try {
            DriverManager.registerDriver(driver);
        }
        catch (SQLException e) {
            throw new Error("Laden des Datenbanktreiber nicht möglich");
        }
    }


    public static Connection getConnection(String database) throws SQLException {
        
    	// @home
    	Properties properties = new Properties();
    	properties.setProperty("securityMechanism", Integer.toString(com.ibm.db2.jcc.DB2BaseDataSource.USER_ONLY_SECURITY));
    	properties.setProperty("user", "dbp12");
    	properties.setProperty("password", "ioshie3g");
    	
    	final String url = "jdbc:db2://cressida.is.inf.uni-due.de:50012/" + database + ":currentSchema=DBP12;";
    	
    	// @local
    	//return DriverManager.getConnection(url);
    	
    	//@home
    	return DriverManager.getConnection(url, properties);
    }

}
