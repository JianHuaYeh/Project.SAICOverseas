package edu.saic.twodfire.mining;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import edu.saic.twodfire.util.DBUtil;

public class DumpMenuMapping {
	
	public static void main(String[] args) {
		DumpMenuMapping dump = new DumpMenuMapping();
		dump.doDump();
	}
	
	public void doDump() {
		Connection conn = DBUtil.getDBConnection();
		try {
			Statement stmt = conn.createStatement();
			String sqlstr = "select id,name from menu";
			ResultSet rs = stmt.executeQuery(sqlstr);
			int len=0;
			while (rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("NAME");
				System.out.println(id+","+name);
				len++;
			}
			rs.close();
			conn.close();
			System.err.println("Total "+len+" records output.");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
