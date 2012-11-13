import java.io.IOException;

import readers.ConfReader;
import readers.DataReader;

import anonymity.Mondrian;

import data.EquivalenceClass;


/**
 * @author Giannis Giannakopoulos <br/><br/>
 * This class is the front end application for executing centralized algorithms 
 */

public class Runner {

	private static String args[];

	public static String getValue(String value){
		for(int i=0;i<args.length;i++)
			if(args[i].equals(value))
				return args[i+1];
		return null;
	}
	
	public static void main(String[] args) throws IOException{
		if(args.length<1){
			System.err.println("Give a configuration file with flag -file");
			System.exit(1);
		}
		Runner.args=args; 
		  
		ConfReader conf = new ConfReader(getValue("-file"));
		DataReader datareader = new DataReader(conf.getValue("datafile"));
		EquivalenceClass data = new EquivalenceClass();
		int temp=new Integer(conf.getValue("tuples"));
		for(int i=0;i<temp;i++)
			data.add(datareader.getNextTuple());
		int k = new Integer(conf.getValue("k"));
		String[] qid = conf.getValue("qid").split(" ");

		Mondrian algo = new Mondrian();
		algo.setQID(qid);
		algo.setData(data);
		algo.setK(k);
		algo.setRelaxedPartitioning();
		algo.run();
		System.out.print(algo.getResults().getGCP(qid, algo.getRanges(), data.getNumberOfTuples())+"\t");
	}
}
