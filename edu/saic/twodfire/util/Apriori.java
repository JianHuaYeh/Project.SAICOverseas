package edu.saic.twodfire.util;

import java.util.*;

public class Apriori {
	
	public static void main(String[] args) {
		Apriori apriori = new Apriori();
		ArrayList<ArrayList<String>> dataSet = apriori.loadDataSet();
		Object[] objs = apriori.apriori(dataSet, 0.5);
		ArrayList<ArrayList<Set<String>>> L = (ArrayList<ArrayList<Set<String>>>)objs[0];
		HashMap<Set<String>, Double> suppData = (HashMap<Set<String>, Double>)objs[1];
		//for (Set<String> key: suppData.keySet()) {
		//	Double d = suppData.get(key);
		//	System.err.println("Support data: ["+key+","+d+"]");
		//}
		apriori.outputL(L);
		ArrayList<Rule> rules = apriori.generateRules(L, suppData, 0.5);
		System.out.println("Total "+rules.size()+" rules.");
	}
	
	public ArrayList<Rule> generateRules(ArrayList<ArrayList<Set<String>>> L, HashMap<Set<String>, Double> supportData) {
		return generateRules(L, supportData, 0.7);
	}
	
	public ArrayList<Rule> generateRules(ArrayList<ArrayList<Set<String>>> L, HashMap<Set<String>, Double> supportData, double minConf) {
		ArrayList<Rule> bigRuleList = new ArrayList<Rule>();
		for (int i=1; i<L.size(); i++) {
			ArrayList<Set<String>> Li = L.get(i);
			for (Set<String> freqSet: Li) {
				ArrayList<Set<String>> H1 = new ArrayList<Set<String>>();
				for (String item: freqSet) {
					Set<String> s = new TreeSet<String>();
					s.add(item);
					H1.add(s);
				}
				if (i > 1) {
					rulesFromConseq(freqSet, H1, supportData, bigRuleList, minConf);
				}
				else {
					calcConf(freqSet, H1, supportData, bigRuleList, minConf);
				}
			}
		}
		return bigRuleList;
	}
	
	private class Rule {
		public Set<String> freqSet;
		public Set<String> conseq;
		public double conf;
		public Rule(Set<String> freqSet, Set<String> conseq, double conf) {
			this.freqSet = freqSet;
			this.conseq = conseq;
			this.conf = conf;
		}
		public String toString() {
			return freqSet+"-->"+conseq+", conf:"+conf;
		}
	}
	
	public ArrayList<Set<String>> calcConf(Set<String> freqSet, ArrayList<Set<String>> H, 
			HashMap<Set<String>, Double> supportData, ArrayList<Rule> brl, double minConf) {
		ArrayList<Set<String>> prunedH = new ArrayList<Set<String>>();
		for (Set<String> conseq: H) {
			//conf = supportData[freqSet]/supportData[freqSet-conseq]
			try {
				TreeSet<String> freqSet2 = new TreeSet<String>();
				for (String item: freqSet) freqSet2.add(item);
				freqSet2.removeAll(conseq);
				//System.err.println(conseq+"=>"+freqSet2);
				if (supportData.get(conseq) == null) System.err.println("Null support data: "+conseq);
				if (supportData.get(freqSet2) == null) System.err.println("Null support data2: "+freqSet2);
				double conf = supportData.get(freqSet)/supportData.get(freqSet2);
				//System.out.println(freqSet+": conf="+conf);
				if (conf > minConf) {
					Rule rule = new Rule(freqSet2, conseq, conf);
					System.out.println(rule);
					brl.add(rule);
					prunedH.add(conseq);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		return prunedH;
	}
	
	public void rulesFromConseq(Set<String> freqSet, ArrayList<Set<String>> H, 
			HashMap<Set<String>, Double> supportData, ArrayList<Rule> brl, double minConf) {
		int m = H.get(0).size();
		if (freqSet.size() > m+1) {
			ArrayList<Set<String>> Hmp1 = aprioriGen(H, m+1);
			Hmp1 = calcConf(freqSet, Hmp1, supportData, brl, minConf);
			if (Hmp1.size() > 1) {
				rulesFromConseq(freqSet, Hmp1, supportData, brl, minConf);
			}
		}
	}
	
	public ArrayList<String> listHead(ArrayList<String> Lki, int k) {
		ArrayList<String> retList = new ArrayList<String>();
		for (int i=0; i<k; i++) {
			retList.add(Lki.get(i));
		}
		return retList;
	}

	public ArrayList<Set<String>> aprioriGen(ArrayList<Set<String>> Lk, int k) {
		ArrayList<Set<String>> retList = new ArrayList<Set<String>>();
		int lenLk = Lk.size();
		for (int i=0; i<lenLk; i++) {
			for (int j=i+1; j<lenLk; j++) {
				ArrayList<String> Lki = new ArrayList<String>(Lk.get(i));
				ArrayList<String> L1 = listHead(Lki, k-2);
				ArrayList<String> Lkj = new ArrayList<String>(Lk.get(j));
				ArrayList<String> L2 = listHead(Lkj, k-2);
				TreeSet<String> ts1 = new TreeSet<String>(L1);
				TreeSet<String> ts2 = new TreeSet<String>(L2);
				if (ts1.equals(ts2)) {
					Lki.addAll(Lkj);
					retList.add(new TreeSet<String>(Lki));
				}
			}
		}
		return retList;
	}
	
	public Object[] apriori(ArrayList<ArrayList<String>> dataSet, double minSupport) {
		ArrayList<Set<String>> C1 = createC1(dataSet);
		Object[] objs = scanD(dataSet, C1, minSupport);
		ArrayList<Set<String>> L1 = (ArrayList<Set<String>>)objs[0];
		HashMap<Set<String>, Double> supportData = (HashMap<Set<String>, Double>)objs[1];
		ArrayList<ArrayList<Set<String>>> L = new ArrayList<ArrayList<Set<String>>>();
		L.add(L1);
		
		int k=2;
		while (L.get(k-2).size() > 0) {
			ArrayList<Set<String>> Ck = aprioriGen(L.get(k-2), k);
			Object[] objs2 = scanD(dataSet, Ck, minSupport);
			ArrayList<Set<String>> refList = (ArrayList<Set<String>>)objs2[0]; 
			HashMap<Set<String>, Double> supK = (HashMap<Set<String>, Double>)objs2[1];
			supportData.putAll(supK);
			ArrayList<Set<String>> Lk = (ArrayList<Set<String>>)objs2[0];
			L.add(Lk);
			k += 1;
		}
		return new Object[]{L, supportData};
	}

	public ArrayList<ArrayList<String>> loadDataSet() {
		String[][] recs = {{"1","3","4"}, {"2","3","5"}, {"1","2","3","5"}, {"2","5"}};
		ArrayList<ArrayList<String>> ds = new ArrayList<ArrayList<String>>();
		for (String[] rec: recs) {
			ArrayList<String> rec1 = new ArrayList<String>();
			for (String i: rec) rec1.add(i);
			ds.add(rec1);
		}
		return ds;
	}
	
	public Object[] scanD(ArrayList<ArrayList<String>> D, ArrayList<Set<String>> Ck, double minSupport) {
		HashMap<Set<String>, Integer> ssCnt = new HashMap<Set<String>, Integer>();
		
		for (Iterator<ArrayList<String>> it=D.iterator(); it.hasNext(); ) {
			ArrayList<String> tid = it.next();
			
			for (Iterator<Set<String>> it2=Ck.iterator(); it2.hasNext(); ) {
				Set<String> can = it2.next();
				
				if (tid.containsAll(can)) {
					if (!ssCnt.containsKey(can)) ssCnt.put(can, 1);
					else ssCnt.put(can, ssCnt.get(can)+1);
				}
			}
		}
		
		double numItems = D.size();
		ArrayList<Set<String>> refList = new ArrayList<Set<String>>();
		HashMap<Set<String>, Double> supportData = new HashMap<Set<String>, Double>();
		for (Iterator<Set<String>> it=ssCnt.keySet().iterator(); it.hasNext(); ) {
			Set<String> key = it.next();
			double support = ssCnt.get(key)/numItems;
			if (support >= minSupport) {
				//System.err.println("Put: "+key+": "+support);
				refList.add(key);
			}
			//System.err.println("Put: "+key+": "+support);
			supportData.put(key, support);
		}
		return new Object[]{refList, supportData};
	}

	public ArrayList<Set<String>> createC1(ArrayList<ArrayList<String>> dataSet) {
		ArrayList<Set<String>> C1 = new ArrayList<Set<String>>();
		TreeSet<String> ts = new TreeSet<String>();
		for (Iterator<ArrayList<String>> it=dataSet.iterator(); it.hasNext(); ) {
			ArrayList<String> rec = it.next();
			for (Iterator<String> it2=rec.iterator(); it2.hasNext(); ) {
				String i = it2.next();
				ts.add(i);
			}
		}
		for (Iterator<String> it=ts.iterator(); it.hasNext(); ) {
			Set<String> s = new TreeSet<String>();
			s.add(it.next());
			C1.add(s);
		}
		return C1;
	}
	
	public void outputL(ArrayList<ArrayList<Set<String>>> L) {
		System.out.println("Frequent item set: ");
		for (ArrayList<Set<String>> Lk: L) {
			for (Set<String> fis: Lk) {
				System.out.print(fis+" ");
			}
		}
		System.out.println();
		System.out.println("============================================");
	}
		
}
