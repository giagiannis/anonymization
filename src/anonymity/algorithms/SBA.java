package anonymity.algorithms;

import java.util.HashMap;

import data.ECList;
import data.EquivalenceClass;
import data.Tuple;

import readers.DataReader;

/**
 * This class is an implementation of a simple algorithm for achieving k-anonymity.<br/>
 * This algorithm executes a plain partitioning to tuples based on the values of their attributes. <br/>
 * Same values on the same attribute lead to the same EquivalenceClass. At the end of the execution, <br/>
 * equivalence classes with less than k-elements are merged with each other or with bigger classes <br/>
 * @author Giannis Giannakopoulos
 *
 */
public class SBA extends AbstractAlgorithm {

	private int depth;

	public SBA(String[] qid, EquivalenceClass data) throws Exception{
		super(qid,data);
	}
	 
	private void sortQIDByRanges(){
		for(int i=0;i<this.qid.length;i++){
			boolean swap=false;
			for(int j=0;j<this.qid.length-i-1;j++){
				if(this.generalRanges[j]>this.generalRanges[j+1]){
					int temp=this.generalRanges[j];
					this.generalRanges[j]=this.generalRanges[j+1];
					this.generalRanges[j+1]=temp;
					temp=qid[j];
					qid[j]=qid[j+1];
					qid[j+1]=temp;
					swap=true;
				}
			}
			if(!swap)
				break;
		}
	}
	
	@Override
	public void run() {
		
		this.sortQIDByRanges();			
		ECList init = new ECList();
		init.add(this.getData());
		ECList result = new ECList();
		for(int i=0;i<this.depth;i++){
			result=this.step(init, this.qid[i]);
			init=result;
		}
		this.setResults(result);
	
	}
	
	private ECList step(ECList parts, int dim){
		ECList current=new ECList();
		for(EquivalenceClass cl:parts)
			current.merge(this.splitECByDimensionValues(cl, dim));
		return current;
	}
	
	private ECList splitECByDimensionValues(EquivalenceClass partition, int dimension){
		HashMap<Integer, EquivalenceClass> hashmap = new HashMap<Integer,EquivalenceClass>();
		for(Tuple t:partition){
			if(hashmap.containsKey(t.getValue(dimension)))
				hashmap.get(t.getValue(dimension)).add(t);
			else{
				EquivalenceClass temp=new EquivalenceClass();
				temp.add(t);
				hashmap.put(t.getValue(dimension), temp);
			}
		}
		ECList results=new ECList();
		
		for(EquivalenceClass current:hashmap.values())
			results.add(current);
		
		return results;
	}
	
	public void setDepth(int depth){
		this.depth=depth;
	}
	
	public static void main(String[] args) throws Exception{
		DataReader reader = new DataReader(args[0]);
		String qid="0 1 2 3 4 5 6 7 8 9";
//		String qid="0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<1000;i++)
			data.add(reader.getNextTuple());
		
		SBA algo = new SBA(qid.split(" "), data);
		algo.setK(20);
		algo.setDepth(4);
		algo.run(); 
		for(EquivalenceClass cls:algo.getResults()){
			System.out.println(cls.size());
		}
			
	}
}