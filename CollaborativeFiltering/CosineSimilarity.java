import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class CosineSimilarity implements Similarity{
	private boolean useAvg = false;
	public boolean isUseAvg(){
		return useAvg;
	}
	public void setUseAvg(boolean useAvg){
		this.useAvg = useAvg;
	}
	@Override
	public double calSimilarity(ObjVector query, ObjVector vector) {
		// TODO Auto-generated method stub
		HashMap<Integer, Byte> queryVector = query.getRecords();
		HashMap<Integer, Byte> baseVector = vector.getRecords();
		double norm1 = (useAvg?query.getAvgScore():3);
		double norm2 = (useAvg?query.getAvgScore():3);
		
		Set<Integer> all = new HashSet<Integer>();
		all.addAll(queryVector.keySet());
		all.retainAll(baseVector.keySet());
		double similarTotal = 0;
		double v1 = 0;
		double v2 = 0;
		for (int key: all){
			double d1 = 0;
			double d2 = 0;
			d1 = queryVector.get(key)-norm1;
			d2 = baseVector.get(key) - norm2;
			similarTotal+=d1*d2;
		}
		for(int score: queryVector.values()){
			v1+=(score-norm1)*(score-norm1);
		}
		for (int score: baseVector.values()) {
			v2+=(score-norm2)*(score-norm2);
		}
		if (v1==0||v2==0){
			return 0;
		}
		return similarTotal/(Math.sqrt(v1)*Math.sqrt(v2));
	}
}
