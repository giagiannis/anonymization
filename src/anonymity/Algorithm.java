package anonymity;

import data.EquivalenceClass;
import data.ECList;
/**
 * Base class for algorithms implementation. This class provides methods for 
 * setting and getting data, qid, results, etc. It also includes an abstract method that must be included
 * in every algorithm (method run with no arguments).
 * 
 * @author Giannis Giannakopoulos
 *
 */

public abstract class Algorithm {

	protected int[] qid;
	protected int[] generalRanges;
	private EquivalenceClass data;
	private ECList results;
	private int k;
	
	public Algorithm(){
		this.results = new ECList();
	}
	
	public Algorithm(int[] qid, EquivalenceClass data){
		this.setQID(qid);
		this.setData(data);
		this.results = new ECList();
	}
	
	public Algorithm(String[] qid, EquivalenceClass data){
		this.setQID(qid);
		this.setData(data);
		this.results = new ECList();
	}

	/**
	 * Sets QID array as an integer array
	 * @param qid
	 */
	public void setQID(int qid[]){
		this.qid=qid;
	}
	
	/**
	 * Sets QID array as a String array and converts it to int array (useful when reading qid from file/command line)
	 * @param qid
	 */
	public void setQID(String qid[]){
		this.qid=new int[qid.length];
		for(int i=0;i<this.qid.length;i++)
			this.qid[i]=new Integer(qid[i]);
	}
	
	/**
	 * Get qid as an int array
	 * @return 
	 */
	public int[] getQID(){
		return this.qid;
	}
	
	/**
	 * Sets data as an EquivalenceClass (set of tuples)<br/>
	 * This method must be called AFTER setQID method or else an exception will be thrown
	 * @param data
	 */
	public void setData(EquivalenceClass data){
		this.data=data;
		if(this.qid==null){
			System.err.println("QID must be set before setting data");
			System.exit(1);
		}
		this.generalRanges = new int[this.qid.length];
		for(int i=0;i<this.qid.length;i++)
			this.generalRanges[i]=data.getRangeByDimension(this.qid[i]);
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
	
	public ECList getResults(){
		return this.results;
	}
	
	protected void addToResults(EquivalenceClass eq){
		this.results.add(eq);
	}
	
	protected void setResults(ECList results){
		this.results=results;
	}
}