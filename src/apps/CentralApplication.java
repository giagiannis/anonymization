package apps;

import java.io.IOException;

import anonymity.algorithms.AbstractAlgorithm;
import anonymity.algorithms.DistanceBasedAnonymity;
import anonymity.algorithms.Mondrian;
	

import data.ECList;
import data.EquivalenceClass;
import partition.AbstractPartitioner;
import partition.DaCPartitioner;
import partition.RandomPartitioner;
import partition.SortPartitioner;
import readers.ConfReader;
import readers.DataReader;

public class CentralApplication {

	private static String qid;
	private static int k;
	private static int numberOfPartitions;
	private static int tuples;
	private static EquivalenceClass data;
	
	/**
	 * @param args
	 */
	
	private static void confParser(String confname) throws IOException{
		ConfReader conf = new ConfReader(confname);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		k=new Integer(conf.getValue("K"));
		tuples=new Integer(conf.getValue("TUPLES"));
		numberOfPartitions=new Integer(conf.getValue("PARTITIONS"));
		qid=conf.getValue("QID");
		
		data=new EquivalenceClass();
		for(int i=0;i<tuples;i++)
			data.add(reader.getNextTuple());
		
	}
	
	private static String getValueAsArg(String[] args, String value){
		for(int i=0;i<args.length;i++){
			if(args[i].equals(value))
				return args[i+1];
		}
		return null;
	}
	
	private static void commandLineParser(String[] args) throws IOException{
		qid=getValueAsArg(args, "-qid");
		k=new Integer(getValueAsArg(args, "-k"));
		numberOfPartitions=new Integer(getValueAsArg(args, "-partitions"));
		tuples=new Integer(getValueAsArg(args, "-tuples"));
		DataReader reader = new DataReader(getValueAsArg(args, "-file"));
		data=new EquivalenceClass();
		for(int i=0;i<tuples;i++)
			data.add(reader.getNextTuple());
		
	}
	
	private static void presentResults(ECList results, AbstractPartitioner part){
		int[] qid=part.getQID();
		int[] ranges=part.getRanges();
		int numberOfTuples=part.getData().size();
		System.out.print(results.getGCP(qid, ranges, numberOfTuples)+"\t");
		System.out.print(results.getSumOfNCP(qid, ranges)+"\t");
		System.out.print(results.size()+"\n");
	}
	
	public static void main(String[] args) throws IOException{
		if(args.length<1){
			System.out.println("I need a configuration file!");
			System.exit(1);
		}
		//confParser(args[0]);
		commandLineParser(args);
		
		AbstractPartitioner partitioner = new DaCPartitioner(qid, data);
		
		partitioner.setNumberOfPartitions(numberOfPartitions);
		partitioner.createPartitions();
		
		ECList results=new ECList();
		for(EquivalenceClass data:partitioner.getPartitions()){
			AbstractAlgorithm algo = new Mondrian(qid, data);
			algo.setK(k);
			algo.run();
			results.merge(algo.getResults());
		}
		presentResults(results, partitioner);
		
/*		partitioner = new RandomPartitioner(qid, data);
		partitioner.setNumberOfPartitions(numberOfPartitions);
		partitioner.createPartitions();
		
		results=new ECList();
		for(EquivalenceClass data:partitioner.getPartitions()){	
			AbstractAlgorithm algo = new DistanceBasedAnonymity(qid, data);
			algo.setK(k);
			algo.run();
			results.merge(algo.getResults());
		}
		presentResults(results, partitioner);
*/		
	}
}
