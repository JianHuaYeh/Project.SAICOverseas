package edu.saic.lml.mining;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

public class MergeDump {
	private HashMap<String, String> data1;
	private HashMap<String, String> data2;
	private String outfname;
	
	public static void main(String[] args) {
		String fname1 = "fulong.over18.txt.out";
		String fname2 = "fulong.over18.2.txt.out";
		String fname3 = "fulong.over18.all.txt";
		MergeDump md = new MergeDump(fname1, fname2, fname3);
		md.doMerge();
	}
	
	public void doMerge() {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(this.outfname));
			for (String key: this.data1.keySet()) {
				String str1 = this.data1.get(key);
				String str2 = this.data2.get(key);
				if (str2 != null) {
					pw.println(key+","+str1+str2);
				}
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public MergeDump(String s1, String s2, String s3) {
		try {
			this.data1 = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(new FileReader(s1));
			String line="";
			while ((line=br.readLine()) != null) {
				int pos = line.indexOf(",");
				if (pos >= 0) {
					String id = line.substring(0, pos);
					String data = line.substring(pos+1);
					this.data1.put(id, data);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
			this.data2 = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(new FileReader(s2));
			String line="";
			while ((line=br.readLine()) != null) {
				int pos = line.indexOf(",");
				if (pos >= 0) {
					String id = line.substring(0, pos);
					String data = line.substring(pos);
					this.data2.put(id, data);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		this.outfname = s3;
	}

}
