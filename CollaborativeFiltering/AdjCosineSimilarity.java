
public class AdjCosineSimilarity implements Similarity{
	CosineSimilarity cs = new CosineSimilarity();

	@Override
	public double calSimilarity(ObjVector query, ObjVector uv) {
		// TODO Auto-generated method stub
		cs.setUseAvg(true);
		double similarity = cs.calSimilarity(query, uv);
		return similarity;
	}

}
