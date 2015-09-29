import java.util.HashMap;


public class UserVector implements ObjVector {
	private double avgScore = 0;
	private int size = 0;
	private int userID;
	private int totalScore = 0;
	private HashMap<Integer, Byte> records = null;
	
	public UserVector() {
		this.records = new HashMap<Integer, Byte>();
	}
	
	public void setUserID(int userID){
		this.userID = userID;
	}
	
	public int getUserID(){
		return this.userID;
	}
	public int getTotalScore(){
		return this.totalScore;
	}
	
	public void setTotalScore(int totalScore){
		this.totalScore = totalScore;
	}
	@Override
	public double getAvgScore() {
		// TODO Auto-generated method stub
		return this.avgScore;
	}

	@Override
	public void setAvgScore(double avgScore) {
		// TODO Auto-generated method stub
		this.avgScore = avgScore;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return this.size;
	}

	@Override
	public void setSize(int size) {
		// TODO Auto-generated method stub
		this.size = size;
	}

	@Override
	public HashMap<Integer, Byte> getRecords() {
		// TODO Auto-generated method stub
		return this.records;
	}

	@Override
	public void addRecord(int id, byte score) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRecords(HashMap<Integer, Byte> records) {
		// TODO Auto-generated method stub
		this.records = records;
	}
	
	public void addRecords(int movieID, byte score){
		this.records.put(movieID, score);
	}
}
