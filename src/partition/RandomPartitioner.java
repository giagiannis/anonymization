package partition;

import java.io.IOException;
import java.util.Random;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;

/**
 * Random partitioner. Data tuples are spread with equal odds to any of the partition. For large datasets
 * the partitions are equally sized.
 * @author Giannis Giannakopoulos
 *
 */
public class RandomPartitioner extends AbstractPartitioner {

	public RandomPartitioner(){
		super();
	}
	
	public RandomPartitioner(String[] qid, EquivalenceClass data) {
		super(qid, data);
	}

	@Override
	public void createPartitions() {
		for(int i=0;i<this.getNumberOfPartitions();i++)
			this.addPartition(new EquivalenceClass());
		Random rand = new Random();
		for(Tuple t:this.getData())
			this.getPartitions().get(rand.nextInt(this.getNumberOfPartitions())).add(t);
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
		
		AbstractPartitioner part = new RandomPartitioner(qid.split(" "), data);
		part.setNumberOfPartitions(numberOfPartitions);

		part.createPartitions();
		
		AbstractPartitioner.presentPartitions(part.getPartitions());
	}
}
