package edu.saic.dianpingextractor.web;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class Fetcher {
	private String baseurl = "http://www.dianping.com/shop/";
	private int index;
	private String Citylist[] = {"台湾其他", "台北", "花莲", "高雄", "台南", "桃园", "新北", "台中", "垦丁", "阿里山", "南投"};
	public static void main(String args[]){
		Fetcher f = new Fetcher();
		//f.run(Integer.parseInt(args[0]));
		f.run();
	}
	//public void run(int index){
	public void run(){
		for (int i=8745337; i<=9999999; i++) {
			// run through pages
			// 1. fetch page
			String urlstr = baseurl+i;
			System.err.println("Fetching "+urlstr);
			String fname = doFetch(urlstr, i);

			// 2. extract basic shop info, fill in data
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
	}
	public String doFetch(String urlstr, int index){
		try{
		URL url = new URL(urlstr);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        String fname = "/home/jhyeh/Desktop/tmp/"+index+".txt";
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname), "utf-8"));
        String line = "";
        while ((line=br.readLine()) != null) {
        	/*int pos = -1;        	
        	String template1 = "<title>";
        	if ((pos=line.indexOf(template1)) >= 0) { // found
        		line += br.readLine().trim();
        		pos += template1.length();
                int pos2 = line.indexOf("</title>");
                //System.out.println("pos1="+pos+", pos2="+pos2);
                String check = line.substring(pos, pos2).trim();
                System.out.println(line);
        	}*/
        	pw.println(line);
        }
        br.close();
        pw.close();
        return fname;
		}catch(Exception e){
			 e.printStackTrace();
		}
		return null;
	}
}
