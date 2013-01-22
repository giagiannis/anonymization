package apps;

import java.io.IOException;

import anonymity.algorithms.AbstractAlgorithm;
import anonymity.algorithms.DistanceBasedAnonymity;
import anonymity.algorithms.Mondrian;
import anonymity.algorithms.SortAlgorithm;
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
	private static String partitionType;
	
	/**
	 * @param args
	 */
	
	@SuppressWarnings("unused")
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
		partitionType=getValueAsArg(args, "-partType");
		
	}
	
	private static void presentResults(ECList results, AbstractPartitioner part, double time){
		int[] qid=part.getQID();
		int[] ranges=part.getRanges();
		int numberOfTuples=part.getData().size();
		System.out.format("%.5f\t",results.getGCP(qid, ranges, numberOfTuples));
		System.out.format("%.5f\t",results.getSumOfNCP(qid, ranges));
		System.out.print(results.size()+"\t");
		System.out.format("%.0f\t", time);
	}
	
	public static void main(String[] args) throws IOException{
		if(args.length<12){
			System.err.println("Needed arguments:\n\t-k\t\t<k factor>\n\t-tuples\t\t<# of tuples>\n\t-partitions\t<# of partitions>\n\t-file\t\t<file name>\n\t-qid\t\t\"<qid>\"\n\t-partType\t<rand|sort|dac>");
			System.exit(1);
		}
		commandLineParser(args);
		
		AbstractPartitioner sortPartition=null;
		if(partitionType.equals("sort"))
			sortPartition= new SortPartitioner(qid, data);
		else if(partitionType.equals("dac"))
			sortPartition = new DaCPartitioner(qid,data);
		else if(partitionType.equals("rand"))
			sortPartition = new RandomPartitioner(qid, data);
		else{
			System.err.println("Not a valid partitioning type!!");
			System.exit(1);
		}
	
		sortPartition.setNumberOfPartitions(numberOfPartitions);
		sortPartition.createPartitions();
		
	/*	ECList results=new ECList();
		for(EquivalenceClass data:partitioner.getPartitions()){
			AbstractAlgorithm algo = new Mondrian(qid, data);
			algo.setK(k);
			algo.run();
			results.merge(algo.getResults());
		}
		presentResults(results, partitioner);
		
		results.clear();
		for(EquivalenceClass data:partitioner.getPartitions()){
			AbstractAlgorithm algo = new DistanceBasedAnonymity(qid, data);
			algo.setK(k);
			algo.run();
			results.merge(algo.getResults());
		}
		presentResults(results, partitioner);
		
		*/
		ECList	results = new ECList();
		double start=0;
		double stop=0;
		for(EquivalenceClass data:sortPartition.getPartitions()){
			AbstractAlgorithm algo = new SortAlgorithm(qid, data);
			algo.setK(k);
			algo.setRanges(sortPartition.getRanges());
			start=System.currentTimeMillis();
			algo.run();
			stop=System.currentTimeMillis()-start;
			results.merge(algo.getResults());
		}
		presentResults(results, sortPartition, stop);
		
		results = new ECList();
		for(EquivalenceClass data:sortPartition.getPartitions()){
			AbstractAlgorithm algo = new Mondrian(qid, data);
			algo.setK(k);
			algo.setRanges(sortPartition.getRanges());
			start=System.currentTimeMillis();
			algo.run();
			stop=System.currentTimeMillis()-start;
			results.merge(algo.getResults());
		}
		presentResults(results, sortPartition, stop);
		
		results = new ECList();
		for(EquivalenceClass data:sortPartition.getPartitions()){
			AbstractAlgorithm algo = new DistanceBasedAnonymity(qid, data);
			algo.setK(k);
			algo.setRanges(sortPartition.getRanges());
			start=System.currentTimeMillis();
			algo.run();
			stop=System.currentTimeMillis()-start;
			results.merge(algo.getResults());
		}
		presentResults(results, sortPartition,stop);
	}
}
