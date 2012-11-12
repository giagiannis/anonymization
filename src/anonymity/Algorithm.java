package anonymity;

import data.EquivalenceClass;
import data.ResultsClass;
import data.Tuple;

@SuppressWarnings("unused")
public abstract class Algorithm {

	protected int[] qid;
	protected int[] generalRanges;
	private EquivalenceClass data;
	private ResultsClass results;
	private int k;
	
	public Algorithm(){
		this.results = new ResultsClass();
	}
	
	public Algorithm(int[] qid, EquivalenceClass data){
		this.setQID(qid);
		this.setData(data);
		this.results = new ResultsClass();
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
		this.generalRanges = new int[data.get(0).getNumberOfAttributes()];
		for(int dim:this.qid)
			this.generalRanges[dim]=data.getRangeByDimension(dim);
	}

	public EquivalenceClass getData(){
		return this.data;
	}
	
	public int[] getRanges(){
		return this.generalRanges;
	}

	public void setK(int k){
		this.k=k;
	}
	
	public int getK(){
		return this.k;
	}
	
	public abstract void run();
	
	public ResultsClass getResults(){
		return this.results;
	}
	
	protected void addToResults(EquivalenceClass eq){
		this.results.add(eq);
	}
}