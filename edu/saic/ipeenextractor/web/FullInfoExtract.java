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
import java.util.StringTokenizer;

import edu.saic.ipeenextractor.ShopInfo;

public class FullInfoExtract {
	private String rawDir, outDir, infname;
	private HashMap<String, ShopInfo> data;
	private String patt11 = "<section id=\"shop-metainfo\">";
	private String patt12 = "</section>";
	private String patt21 = "<div id=\"shop-details\">";
	private String patt22 = "<div class=\"shop-guide-bottom\">";
	
	public static void main(String[] args) {
		//String prefix = "/home/jhyeh/00MyDesktop/workspace/iPeenExtractor/data/";
		String prefix = args[0].endsWith("/")?args[0]:args[0]+"/";
		String csv = prefix+"output-simple.csv";
		String raw = prefix+args[1];
		raw = raw.endsWith("/")?raw:raw+"/";
		String out = prefix;
		FullInfoExtract fie = new FullInfoExtract(raw, out, csv);
		fie.run();
	}
	
	public FullInfoExtract(String s1, String s2, String s3) {
		this.rawDir = s1;
		this.outDir = s2;
		this.infname = s3;
		this.data = ShopInfo.importFromSimpleCSV(infname);
	}
	
	public void run() {
		File dir = new File(rawDir);
		String[] flist = dir.list();
		for (String fname: flist) {
			try {
				System.err.println("Extract: "+fname);
				// doExtract() will add object into this.data
				StringTokenizer st = new StringTokenizer(fname, ".");
				String id = st.nextToken().trim();
				if (data.get(id) == null) continue;
				doExtract(id, rawDir+fname);
			} catch (Exception e) {
				System.err.println("Parsing error: "+rawDir+fname);
				e.printStackTrace(System.err);
			}
		}
		// dump data, or fetch deep shop info
		String outfname = this.outDir+"output-full.csv";
		System.err.println("Data dump to: "+outfname);
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfname), "utf-8"));
			for (ShopInfo info: this.data.values()) {
				pw.println(info.toString());
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public void doExtract(String id, String fname) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "utf-8"));
		String line="";
		ArrayList<String> al = new ArrayList<String>();
		boolean rec_begin = false;
		while ((line=br.readLine()) != null) {
			if (line.indexOf(patt11) >= 0) {
				rec_begin = true;
				continue;
			}
			if (line.indexOf(patt12) >= 0) {
				rec_begin = false;
				parseRecord1(id, al);
				al = new ArrayList<String>();
			}
			if (rec_begin) {
				al.add(line);
			}
			
		}
		br.close();
		
		// part2
		br = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
		line="";
		al = new ArrayList<String>();
		rec_begin = false;
		while ((line=br.readLine()) != null) {
			if (line.indexOf(patt21) >= 0) {
				rec_begin = true;
				continue;
			}
			if (line.indexOf(patt22) >= 0) {
				rec_begin = false;
				parseRecord2(id, al);
				al = new ArrayList<String>();
			}
			if (rec_begin) {
				al.add(line);
			}
			
		}
		br.close();
	}
	
	public String getFieldString(String pt1, String pt3, String pt4, ArrayList<String> al) {
		for (Iterator<String> it=al.iterator(); it.hasNext();) {
			String s = it.next();
			int pos1 = s.indexOf(pt1);
			if (pos1 >= 0) {
				s = it.next();
				int pos3 = s.indexOf(pt3);
				if (pos3 >= 0) {
					int pos4 = s.indexOf(pt4, pos3+pt3.length());
					if (pos4 >= 0) {
						String valstr = s.substring(pos3+pt3.length(), pos4).trim();
						return valstr;
					}
				}
			}
		}
		return null;
	}
	
	public void parseRecord1(String id, ArrayList<String> al) {
		ShopInfo info = this.data.get(id);
		if (info == null) return;
		
		String pt1 = "<dt>";
		String pt2 = "</dt>";
		String pt3 = "<meter value=\"";
		String pt4 = "\"";
		// parse for: several ratings(美味度...), articles, bookmarks, views, openhours
		// extract 3 ratings
		int i=0;
		for (Iterator<String> it=al.iterator(); it.hasNext();) {
			String s = it.next();
			if (i >= 3) break;
			int pos1 = s.indexOf(pt1);
			if (pos1 >= 0) {
				//System.err.println("find pos2 from position "+(pos1+patt1.length())+", substring="+s.substring(pos1+patt1.length()));
				int pos2 = s.indexOf(pt2, pos1+pt1.length());
				if (pos2 >= 0) {
					String tag = s.substring(pos1+pt1.length(), pos2);
					// now fetch value
					boolean done=false;
					while (!done) {
						s = it.next();
						int pos3 = s.indexOf(pt3);
						if (pos3 >= 0) {
							int pos4 = s.indexOf(pt4, pos3+pt3.length());
							if (pos4 >= 0) {
								String valstr = s.substring(pos3+pt3.length(), pos4).trim();
								int val = Integer.parseInt(valstr);
								info.setRatingtag(i, tag);
								info.setRatingval(i, val);
								done=true;
							}
						}
					}
				}
				i++;
			} //if (pos1 >= 0)
		} // end fetch 3 ratings
		
		//<dt>分享文數</dt>
		//<dd>0 則</dd>
		pt1 = "<dt>分享文數</dt>";
		pt3 = "<dd>";
		pt4 = "則";
		String s = getFieldString(pt1, pt3, pt4, al);
		if (s != null) {
			Integer val = Integer.parseInt(s);
			info.setArticles(val);
		}
				
		//<dt>收藏數</dt>
		//<dd>0 人</dd>
		pt1 = "<dt>分享文數</dt>";
		pt3 = "<dd>";
		pt4 = "則";
		s = getFieldString(pt1, pt3, pt4, al);
		if (s != null) {
			Integer val = Integer.parseInt(s);
			info.setBookmarks(val);
		}

		//<dt>瀏覽數</dt>
		//<dd>4698 次</dd>
		pt1 = "<dt>瀏覽數</dt>";
		pt3 = "<dd>";
		pt4 = "次";
		s = getFieldString(pt1, pt3, pt4, al);
		if (s != null) {
			Integer val = Integer.parseInt(s);
			info.setViews(val);
		}
	}
	
	public void parseRecord2(String id, ArrayList<String> al) {
		ShopInfo info = this.data.get(id);
		if (info == null) return;
		
		// parse for: coords
		String pt1 = "<a class=\"whole-map\"";
		String pt3 = "/c=";
		String pt4 = "/z=";
		
		for (String s: al) {
			int pos1 = s.indexOf(pt1);
			if (pos1 >= 0) {
				int pos3 = s.indexOf(pt3);
				if (pos3 >= 0) {
					//System.err.println("find pos2 from position "+(pos1+patt1.length())+", substring="+s.substring(pos1+patt1.length()));
					int pos4 = s.indexOf(pt4, pos3+pt3.length());
					if (pos4 >= 0) {
						String substr = s.substring(pos3+pt3.length(), pos4);
						// now we get something like "25.139645,121.793116"
						StringTokenizer st = new StringTokenizer(substr, ",");
						Double lat = Double.parseDouble(st.nextToken());
						Double lon = Double.parseDouble(st.nextToken());
						info.setLatitude(lat);
						info.setLongitude(lon);
					}
				}
			}
		}
		
		
	}
	

}

/* part1: shop metainfo
   <section id="shop-metainfo">
      <dl class="rating">
        <dt>美味度</dt>
        <dd>
          <span class="score-bar small-red">
            <meter value="0" min="0" max="20"></meter>
            <i style="width: 0%"></i>
          </span>
        </dd>
        <dt>服務品質</dt>
        <dd>
          <span class="score-bar small-red">
            <meter value="0" min="0" max="20"></meter>
            <i style="width: 0%"></i>
          </span>
        </dd>
        <dt>環境氣氛</dt>
        <dd>
          <span class="score-bar small-red">
            <meter value="0" min="0" max="20"></meter>
            <i style="width: 0%"></i>
          </span>
        </dd>
      </dl>
    
      <dl class="info">
                  <dt>本店均消</dt>
          <dd>暫無資料</dd>
                <dt>分享文數</dt>
        <dd>0 則</dd>
        <dt>收藏數</dt>
        <dd>0 人</dd>
        <dt>瀏覽數</dt>
        <dd>4698 次</dd>
        
                                  
                  <dt class="no-break">營業時間</dt>
          <dd>
            <span>暫無提供</span>
          </dd>
                
                  <dt class="no-break">公休日</dt>
          <dd>
            <span>暫無提供</span>
          </dd>
              </dl>
      
      <div class="actions">
        <a class="button large ga_tracking" data-category="WEB_shop" data-action="left_error" data-label="左側_店家資訊報錯/更新" href="/report/?type=3&amp;v=s&amp;to_id=10001">店家資訊報錯/更新</a>
                            <a id="add-route-button" class="i button large ga_tracking" data-category="WEB_shop" data-action="left_schedule" data-label="左側_加入行程" href="#">加入行程</a>
              </div>
    </section>
 */

/* part2: shop detail
 <div id="shop-details">
        <table>
        <tr>
          <th>商家名稱</th>
          <td>大將涮涮鍋</td>
        </tr>
        <tr>
          <th>商家分類</th>
          <td class="cate">
            <a href="/search/all/000/1-100-0-0/" class="ga_tracking" data-category="WEB_shop" data-action="details_shop_channel_classify" data-label="詳細資訊_頻道分類">美食</a>
            <span>&gt;</span>
            <a href="/search/taipei/000/1-0-0-0/" class="ga_tracking" data-category="WEB_shop" data-action="details_shop_doublebig_classify" data-label="詳細資訊_大大分類">美食店家</a>
            <span>&gt;</span>
            <a href="/search/taipei/000/1-0-21-0/" class="ga_tracking" data-category="WEB_shop" data-action="details_shop_big_classify" data-label="詳細資訊_大分類">鍋類</a>
            <span>&gt;</span>
            <a href="/search/taipei/000/1-0-21-25/" class="ga_tracking" data-category="WEB_shop" data-action="details_shop_small_classify" data-label="詳細資訊_小分類">涮涮鍋</a>
          </td>
        </tr>
                      <tr>
            <th>電話</th>
            <td>02-27544905</td>
          </tr>
                                  <tr>
            <th>地址</th>
            <td>台北市大安區復興南路二段148巷37號                                     &nbsp;<a class="whole-map" target="_blank" href="/map/#!/s=0/f=0|0|0|0|0|10|0|0|0/c=25.028370320472806,121.54167493857881/z=18/sid=10001/">(觀看地圖)</a>
                              </td>
          </tr>
                    
                      <tr>
            <th>捷運資訊</th>
            <td>
              近台北市捷運&nbsp;-&nbsp;文山內湖線&nbsp;-&nbsp;科技大樓站&nbsp;(步行約7分鐘)<br />                </td>
          </tr>
        
        
        
        
                                       
        
        
        
                                <tr>
          <th>更新時間</th>
          <td>2011/11/22</td>
        </tr>
                  </table>
      <p class="actions">
        <a class="button red ga_tracking" data-category="WEB_shop" data-action="details-error" data-label="詳細資訊店家資訊報錯/更新" href="/report/?type=3&amp;v=s&amp;to_id=10001">店家資訊報錯/更新</a>
                    &nbsp;<a class="button red" href="/ad/adipeen.php?id=ee6be8c5d7721ee004635d98d9f6be89">加入合作店家，編輯更多資訊</a>
                  </p>
			</div>
			<div class="recommend">
				<h2>推 薦 菜</h2>
				<ul>
        					</ul>
			</div>
			<div class="tags">
				<h2>分類標籤</h2>
				<ul>
        <li><a href="/search/all/000/0-100-0-0/綜合鍋類/"  class='ga_tracking' data-category='WEB_shop' data-action='details-tags' data-label="綜合鍋類">綜合鍋類(1)</a></li><li><a href="/search/all/000/0-100-0-0/涮涮鍋/"  class='ga_tracking' data-category='WEB_shop' data-action='details-tags' data-label="涮涮鍋">涮涮鍋(1)</a></li>					</ul>
			</div>
		</section> 
*/