package data;

import java.util.ArrayList;

/**
 * @author Giannis Giannakopoulos	
 * Container class that is used to keep data-tuples. It extends ArrayList class
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
	
	public String toString(){
		String buffer="[";
		int i;
		for(i=0;i<this.size()-1;i++)
			buffer+="("+this.get(i)+"),";
		buffer+="("+this.get(i)+")]";
		return buffer;
	}
}
