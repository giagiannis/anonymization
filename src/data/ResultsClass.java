package data;

import java.util.ArrayList;

public class ResultsClass extends ArrayList<EquivalenceClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String toString(){
		String buffer="";
		for(int i=0;i<this.size();i++)
			buffer+=this.get(i)+"\n";
		return buffer;
	}

}
