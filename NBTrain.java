import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.StringTokenizer;


public class NBTrain {
	
	private Hashtable<String, Long> labelRecord;
	private int totalLabelRecord;
	private Hashtable<String, Long> wordLabelRecord;
	private Hashtable<String, Long> labelTotalWord;
	public static String delimiters = " \t,;.?!-:@[](){}_*/";
	
	public NBTrain () {
		labelRecord = new Hashtable<String, Long>();
		totalLabelRecord = 0;
		wordLabelRecord = new Hashtable<String, Long>();
		labelTotalWord = new Hashtable<String, Long>();
	}
	
	public static void main(String[] argv) throws FileNotFoundException, UnsupportedEncodingException {
		NBTrain nbTrain = new NBTrain();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String line;
		try {
			while ( (line = in.readLine()) != null) {
				String[] tempStrings = line.split("\t");
				String lables = tempStrings[0];
				String words = tempStrings[1];
				String[] label = lables.split(",");
				ArrayList<String> remainLabels = new ArrayList<>();
				for (String a : label) {
					if (!nbTrain.labelRecord.containsKey(a)) {
						nbTrain.labelRecord.put(a, (long) 1);
					} else {
						nbTrain.labelRecord.put(a, nbTrain.labelRecord.get(a)+1);
					}
					remainLabels.add(a);
				}
				nbTrain.totalLabelRecord += remainLabels.size();
				StringTokenizer wordList = new StringTokenizer(words, delimiters);			
				for (int i = 0; i < remainLabels.size(); i++) {
					String currlabel = remainLabels.get(i);
					if (!nbTrain.labelTotalWord.containsKey("Y=" + currlabel + ",W=*"))
						nbTrain.labelTotalWord.put("Y=" + currlabel + ",W=*", (long)wordList.countTokens());
					else
					    nbTrain.labelTotalWord.put("Y=" + currlabel + ",W=*", nbTrain.labelTotalWord.get("Y=" + currlabel + ",W=*") + wordList.countTokens());
					while (wordList.hasMoreTokens()){
						String a = wordList.nextToken();
						if (!nbTrain.wordLabelRecord.containsKey("Y=" + currlabel +",W=" + a))
							nbTrain.wordLabelRecord.put("Y=" + currlabel +",W=" + a, (long)1);
						else 
							nbTrain.wordLabelRecord.put("Y=" + currlabel +",W=" + a, nbTrain.wordLabelRecord.get("Y=" + currlabel +",W=" + a) + 1);
					}
				}	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<String> outItem = nbTrain.labelRecord.keySet();
		for (String a : outItem) {
			System.out.println(a + "\t" + nbTrain.labelRecord.get(a));
		}
		    System.out.println("Y=*" + "\t" + nbTrain.totalLabelRecord);
		
		outItem = nbTrain.wordLabelRecord.keySet();
		for (String a : outItem )
			System.out.println(a + "\t" + nbTrain.wordLabelRecord.get(a));
		
		outItem = nbTrain.labelTotalWord.keySet();
		for (String a : outItem) 
			System.out.println(a + "\t" + nbTrain.labelTotalWord.get(a));

	}
}
