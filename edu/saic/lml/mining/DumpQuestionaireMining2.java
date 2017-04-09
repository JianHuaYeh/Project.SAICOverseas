package edu.saic.lml.mining;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DumpQuestionaireMining2 {

	public static void main(String[] args) {
		DumpQuestionaireMining2 dump = new DumpQuestionaireMining2();
		dump.doDump();
	}
	
	public void doDump() {
		String fname = "fulong.over18.2.txt";
		String outfname = fname+".out";
		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));
			PrintWriter pw = new PrintWriter(new FileOutputStream(outfname));
			String line="";
			ArrayList<String> headers = findHeaders(br.readLine());
			int count=1;
			while ((line=br.readLine()) != null) {
				String str = convertData(line, headers);
				if (!str.equals(""))
					pw.println("rec"+count+","+str);
				count++;
			}
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public String doConvert(String tok, int idx, ArrayList<String> headers) {
		if (idx == 0) return "";
		String val = tok;
		if (val.equals("0")) return "";
		try {
			String header = headers.get(idx);
			if (header.indexOf("��酉") >= 0) return "";
			return headers.get(idx)+"-"+val;
		} catch (Exception e) {
			return "";
		}
		/*switch (idx) {
			case 0: 
			case 2:
			case 4:
				return ""; // skip
			case 5:
				// 103 mean age
				int cell = Integer.parseInt(tok);
				cell /= 5;
				val = "age"+cell;
				return val;
			case 7:
			case 8:
				// expense
				cell = Integer.parseInt(tok);
				cell /= 100;
				val = "expense"+cell;
				return val;
			case 13:
				// stopping time
				cell = Integer.parseInt(tok);
				cell /= 30;
				val = "time"+cell;
				return val;
			case 16:
				// number of company
				cell = Integer.parseInt(tok);
				cell /= 10;
				val = "company-"+cell;
				return val;
			default: return headers.get(idx)+"-"+val;
		}*/
	}
	
	public String convertData(String line, ArrayList<String> headers) {
		String result = "";
		StringTokenizer st = new StringTokenizer(line, "\t");
		int count = 0;
		// skip the first id column
		while (st.hasMoreTokens()) {
			String tmp = doConvert(st.nextToken().trim(), count, headers);
			if (!tmp.equals("")) result += tmp+",";
			count++;
		}
		if (result.length() > 0) result = result.trim().substring(0,result.length()-1);
		System.err.println(line);
		System.err.println("Convert to: "+result);
		return result;
	}
	
	public ArrayList<String> findHeaders(String line) {
		ArrayList<String> result = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(line, "\t");
		while (st.hasMoreTokens()) {
			result.add(st.nextToken().trim());
		}
		return result;
	}
}
