package edu.saic.twodfire.util;

import java.sql.*;

public class DBUtil {
    private static String drvstr = "org.hsqldb.jdbcDriver";
    private static String dburl = "jdbc:hsqldb:hsql://localhost/";
    //private static String user = "jhyeh";
    //private static String pass = "F122794123";

    public static Connection getDBConnection() {
        Connection _conn = null;
        try {
            Class.forName(drvstr);
            //_conn = DriverManager.getConnection(dburl, user, pass);
            _conn = DriverManager.getConnection(dburl);
            // Print all warnings
            for (SQLWarning warn = _conn.getWarnings(); warn != null; 
                    warn = warn.getNextWarning()) {
                System.err.println("DBUtil - SQL Warning:") ;
                System.err.println("DBUtil - State  : " + warn.getSQLState()) ;
                System.err.println("DBUtil - Message: " + warn.getMessage()) ;
                System.err.println("DBUtil - Error  : " + warn.getErrorCode()) ;
            }
            return _conn;				
	} catch (Exception e) {
            System.err.println("DBUtil panic: error during DB initialization, "+e);
            e.printStackTrace();
	}
	return null;
    }
}
