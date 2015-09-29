public class KNNVector{
	public ObjVector uv = null;
	public double similarity = 0;
	public KNNVector(ObjVector uv, double similarity) {
		this.uv = uv;
		this.similarity = similarity;
	}
	
	public ObjVector getVector() {
		return uv;
	}
	public void setVector(ObjVector uv){
		this.uv = uv;
	}
	public void setSimilarity(double similarity){
		this.similarity = similarity;
	}
	public double getSimilarity(){
		return this.similarity;
	}


}
