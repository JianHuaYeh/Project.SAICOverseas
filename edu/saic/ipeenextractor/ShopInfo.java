package edu.saic.ipeenextractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ShopInfo {
	// basic info
	private String id;
	private String name;
	private String tel;
	private String addr;
	private String url;
	private String rating;
	private String category1;
	private String category2;
	private String spent;
	private int close; // 1: close, 0: alive
	// full info
	private String[] ratingtag;
	private int[] ratingval;
	private int articles;
	private int bookmarks;
	private int views;
	private double latitude;
	private double longitude;
	
	public ShopInfo() {
		ratingtag = new String[3];
		ratingval = new int[3];
	}
	
	public String toString() {
		return this.toStringFull();
	}
	
	public String toStringSimple() {
		String name2=(name==null)?"N/A":name;
		String tel2=(tel==null)?"N/A":tel;
		String addr2=(addr==null)?"N/A":addr;
		String url2=(url==null)?"N/A":url;
		String rating2=(rating==null)?"N/A":rating;
		String category12=(category1==null)?"N/A":category1;
		String category22=(category2==null)?"N/A":category2;
		String spent2=(spent==null)?"N/A":spent;
		String result = id+","+name2+","+tel2+","+addr2+","+url2+","+rating2+",";
		result += category12+","+category22+","+spent2+","+close;
		
		return result;
	}
	
	public String toStringFull() {
		String result = toStringSimple()+",";
		
		for (int i=0; i<ratingtag.length; i++) {
			if (ratingtag[i] == null) {
				result += "N/A,0,";
			}
			else result += ratingtag[i]+","+ratingval[i]+",";
		}
		result += articles+","+bookmarks+","+views+","+latitude+","+longitude;
		
		return result;
	}
	
	public boolean isBroken() {
		return id.equals("N/A")?true:false;
	}
	
	public static ShopInfo parseSimpleCSV(String line) {
		// sample
		// 75595,紀香無骨炸雞,N/A,新北市中和區連城路467號,http://www.ipeen.com.tw/shop/75595-紀香無骨炸雞,45,小吃,其他小吃,53
		StringTokenizer st = new StringTokenizer(line, ",");
		ShopInfo info = new ShopInfo();
		if (st.hasMoreTokens()) info.setId(st.nextToken());
		if (st.hasMoreTokens()) info.setName(st.nextToken());
		if (st.hasMoreTokens()) info.setTel(st.nextToken());
		if (st.hasMoreTokens()) info.setAddr(st.nextToken());
		if (st.hasMoreTokens()) info.setUrl(st.nextToken());
		if (st.hasMoreTokens()) info.setRating(st.nextToken());
		if (st.hasMoreTokens()) info.setCategory1(st.nextToken());
		if (st.hasMoreTokens()) info.setCategory2(st.nextToken());
		if (st.hasMoreTokens()) info.setSpent(st.nextToken());
		if (st.hasMoreTokens()) {
			info.setClose(Integer.parseInt(st.nextToken()));
		}
		/*try {
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}*/
		return info;
	}
	
	public static HashMap<String, ShopInfo> importFromSimpleCSV(String str) {
		HashMap<String, ShopInfo> data = new HashMap<String, ShopInfo>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(str));
			String line="";
			while ((line=br.readLine()) != null) {
				try {
					ShopInfo info = ShopInfo.parseSimpleCSV(line);
					data.put(info.getId(), info);
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return data;
	}

	public String getId() {	return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getTel() { return tel; }
	public void setTel(String tel) { this.tel = tel; }
	public String getAddr() { return addr; }
	public void setAddr(String addr) { this.addr = addr; }
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
	public String getRating() { return rating; }
	public void setRating(String rating) { this.rating = rating; }
	public String getCategory1() { return category1; }
	public void setCategory1(String category1) { this.category1 = category1; }
	public String getCategory2() { return category2; }
	public void setCategory2(String category2) { this.category2 = category2; }
	public String getSpent() { return spent; }
	public void setSpent(String spent) { this.spent = spent; }
	public int getClose() { return close; }
	public void setClose(int close) { this.close = close; }	

	public double getLatitude() { return latitude; }
	public void setLatitude(double latitude) { this.latitude = latitude; }
	public double getLongitude() { return longitude; }
	public void setLongitude(double longitude) { this.longitude = longitude; }
	public int getArticles() { return articles; }
	public void setArticles(int articles) { this.articles = articles; }
	public int getBookmarks() { return bookmarks; }
	public void setBookmarks(int bookmarks) { this.bookmarks = bookmarks; }
	public int getViews() {	return views; }
	public void setViews(int views) { this.views = views; }
	public String[] getRatingtag() {	return ratingtag; }
	public void setRatingtag(int i, String rating1tag) { 
		if (i<3 && i>=0) { this.ratingtag[i] = rating1tag; } 
	}
	public int[] getRatingval() { return ratingval; }
	public void setRatingval(int i, int rating1val) { 
		if (i<3 && i>=0) { this.ratingval[i] = rating1val; }
	}

}
