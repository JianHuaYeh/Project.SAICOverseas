package edu.saic.lml.mining;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DumpQuestionaireMining {

	public static void main(String[] args) {
		DumpQuestionaireMining dump = new DumpQuestionaireMining();
		dump.doDump();
	}
	
	public void doDump() {
		String fname = "fulong.over18.txt";
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
	
	// header: ��蝺刻��	撅�	�隞�	�批	撟湔活	103	頨怠��	���鞎�	蝮質�鞎�	1閫������	
	//         2蝳����	3隞�暻潭���	4靘�����	����	5銝餉�暑���	6蝚砍嗾甈�	7���犖�	8��狐���	9�蝔漱��	10���漱��	
	//         11瘣餃��	瘣餃��1	瘣餃��2	瘣餃��3	瘣餃��4	瘣餃��5	瘣餃��6	隤1	隤2	隤3	
	//         隤4	隤5	隤6	隤7	隤8	隤9	隤10	��情1	��情2	��情3	
	//         ��情4	��情5	銵1	銵2	銵3	銵4
	public String doConvert(String tok, int idx, ArrayList<String> headers) {
		String val = tok;
		switch (idx) {
			case 0: 
			case 2:
			case 4:
				return ""; // skip
			case 5:
				// 103 mean age
				int cell = Integer.parseInt(tok);
				cell /= 5;
				val = "age-"+cell;
				return val;
			case 7:
				// expense
				cell = Integer.parseInt(tok);
				cell /= 100;
				val = "expense1-"+cell;
				return val;
			case 8:
				// expense
				cell = Integer.parseInt(tok);
				cell /= 100;
				val = "expense2-"+cell;
				return val;
			/*case 13:
				// stopping time
				cell = Integer.parseInt(tok);
				cell /= 30;
				val = "time"+cell;
				return val;*/
			case 16:
				// number of company
				cell = Integer.parseInt(tok);
				cell /= 10;
				val = "company-"+cell;
				return val;
			default: return headers.get(idx)+"-"+val;
		}
	}
	
	public String convertData(String line, ArrayList<String> headers) {
		String result = "";
		StringTokenizer st = new StringTokenizer(line, "\t");
		int count = 0;
		int minutes = 0;
		while (st.hasMoreTokens()) {
			if (count==13) {
				minutes = Integer.parseInt(st.nextToken().trim());
				if (minutes > 0) result += "time-"+minutes+",";
			}
			else {
				String tmp = doConvert(st.nextToken().trim(), count, headers);
				if (!tmp.equals("")) result += tmp+",";
			}
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
