import java.util.PriorityQueue;


public class MeanPredictor implements Predictor{
	
	@Override
	public double predictUser(int movieID, PriorityQueue<KNNVector> knn) {
		// TODO Auto-generated method stub
		double score = 0.0;
		double weight = 0.0;
		for (KNNVector user:knn){
			double raw = 0;
			if (user.getVector().getRecords().containsKey(movieID)){
				raw = user.getVector().getRecords().get(movieID);
			} else {
				raw = user.getVector().getAvgScore();
			}
			score += raw;
			weight = weight+1;
		}
		return score/weight;
	}

}
