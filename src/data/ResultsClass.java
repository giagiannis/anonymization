package data;

import java.util.ArrayList;

public class ResultsClass extends ArrayList<EquivalenceClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public double getGCP(int qid[], int ranges[], int numberOfTuples){
		double gcp=0;
		for(EquivalenceClass eq:this)
			gcp+=eq.getNCP(qid, ranges)*eq.getNumberOfTuples();
		gcp=gcp/(qid.length*numberOfTuples*1.0);
		return gcp;
	}
	
	public String toString(){
		String buffer="";
		for(int i=0;i<this.size();i++)
			buffer+=this.get(i)+"\n";
		return buffer;
	}

}
