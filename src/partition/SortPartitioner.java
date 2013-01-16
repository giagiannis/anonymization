package partition;

import java.io.IOException;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;

public class SortPartitioner extends AbstractPartitioner {

	public SortPartitioner() {
	
	}

	public SortPartitioner(String[] qid, EquivalenceClass data) {
		super(qid, data);
	}

	@Override
	public void createPartitions() {


	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String [] args) throws IOException{
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		Integer numberOfPartitions=new Integer(conf.getValue("PARTITIONS"));
		Integer tuples = new Integer(conf.getValue("TUPLES"));
		String qid = conf.getValue("QID");
		EquivalenceClass data = new EquivalenceClass();
		
		for(int i=0;i<tuples;i++)
			data.add(reader.getNextTuple());
		
		AbstractPartitioner part = new SortPartitioner(qid.split(" "), data);
		part.setNumberOfPartitions(numberOfPartitions);

		part.createPartitions();
		
		AbstractPartitioner.presentPartitions(part.getPartitions());
	}
}
