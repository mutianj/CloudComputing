import java.util.HashMap;


public class MovieVector implements ObjVector{
	private int movieID;
	private double avgScore;
	private int size;
	private HashMap<Integer, Byte> records = new HashMap<Integer, Byte>();
	
	public MovieVector(){
		super();
	}
	public MovieVector(int movieID, double avgScore, HashMap<Integer, Byte> records){
		super();
		this.movieID = movieID;
		this.avgScore = avgScore;
		this.records = records;
	}
	
	public int getMovieID() {
		return this.movieID;
	}
	
	public void setMovieID(int movieID){
		this.movieID = movieID;
	}
	@Override
	public double getAvgScore() {
		return this.avgScore;
	}

	@Override
	public void setAvgScore(double avgScore) {
		this.avgScore = avgScore;
		
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public void setSize(int size) {
		this.size = size;
		
	}

	@Override
	public HashMap<Integer, Byte> getRecords() {
		return this.records;
	}

	@Override
	public void addRecord(int userID, byte score) {
		this.records.put(userID, score);
		
	}

	@Override
	public void setRecords(HashMap<Integer, Byte> records) {
		this.records = records;
		
	}
	
}
