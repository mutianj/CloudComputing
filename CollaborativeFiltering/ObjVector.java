import java.util.HashMap;


public interface ObjVector {
	public double getAvgScore();
	
	public void setAvgScore(double avgScore);
	
	public int getSize();
	
	public void setSize(int size);
	
	public HashMap<Integer, Byte> getRecords();
	
	public void addRecord(int id, byte score);
	
	public void setRecords(HashMap<Integer, Byte> records);
}
