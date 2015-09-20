import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;

public class NBTest {
	
	private Hashtable<String, Long> labelRecord;
	private long totalLabelRecord;
	private long numberOfLabel;
	private Hashtable<String, Long> wordLabelRecord;
	private Hashtable<String, Long> labelTotalWord;
	public static String delimiters = " \t,;.?!-:@[](){}_*/";
	
	public NBTest () {
		labelRecord = new Hashtable<String, Long>();
		totalLabelRecord = (long)0;
		numberOfLabel = (long)0;
		wordLabelRecord = new Hashtable<String, Long>();
		labelTotalWord = new Hashtable<String, Long>();
	}
	
	static Vector<String> tokenizeDoc(String cur_doc) {
        String[] words = cur_doc.split("\\s+");
        Vector<String> tokens = new Vector<String>();
        for (int i = 0; i < words.length; i++) {
        	words[i] = words[i].replaceAll("\\W", "");
        	if (words[i].length() > 0) {
        		tokens.add(words[i]);
        	}
        }
        return tokens;
	}
	
	
	public static void main(String[] argv) throws IOException {
		NBTest nbTest = new NBTest();
	
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		
		try {
			String line = in.readLine();
			while ( line != null) {
				String[] tempStrings = line.split("\t");
				String key = tempStrings[0];
				long value = Long.valueOf(tempStrings[1]);

				if (key.equals("Y=*")){
					nbTest.totalLabelRecord = value;
				} else if (!key.contains(",")){
					nbTest.labelRecord.put(key, value);	
					nbTest.numberOfLabel++;
				} else {
					String[] keypart = key.split(",");
					if (keypart[1].equals("W=*")){
						nbTest.labelTotalWord.put(key, value);
					} else {
						nbTest.wordLabelRecord.put(key, value);
					}
				}	
				line = in.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        int addDenominator = nbTest.wordLabelRecord.size();
		
		Hashtable<String, Double> probs = new Hashtable<String, Double>();
		Enumeration<String> e1 = nbTest.labelRecord.keys();		
		while (e1.hasMoreElements()) {
			String k = (String) e1.nextElement();
			probs.put(k, nbTest.labelRecord.get(k)/(double)nbTest.totalLabelRecord);
		}
		//System.out.println(ccatProb + " " + ecatProb + " " + gcatProb + " " + mcatProb);
		
		
		BufferedReader br = new BufferedReader(new FileReader(argv[0]));
		//BufferedReader br = new BufferedReader(new FileReader("RCV1.very_small_test.txt"));
		String newLine = br.readLine();
		while (newLine != null) {	
			//String words = newline;
			StringTokenizer wordList = new StringTokenizer(newLine, delimiters);
			Hashtable<String, Double> testProbs = new Hashtable<String, Double>();
			String label = null;
			Double predProb= - Double.MAX_VALUE;
			while (wordList.hasMoreTokens()){
				String w = wordList.nextToken();
				Enumeration<String> e2 = nbTest.labelRecord.keys();
				while (e2.hasMoreElements()) {
					String k = (String) e2.nextElement();
					if (!testProbs.containsKey(k)) testProbs.put(k, (double)0);
					if (nbTest.wordLabelRecord.containsKey("Y="+k+",W="+w)){
						testProbs.put(k, testProbs.get(k)+Math.log((nbTest.wordLabelRecord.get("Y="+k+",W="+ w)+1) / (double)(nbTest.labelTotalWord.get("Y="+k+",W=*")+addDenominator)));
					} else {
						testProbs.put(k, testProbs.get(k)+Math.log(1 / (double)(nbTest.labelTotalWord.get("Y="+k+",W=*")+addDenominator)));
					}
				}
			}
			Enumeration<String> e3 = testProbs.keys();
			while (e3.hasMoreElements()){
				String k = (String) e3.nextElement();
				testProbs.put(k, testProbs.get(k)+Math.log(probs.get(k)));
				System.out.println(k+testProbs.get(k));
			}
			Enumeration<String> e4 = testProbs.keys();
			while (e4.hasMoreElements()){
				String k = (String) e4.nextElement();
				if (predProb<testProbs.get(k)) {
					label = k;
					predProb = testProbs.get(k);
				}
			}
			
			System.out.println(label + "\t" + predProb);
			newLine = br.readLine();
		}
		br.close();
	}
}
