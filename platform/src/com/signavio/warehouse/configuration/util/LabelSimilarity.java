package com.signavio.warehouse.configuration.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;


public class LabelSimilarity {
	
	private Map<String, Long> word2id;
	private Map<String, Long> morph2wordid;
	private Map<Long, Set<Long>> wordid2synidset;
	private Set<String> stopWords;

	public static final double EQ_SCORE = 1.0;
	public static final double SYN_SCORE = 0.75;
	
	private Boolean dictionaryLoaded = false;
	private String mapdir;
	
	private final Porter stemmingAlgorithm = new Porter();
	
	public LabelSimilarity(String mapdir) {
		this.mapdir = mapdir;
		loadDictionary();
	}
	
	@SuppressWarnings("unchecked")
	private void loadDictionary() {
		word2id = Collections.unmodifiableMap((Map<String, Long>) loadMapFromObject(mapdir + "word2id.map"));
		morph2wordid = Collections.unmodifiableMap((Map<String, Long>) loadMapFromObject(mapdir + "morph2wordid.map"));
		wordid2synidset = Collections.unmodifiableMap((Map<Long, Set<Long>>) loadMapFromObject(mapdir
				+ "wordid2synidset.map"));
		stopWords = Collections.unmodifiableSet(loadStringSetFromText(mapdir + "englishST.txt"));
		dictionaryLoaded = true;
	}
	
	private Set<String> loadStringSetFromText(String file) {
		Set<String> result = new HashSet<String>();
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			boolean hasRead = true;
			while (hasRead) {
				String read = br.readLine();
				hasRead = (read != null);
				if (hasRead) {
					result.add(read);
				}
			}

			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private Object loadMapFromObject(String file) {
		Object result = null;
		try {
			FileInputStream fos = new FileInputStream(file);
			ObjectInputStream oos = new ObjectInputStream(fos);
			result = oos.readObject();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String normalizeWord(String word) {
		return word.trim().toLowerCase().replaceAll(",|:|/|\\[|\\]|\\(|\\)", "").replaceAll("-", " ");
	}
	
	private Long word2id(String word) {
		if (!dictionaryLoaded) {
			throw new RejectedExecutionException("Dictionaries not loaded, Checker not ready for this.");
		}
		return word2id.get(word);
	}
	
	private Long morph2wordid(String word) {
		if (!dictionaryLoaded) {
			throw new RejectedExecutionException("Dictionaries not loaded, Checker not ready for this.");
		}
		return morph2wordid.get(word);
	}
	
	private Vector<String> splitString(String src) {
		if (!dictionaryLoaded) {
			throw new RejectedExecutionException("Dictionaries not loaded, Checker not ready for this.");
		}
		Vector<String> result = new Vector<String>();

		int fromIndex = 0;
		int foundIndex;
		do {
			foundIndex = src.indexOf(' ', fromIndex);
			int toIndex = (foundIndex == -1) ? (src.length()) : (foundIndex);
			if (fromIndex != toIndex) {
				String trimmedWord = normalizeWord(src.substring(fromIndex, toIndex));
				if (!stopWords.contains(trimmedWord)) {
					result.add(trimmedWord);
				}
			}
			fromIndex = toIndex + 1;
		} while (foundIndex != -1);

		return result;
	}
	
	private long getWordID(String word) {
		if (word.length() == 0) {
			return 0;
		}
		;

		Long result = word2id(word);

		if (result == null) {
			String stemmedWord = stemmingAlgorithm.stripAffixes(word);
			result = word2id(stemmedWord);
		}

		if (result == null) {
			result = morph2wordid(word);
		}

		if (result != null) {
			return result;
		} else {
			return 0;
		}
	}
	
	private Set<Long> wordid2synidset(long wordid) {
		if (!dictionaryLoaded) {
			throw new RejectedExecutionException("Dictionaries not loaded, Checker not ready for this.");
		}
		Set<Long> result = wordid2synidset.get(wordid);
		if (result != null) {
			return result;
		} else {
			return new HashSet<Long>();
		}
	}
	
	//return semantic equivalence
	public double semanticEquivalenceScore(String label1, String label2) {
		Vector<String> wds1 = splitString(label1.replace("\r\n", " ").replace('\n', ' '));
		Vector<String> wds2 = splitString(label2.replace("\r\n", " ").replace('\n', ' '));

		Vector<String> longestVector;
		Vector<String> shortestVector;
		if (wds1.size() > wds2.size()) {
			longestVector = wds1;
			shortestVector = wds2;
		} else {
			longestVector = wds2;
			shortestVector = wds1;
		}

		Vector<Set<Long>> lwordsyns = new Vector<Set<Long>>();
		Vector<Long> lwordids = new Vector<Long>();
		for (Iterator<String> i = longestVector.iterator(); i.hasNext();) {
			String word1 = i.next();
			long wordid1 = getWordID(word1);
			lwordids.add(wordid1);
			lwordsyns.add(wordid2synidset(wordid1));
		}
		Vector<Set<Long>> swordsyns = new Vector<Set<Long>>();
		Vector<Long> swordids = new Vector<Long>();
		for (Iterator<String> i = shortestVector.iterator(); i.hasNext();) {
			String word2 = i.next();
			long wordid2 = getWordID(word2);
			swordids.add(wordid2);
			swordsyns.add(wordid2synidset(wordid2));
		}

		double score = 0.0;

		for (int i = 0; i < shortestVector.size(); i++) {
			String word1 = shortestVector.elementAt(i);
			double currscore = 0.0;
			for (int j = 0; j < longestVector.size(); j++) {
				String word2 = longestVector.elementAt(j);
				if (word1.equals(word2)) {
					currscore = EQ_SCORE;
					break;
				}
				if (swordsyns.elementAt(i).contains(lwordids.elementAt(j))) {
					currscore = SYN_SCORE;
				} else if (lwordsyns.elementAt(j).contains(swordids.elementAt(i))) {
					currscore = SYN_SCORE;
				}
			}
			score += currscore;
		}
		return score / longestVector.size();
	}
	
	//syntactic equivalence
	
		public double syntacticEquivalenceScore(String label1, String label2) {
			String s = label1;
			String t = label2;

			int n = s.length(); // length of s
			int m = t.length(); // length of t

			if (n == 0) {
				return m;
			} else if (m == 0) {
				return n;
			}
			int MAX_N = m + n;

			short[] swap; // placeholder to assist in swapping p and d

			// indexes into strings s and t
			short i; // iterates through s
			short j; // iterates through t

			Object t_j = null; // jth object of t

			short cost; // cost

			short[] d = new short[MAX_N + 1];
			short[] p = new short[MAX_N + 1];

			for (i = 0; i <= n; i++) {
				p[i] = i;
			}

			for (j = 1; j <= m; j++) {
				t_j = t.charAt(j - 1);
				d[0] = j;

				Object s_i = null; // ith object of s
				for (i = 1; i <= n; i++) {
					s_i = s.charAt(i - 1);
					cost = s_i.equals(t_j) ? (short) 0 : (short) 1;
					// minimum of cell to the left+1, to the top+1, diagonally left
					// and up +cost
					d[i] = (short) Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
				}

				// copy current distance counts to 'previous row' distance counts
				swap = p;
				p = d;
				d = swap;
			}

			// our last action in the above loop was to switch d and p, so p now
			// actually has the most recent cost counts
			int costcount = p[n];

			// equivalence score = 1 - (costcount / max_costcount)
			// where max_costcount = sum of string lengths
			return 1 - (costcount * 1.0) / (s.length() * 1.0 + t.length() * 1.0);
		}
		

}

