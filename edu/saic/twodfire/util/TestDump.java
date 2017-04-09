package edu.saic.twodfire.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

public class TestDump {
	private String fname;
	
	public static void main(String[] args) {
		String fname = "/home/jhyeh/Desktop/tmp/2dfire/2dfire.backup";
		TestDump td = new TestDump(fname);
		td.doDump();
	}
	
	public TestDump(String s) {
		this.fname = s;
	}
	
	public String doConvert(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			char ch = s.charAt(i);
			if (Character.isISOControl(ch) || Character.isSpaceChar(ch))
				sb.append(' ');
			else sb.append(ch);
		}
		return sb.toString();
	}
	
	public void doDump() {
		String outfname = fname+".txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));
			PrintWriter pw = new PrintWriter(new FileOutputStream(outfname));
			String line = "";
			while ((line=br.readLine()) != null) {
				if (line.trim().equals("")) continue;
				line = doConvert(line);
				pw.println(line);
			}
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
