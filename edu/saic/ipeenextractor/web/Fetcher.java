package edu.saic.ipeenextractor.web;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class Fetcher {
	private String baseurl = "http://www.ipeen.com.tw/search/taiwan/000/1-0-0-0/?p=";
	private String outdir = "";
	private int pages;
	private static int REPORTED_REC=110156;
	private static int PAGE_SIZE=15;
	
	public static void main(String[] args) {
		try {
			int rec_size = Integer.parseInt(args[0]);
			Fetcher f = new Fetcher(rec_size, args[1]);
			f.run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public Fetcher() {
		// total 7344 pages
		this(REPORTED_REC, PAGE_SIZE, ""); 
	}
	
	public Fetcher(int size, String s) {
		this(size, PAGE_SIZE, s); 
	}

	public Fetcher(int num, int pagesize, String s) {
		this.pages = num/pagesize + 1;
		this.outdir = s.endsWith("/")?s:s+"/";
	}
	
	public void run() {
		for (int i=1; i<=pages; i++) {
			// run through pages
			// 1. fetch page
			String urlstr = baseurl+i;
			System.err.println("Fetching "+urlstr);
			String fname = doFetch(urlstr);
			// 2. extract basic shop info, fill in data
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
	}
	
	public String doFetch(String urlstr) {
        try {
            URL url = new URL(urlstr);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String fname = outdir+System.currentTimeMillis()+".txt";
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname), "utf-8"));
            String line = "";
            while ((line=br.readLine()) != null) {
                pw.println(line);
            }
            br.close();
            pw.close();
            return fname;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

}
