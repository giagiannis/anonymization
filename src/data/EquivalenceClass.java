package data;

import java.util.ArrayList;

/**
 * @author Giannis Giannakopoulos	
 * <p>
 * Container class that is used to keep data-tuples. It extends ArrayList class.
 * </p>
 * */
public class EquivalenceClass extends ArrayList<Tuple> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int getRangeByDimension(int dimension){
		int max=this.get(0).getValue(dimension),min=this.get(0).getValue(dimension);
		for(Tuple t:this){
			int tempValue=t.getValue(dimension);
			if(tempValue>max)
				max=tempValue;
			if(tempValue<min)
				min=tempValue;
		}
		return max-min+1;
	}
	
	public int[] getValuesByDimension(int dimension){
		int[] values = new int[this.size()];
		for(int i=0;i<this.size();i++)
			values[i]=this.get(i).getValue(dimension);
		return values;
		
	}
	
	public int getNumberOfTuples(){
		return this.size();
	}
	
	public double getNCP(int qid[], int ranges[]){
		double sum=0;
		for(int dim:qid)
			sum+=getRangeByDimension(dim)/(1.0*ranges[dim]);
		return sum;
	}
	
	public String toString(){
		String buffer="[";
		int i;
		if(this.size()>=1){
			for(i=0;i<this.size()-1;i++)
				buffer+="("+this.get(i)+"),";
			buffer+="("+this.get(i)+")]";
		}
		else
			buffer+="]";
		return buffer;
	}
}
