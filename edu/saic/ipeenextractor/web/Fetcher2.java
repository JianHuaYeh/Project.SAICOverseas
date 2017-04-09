package edu.saic.ipeenextractor.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import edu.saic.ipeenextractor.ShopInfo;

public class Fetcher2 {
	private String infname;
	private String outDir;

	public static void main(String[] args) {
		//String str = "/home/jhyeh/workspace/iPeenExtractor/data/output-simple.csv";
		//String out = "/home/jhyeh/workspace/iPeenExtractor/data/raw2/";
		// how to run in command line:
		// java edu.saic.ipeenextractor.web.Fetcher2 [CSV file] [output dir]
		String str = args[0];
		String out = args[1].endsWith("/")?args[1]:args[1]+"/";
		Fetcher2 f2 = new Fetcher2(str, out);
		f2.run();
	}
	
	public Fetcher2(String s1, String s2) {
		this.infname = s1;
		this.outDir = s2;
	}
	
	public Set loadDoneList() {
		TreeSet<String> ts = new TreeSet<String>();
		try {
			File dir = new File(outDir);
			String[] flist = dir.list();
			int count=0;
			for (String s: flist) {
				if (s.endsWith(".html")) {
					StringTokenizer st = new StringTokenizer(s, ".");
					ts.add(st.nextToken());
					count++;
				}
			}
			System.err.println("Currently "+count+" files downloaded.");
		} catch (Exception e) {
			System.err.println("Error loading downloaded list.");
			ts = new TreeSet<String>();
		}
		return ts;
	}
	
	public void run() {
		Set doneSet = loadDoneList();
		HashMap<String, ShopInfo> data = ShopInfo.importFromSimpleCSV(infname);
		for (ShopInfo info: data.values()) {
			String id = info.getId();
			if (doneSet.contains(id)) continue;
			String url = info.getUrl();
			String outfname = outDir+id+".html";
			doFetch(url, outfname);
			System.err.println(outfname+" fetched.");
			//System.out.println(id+".html");
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
	}
	
	public String doFetch(String urlstr, String fname) {
        try {
            URL url = new URL(urlstr);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            PrintWriter pw = new PrintWriter(new FileOutputStream(fname));
            String line = "";
            while ((line=br.readLine()) != null) {
                pw.println(line);
            }
            br.close();
            pw.close();
            return fname;
        } catch (java.net.MalformedURLException e) {
        	System.err.println("MalformedURLException, fname(id)="+fname);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
	
}
