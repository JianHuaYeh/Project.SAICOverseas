package edu.saic.twodfire.mining;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import edu.saic.twodfire.util.DBUtil;

public class DumpMenuMining {

	public static void main(String[] args) {
		DumpMenuMining dump = new DumpMenuMining();
		dump.doDump();
	}
	
	public void doDump() {
		Connection conn = DBUtil.getDBConnection();
		try {
			Statement stmt = conn.createStatement();
			String sqlstr = "select id,currdate,totalpayid from orderinfo";
			ResultSet rs = stmt.executeQuery(sqlstr);
			ArrayList<String> oids = new ArrayList<String>();
			ArrayList<java.util.Date> dates = new ArrayList<java.util.Date>();
			ArrayList<String> payids = new ArrayList<String>();
			while (rs.next()) {
				String id = rs.getString("ID");
				oids.add(id);
				java.util.Date da = rs.getDate("CURRDATE");
				dates.add(da);
				String payid = rs.getString("TOTALPAYID");
				payids.add(payid);
			}
			int len=oids.size();
			for (int i=0; i<len; i++) {
				String oid = oids.get(i);
				java.util.Date da = dates.get(i);
				String payid = payids.get(i);
				StringBuilder sb = new StringBuilder();
				Calendar cal = Calendar.getInstance();
				cal.setTime(da);
				String dd = "DD:"+cal.get(Calendar.DAY_OF_MONTH);
				String mm = "MM:"+cal.get(Calendar.MONTH);
				String wd = "WD:"+cal.get(Calendar.DAY_OF_WEEK);
				sb.append(oid).append(",").append(da).append(",").append(mm).append(",").append(dd).append(",").append(wd);
				// incorporate totalpay
				sqlstr = "select sourceamount from totalpay where id='"+payid+"'";
				rs = stmt.executeQuery(sqlstr);
				if (rs.next()) {
					int amount = rs.getInt("SOURCEAMOUNT");
					int amt100 = amount/100;
					String label = "AMT:"+amt100;
					sb.append(",").append(label);
				}
				else {
					String label = "AMT:N/A";
					sb.append(",").append(label);
				}
				// incorporate instance
				sqlstr = "select menuid from instance where orderid='"+oid+"'";
				rs = stmt.executeQuery(sqlstr);
				int count=0;
				while (rs.next()) {
					String muid = rs.getString("MENUID");
					if (muid.trim().equals("0")) continue;
					sb.append(",").append(muid);
					count++;
				}
				if (count > 0)
					System.out.println(sb.toString());
			}
			rs.close();
			conn.close();
			System.err.println("Total "+len+" records output.");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
	}
}
