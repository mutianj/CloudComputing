import java.util.Comparator;


public class KCompare implements Comparator<KNNVector> {
	public int compare(KNNVector o1, KNNVector o2){
		if (o1.similarity<o2.similarity){
			return -1;
		} else if (o1.similarity==o2.similarity){
			return 0;
		} else {
			return 1;
		}
	}

}
