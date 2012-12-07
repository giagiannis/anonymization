package anonymity.algorithms;


import data.EquivalenceClass;

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

	public SBA(String[] qid, EquivalenceClass data) throws Exception{
		super(qid,data);
	}
	@Override
	public void run() {
		
	}
	
	public static void main(String[] args) throws Exception{
		DataReader reader = new DataReader(args[0]);
		String qid="0 1 2";
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<100;i++)
			data.add(reader.getNextTuple());
		
		System.out.println(data);
		SBA algo = new SBA(qid.split(" "), data);
		algo.setK(2);
		algo.run();
	}

}
