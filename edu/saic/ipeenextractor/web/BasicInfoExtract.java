package edu.saic.ipeenextractor.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.saic.ipeenextractor.ShopInfo;

public class BasicInfoExtract {
	private String rawDir, outDir;
	private HashMap<String, ShopInfo> data;
	private String patt1 = "<article class=\"serItem\"";
	private String patt2 = "</article>";
	
	public static void main(String[] args) {
		//String prefix = "/home/jhyeh/00MyDesktop/workspace/iPeenExtractor/data/";
		String prefix = args[0].endsWith("/")?args[0]:args[0]+"/";
		String raw = prefix+args[1];
		raw = raw.endsWith("/")?raw:raw+"/";
		String out = prefix;
		System.err.println("Raw data directory: "+raw);
		BasicInfoExtract bie = new BasicInfoExtract(raw, out);
		bie.run();
	}
	
	public BasicInfoExtract(String s1, String s2) {
		this.rawDir = s1;
		this.outDir = s2;
		this.data = new HashMap<String, ShopInfo>();
	}
	
	public void run() {
		File dir = new File(rawDir);
		File[] flist = dir.listFiles();
		for (File f: flist) {
			try {
				System.err.println("Extract: "+f.getName());
				// doExtract() will add object into this.data
				doExtract(f);
			} catch (Exception e) {
				System.err.println("Parsing error: "+f.getName());
				e.printStackTrace(System.err);
			}
		}
		// dump data, or fetch deep shop info
		String outfname = this.outDir+"output-simple.csv";
		System.err.println("Data dump to: "+outfname);
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfname), "utf-8"));
			for (ShopInfo info: this.data.values()) {
				pw.println(info.toStringSimple());
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public void doExtract(File f) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
		String line;
		ArrayList<String> al = new ArrayList<String>();
		boolean rec_begin = false;
		while ((line=br.readLine()) != null) {
			if (line.indexOf(patt1) >= 0) {
				rec_begin = true;
				continue;
			}
			if (line.indexOf(patt2) >= 0) {
				rec_begin = false;
				ShopInfo info = parseRecord(al);
				//System.err.println("Fetched id: "+info.getId());
				if (!info.isBroken()) {
					this.data.put(info.getId(), info);
					//System.err.println(info);
				}
				al = new ArrayList<String>();
			}
			if (rec_begin) {
				al.add(line);
			}
			
		}
		br.close();
	}
	
	public String extractFieldSimple(ArrayList<String> al, String patt1, String patt2) {
		for (String s: al) {
			int pos1 = s.indexOf(patt1);
			if (pos1 >= 0) {
				//System.err.println("find pos2 from position "+(pos1+patt1.length())+", substring="+s.substring(pos1+patt1.length()));
				int pos2 = s.indexOf(patt2, pos1+patt1.length());
				if (pos2 >= 0) {
					String substr = s.substring(pos1+patt1.length(), pos2);
					return substr;
				}
				//System.err.println("pos1="+pos1+",pos2="+pos2);
			}
		}
		return "N/A";
	}
	
	public ShopInfo parseRecord(ArrayList<String> al) {
		// target: 店名，電話，地址，評估價，餐飲類別一，餐飲類別二，均消，網址，介紹，坐標x,y，時間，合作店家，瀏覽數，收藏數，分享數
		// find id: <div class="serShop" id="shop_row_67012">
		// find url & name: <a href="http://www.ipeen.com.tw/shop/67012-%E8%9E%BA%E7%B5%B2%E7%91%AA%E8%8E%89-Rose-Mary" target="_blank" class="a37 ga_tracking" data-category="search" data-action="shop_1" data-label="店名">螺絲瑪莉 Rose Mary</a>
		// find tel: <label>電話：</label> \n 暫無資料
		// find addr: <label>地址：</label> \n \n <span style="padding-left:3em;">台北市中山區南京西路12巷13弄9號
		// find rating: <img src="%E5%8F%B0%E7%81%A3%E7%BE%8E%E9%A3%9F%E5%A4%A7%E6%90%9C%E6%9F%A5%20%EF%BC%8D%20iPeen%20%E6%84%9B%E8%A9%95%E7%B6%B2%E7%BE%8E%E9%A3%9F%E9%A0%BB%E9%81%93_files/icon_star55.png" class="star" alt="綜合評價：55" title="綜合評價：55">
		// find category: <a href="http://www.ipeen.com.tw/search/taiwan/000/1-0-25-0/" class="ga_tracking" data-category="search" data-action="shop_1" data-label="大分類">異國料理</a>&nbsp;/&nbsp;<a href="http://www.ipeen.com.tw/search/taiwan/000/1-0-25-42/" class="ga_tracking" data-category="search" data-action="shop_1" data-label="小分類">義式料理</a>
		// find spent: 本店均消&nbsp;269&nbsp;元
		// find close: <span class="status">【已歇業】</span>
		// other info are available at detail page (the url)
		
		ShopInfo info = new ShopInfo();
		// id
		String str = extractFieldSimple(al, "<div class=\"serShop\" id=\"shop_row_", "\"");
		info.setId(str);
		//System.err.println("Fetched id: "+str);
		// url
		for (String s: al) {
			String patt0 = "data-label=\"店名\"";
			String patt1 = "<a href=\"";
			String patt2 = "\" target=\"_blank\"";
			String prefix = "http://www.ipeen.com.tw";
			int pos0 = s.indexOf(patt0);
			if (pos0 >= 0) {
				int pos1 = s.indexOf(patt1);
				int pos2 = s.indexOf(patt2, pos1);
				String substr = prefix+s.substring(pos1+patt1.length(), pos2);
				info.setUrl(substr);
				break;
			}
		}
		// name
		str = extractFieldSimple(al, "data-label=\"店名\">", "</a>");
		str = str.replaceAll(",", "，");
		info.setName(str);
		//System.err.println("Fetched name: "+str);
		// tel
		for (Iterator<String> it=al.iterator(); it.hasNext(); ) {
			String s = it.next();
			String patt0 = "<label>電話：</label>";
			String patt1 = "TEL/phone_pic.php?si=";
			String patt2 = "\" alt=\"電話號碼\" title=\"電話號碼\"";
			int pos0 = s.indexOf(patt0);
			if (pos0 >= 0) {
				s = it.next();
				int pos1 = s.indexOf(patt1);
				if (pos1 >= 0) {
					int pos2 = s.indexOf(patt2, pos1);
					if (pos2 >= 0) {
						String substr = s.substring(pos1+patt1.length(), pos2);
						info.setTel(substr);
						break;
					}
				}
			}
		}
		// addr
		for (Iterator<String> it=al.iterator(); it.hasNext(); ) {
			String s = it.next();
			String patt0 = "<label>地址：</label>";
			int pos0 = s.indexOf(patt0);
			if (pos0 >= 0) {
				it.next();
				s = it.next();
				String addr = "";
				if (s.indexOf("店家已搬遷") >= 0) { // shop moved
					String patt1 = "店家已搬遷至<a href=\"";
					String patt2 = "\"";
					int pos1 = s.indexOf(patt1);
					int pos2 = s.indexOf(patt2, pos1+patt1.length());
					addr = s.substring(pos1+patt1.length(), pos2).trim();
					addr = "已搬遷("+addr+")";
				}
				else {
					String patt1 = "<span style=\"padding-left:3em;\">";
					int pos1 = s.indexOf(patt1);
					addr = s.substring(pos1+patt1.length()).trim();
					addr = addr.replaceAll(",", "，");
				}
				info.setAddr(addr);
				break;
			}
		}
		// rating
		str = extractFieldSimple(al, "title=\"綜合評價：", "\">");
		info.setRating(str);
		// category1
		str = extractFieldSimple(al, "data-label=\"大分類\">", "</a>");
		info.setCategory1(str);
		// category2
		str = extractFieldSimple(al, "data-label=\"小分類\">", "</a>");
		info.setCategory2(str);
		// spent
		str = extractFieldSimple(al, "本店均消&nbsp;", "&nbsp;元");
		info.setSpent(str);
		// close
		str = extractFieldSimple(al, "<span class=\"status\">", "</span>");
		if (str.indexOf("已歇業") >= 0) info.setClose(1);
		
		return info;
	}

}
