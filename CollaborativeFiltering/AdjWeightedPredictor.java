import java.util.PriorityQueue;


public class AdjWeightedPredictor implements Predictor{
	public double avg = 0;
	public AdjWeightedPredictor(){
		this.avg = 3;
	}
	public AdjWeightedPredictor(double avg){
		this.avg = avg;
	}
	public void setAvg(double avg){
		this.avg = avg;
	}
	@Override
	public double predictUser(int movieID, PriorityQueue<KNNVector> knn) {
		// TODO Auto-generated method stub
		double score = 0.0;
		double weight = 0.0;
		double score_backup = 0.0;
		for(KNNVector user:knn){
			double raw = 0.0;
			if (user.getVector().getRecords().containsKey(movieID)){
				raw = user.getVector().getRecords().get(movieID)-user.getVector().getAvgScore();
			} else {
				raw = 0;
			}
			score+=raw*user.similarity;
			weight+=user.similarity;
			score_backup+=raw;
		}
		if (weight>0.0)
			return this.avg+score/weight;
		else
			return this.avg+score_backup/knn.size();
	}

}
