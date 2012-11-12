import java.io.IOException;

import anonymity.Mondrian;

import data.DataReader;
import data.EquivalenceClass;


public class Runner {

	public static String args[];

	public static String getValue(String value){
		for(int i=0;i<args.length;i++)
			if(args[i].equals(value))
				return args[i+1];
		return null;
	}
	
	public static void main(String[] args) throws IOException{
		Runner.args=args;
		DataReader reader = new DataReader(getValue("-file"));
		EquivalenceClass data = new EquivalenceClass();
//		data = reader.getTuples();
		int numberOfTuples = new Integer(getValue("-tuples"));
		for(int i=0;i<numberOfTuples;i++)
			data.add(reader.getNextTuple());
	//	System.out.println(data);
		int qid[]={0,1,2,3,4};
		
		Mondrian algo = new Mondrian();
		algo.setQID(qid);
		algo.setData(data);
		algo.setK(2);
		algo.setRelaxedPartitioning();
		algo.run();
		System.out.println(algo.getResults().getGCP(qid, algo.getRanges(), data.getNumberOfTuples()));
	}
}
