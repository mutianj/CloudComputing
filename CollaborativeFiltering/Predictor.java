import java.util.PriorityQueue;


public interface Predictor {
	public abstract double predictUser(int movieID, PriorityQueue<KNNVector> knn);
}
