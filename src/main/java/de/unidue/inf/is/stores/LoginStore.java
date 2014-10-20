package de.unidue.inf.is.stores;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;

public class LoginStore implements Closeable {

    private Connection connection;
    private boolean complete;
    
    public LoginStore() throws StoreException {
    	try {
            connection = DBUtil.getConnection("hospital");
            connection.setAutoCommit(false);
        }
        catch (SQLException e) {
            throw new StoreException(e);
        }
	}
    
    public List<User> getUserlist() {
    	List<User> userlist = new ArrayList<>();
    	ResultSet rs = null;
    	try {
    		PreparedStatement prep = connection.prepareStatement("SELECT login FROM Arzt");
    		rs = prep.executeQuery();
    		while(rs.next()) {
    			userlist.add(new User(rs.getString("login")));
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
    	return userlist;
    }
    
	public boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie[] cookies = request.getCookies();
		List<User> userlist = getUserlist();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				for (int i=0;i<userlist.size(); i++) {
					if (cookie.getName().equals("login") && cookie.getValue().equals(userlist.get(i).getUsername())) return true;
				}
			}
		}
		complete();
		response.sendRedirect("/login");	
		return false;
	}
    
    public boolean checkLogin(String login, String password) throws StoreException {
    	String _password = "";
    	ResultSet rs = null;
    	try {
    		PreparedStatement prep = connection.prepareStatement("SELECT login,pw FROM Arzt");
    		rs = prep.executeQuery();
    		while(rs.next()) {
    			String _login = rs.getString("login");
    			if (_login.equals(login)) {
    				_password = rs.getString("pw");
    	    		break;
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
    	if (_password.equals(password)) {
    		complete();
    		return true;
    	}
    	complete();
    	return false;
    }
    
    public void complete() {
        complete = true;
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

}
