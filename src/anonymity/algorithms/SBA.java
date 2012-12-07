package anonymity.algorithms;

import java.util.HashMap;

import data.ECList;
import data.EquivalenceClass;
import data.Tuple;

import readers.DataReader;
import anonymity.Algorithm;

/**
 * This class is an implementation of a simple algorithm for achieving k-anonymity.<br/>
 * This algorithm executes a plain partitioning to tuples based on the values of their attributes. <br/>
 * Same values on the same attribute lead to the same EquivalenceClass. At the end of the execution, <br/>
 * equivalence classes with less than k-elements are merged with each other or with bigger classes
 * @author Giannis Giannakopoulos
 *
 */
public class SBA extends Algorithm {

	private int depth;

	public SBA(String[] qid, EquivalenceClass data) throws Exception{
		super(qid,data);
	}
	 
	private void sortQIDByRanges(int[] ranges){
		for(int i=0;i<this.qid.length;i++){
			boolean swap=false;
			for(int j=0;j<this.qid.length-i-1;j++){
				if(ranges[j]>ranges[j+1]){
					int temp=ranges[j];
					ranges[j]=ranges[j+1];
					ranges[j+1]=temp;
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
		this.sortQIDByRanges(this.getRanges());
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
		//String qid="1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";
		String qid="0 1 2 3 4 5 6 7 8 9";
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<30000;i++)
			data.add(reader.getNextTuple());
	//	System.out.println(data.size());
		
	//	System.out.println(data);
		SBA algo = new SBA(qid.split(" "), data);
		algo.setK(20);
		algo.setDepth(3);
		algo.run();
		int count=0;
		for(EquivalenceClass cl:algo.getResults())
			if(cl.size()<20)
				count++;
		System.out.println(count+" out of "+algo.getResults().size()+" EC are illegal");
	}
}