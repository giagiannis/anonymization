package data;

/**
 * @author Giannis Giannakopoulos
 * 
 * The objects of this class are points that belong to q-dimensional space. 
 * The dimensions are counted starting at 0 (and not 1).
 *  
 * */
public class Tuple {
	
	private String[] values;
	private int[] data;
	
	public Tuple(){
		
	}
	
	public Tuple(String[] values){
		setValues(values);
	}
	
	public void setValues(String values[]){
		this.values=values;
		this.values = new String[values.length];
		for(int i=0;i<this.values.length;i++)
			this.values[i]=values[i].trim();
		this.data=new int[this.values.length];
		for(int i=0;i<this.data.length;i++)
			this.data[i]=-1;
	}
	
	public int getValue(int dimension){
		if(this.data[dimension]==-1)
			this.data[dimension]=new Integer(this.values[dimension]);
		return this.data[dimension];
	}
	
	public int getNumberOfAttributes(){
		return this.values.length;
	}
	
	public String toString(){
		String buffer="";
		int i;
		for(i=0;i<this.values.length-1;i++)
			buffer+=this.values[i]+",";
		buffer+=this.values[i];
		return buffer;
	}
	
	public double getDistance(Tuple other, int[] qid, int[] ranges){
		double res=0.0;
		for(int i=0;i<qid.length;i++){
			res+=(Math.abs(this.getValue(qid[i])-other.getValue(qid[i])))/(1.0*ranges[i]);
		}
		return res;
	}
}
