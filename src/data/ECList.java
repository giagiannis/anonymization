package data;

import java.util.ArrayList;

public class ECList extends ArrayList<EquivalenceClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void merge(ECList other){
		for(EquivalenceClass cl:other)
			this.add(cl);
	}
	
	public double getGCP(int qid[], int ranges[], int numberOfTuples){
		double gcp=0;
		for(EquivalenceClass eq:this)
			gcp+=eq.getNCP(qid, ranges)*eq.getNumberOfTuples();
		gcp=gcp/(qid.length*numberOfTuples*1.0);
		return gcp;
	}
	
	public double getSumOfNCP(int qid[], int ranges[]){
		double sum=0.0;
		for(EquivalenceClass cl:this)
			sum+=cl.getNCP(qid, ranges);
		return sum;
	}
	
	public double getDM(){
		double sum=0;
		for(EquivalenceClass cl:this)
			sum+=Math.pow(cl.size(), 2);
		return sum;
	}
	
	public double getGCP(String qid[], int ranges[], int numberOfTuples){
		int qidI[] = new int[qid.length];
		for(int i=0;i<qidI.length;i++)
			qidI[i] = new Integer(qid[i]);
		return getGCP(qidI, ranges, numberOfTuples);
	}
	
	public String toString(){
		String buffer="";
		for(int i=0;i<this.size();i++)
			buffer+=this.get(i)+"\n";
		return buffer;
	}

}
