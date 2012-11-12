package anonymity;

import data.EquivalenceClass;
import data.ResultsClass;
import data.Tuple;

@SuppressWarnings("unused")
public abstract class Algorithm {

	protected int[] qid;
	private EquivalenceClass data;
	private ResultsClass results;
	
	public Algorithm(){
		;
	}
	
	public Algorithm(int[] qid, EquivalenceClass data){
		this.setQID(qid);
		this.setData(data);
	}

	public void setQID(int qid[]){
		this.qid=qid;
	}
	
	public void setQID(String qid[]){
		this.qid=new int[qid.length];
		for(int i=0;i<this.qid.length;i++)
			this.qid[i]=new Integer(qid[i]);
	}
	
	public int[] getQID(){
		return this.qid;
	}
	
	public void setData(EquivalenceClass data){
		this.data=data;
	}

	public EquivalenceClass getData(){
		return this.data;
	}

	public abstract void run();
	
	public ResultsClass getResults(){
		return this.results;
	}

}