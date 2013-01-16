package partition;

import java.io.IOException;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;

public class RandomPartitioner extends AbstractPartitioner {

	public RandomPartitioner(String[] qid, EquivalenceClass data) {
		super(qid, data);
	}

	@Override
	public void createPartitions() {
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		Integer numberOfPartitions=new Integer(conf.getValue("PARTITIONS"));
		Integer tuples = new Integer(conf.getValue("TUPLES"));
		String qid = conf.getValue("QID");
		EquivalenceClass data = new EquivalenceClass();
		
		for(int i=0;i<tuples;i++)
			data.add(reader.getNextTuple());
		
		AbstractPartitioner part = new RandomPartitioner(qid.split(" "), data);
		part.setNumberOfPartitions(numberOfPartitions);

		part.createPartitions();
		
		System.out.println(part.getPartitions());
		

	}

}
