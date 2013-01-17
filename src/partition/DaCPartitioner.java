package partition;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import readers.ConfReader;
import readers.DataReader;
import data.ECList;
import data.EquivalenceClass;
import data.Tuple;

/**
 * This partitioner is used for partitioning raw data tuples before executing anonymization algorithm. 
 * DAC stands for Divide and Conquer. This partitioner divides tuples recursively based on their median 
 * value for a chosen dimension. Currently, the number of partitions that will be exported will be 2^n (n>1).
 * 
 * @author Giannis Giannakopoulos
 *
 */
public class DaCPartitioner extends AbstractPartitioner {

	public DaCPartitioner() {
		super();
	}

	public DaCPartitioner(String qid, EquivalenceClass data) {
		super(qid.split(" "), data);

	}

	@Override
	public void createPartitions() {
		ECList current = new ECList();
		current.add(this.getData());
		double log=Math.log(this.getNumberOfPartitions())/Math.log(2);
		int loops = (int)log;
		for(int i=0;i<loops;i++)
			current=this.partitionStep(current);
		
		this.setPartitions(current);

	}
	
	private ECList partitionStep(ECList partitions){
		ECList producedPartitions = new ECList();
		for(EquivalenceClass part:partitions)
			producedPartitions.merge(this.split(part, this.chooseDimension(part)));
		return producedPartitions;
	}
	
	private int chooseDimension(EquivalenceClass partition){
		int dimensionIndex=-1, index=0;
		int ranges[]=this.getRanges();
		double maxDistance=Double.MIN_VALUE;
		for(int d:this.getQID()){
			double current=partition.getRangeByDimension(d)/(ranges[index]*1.0);
			if((current > maxDistance) || (current==maxDistance && ranges[index]<ranges[dimensionIndex])){
				maxDistance=current;
				dimensionIndex=index;
			}
			index++;
		}
		return this.getQID()[dimensionIndex];
	}
	
	private ECList split(EquivalenceClass partition, int chosenDimension){
		ECList splits = new ECList();
		LinkedList<TupleSortable> sort = new LinkedList<TupleSortable>();
		for(Tuple t:partition)
			sort.add(new TupleSortable(t, chosenDimension));
		Collections.sort(sort);
		int median=sort.size()/2;
		EquivalenceClass one = new EquivalenceClass(this.getQID());
		for(TupleSortable t:sort.subList(0, median))
			one.add(t.getTuple());
		splits.add(one);
		EquivalenceClass two= new EquivalenceClass(this.getQID());
		for(TupleSortable t:sort.subList(median, sort.size()))
			two.add(t.getTuple());
		splits.add(two);
		return splits;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		Integer numberOfPartitions=new Integer(conf.getValue("PARTITIONS"));
		Integer tuples = new Integer(conf.getValue("TUPLES"));
		String qid = conf.getValue("QID");
		EquivalenceClass data = new EquivalenceClass();
		
		for(int i=0;i<tuples;i++)
			data.add(reader.getNextTuple());
		
		AbstractPartitioner part = new DaCPartitioner(qid, data);
		part.setNumberOfPartitions(numberOfPartitions);

		part.createPartitions();
		
		AbstractPartitioner.presentPartitions(part.getPartitions());

	}
}

class TupleSortable implements Comparable<TupleSortable>{
	private Tuple tuple;
	private int d;

	public TupleSortable(Tuple t, int dim) {
		this.tuple=t;
		this.d=dim;
	}

	public Tuple getTuple(){
		return this.tuple;
	}
	
	@Override
	public int compareTo(TupleSortable o) {
		if(this.getTuple().getValue(this.d) > o.getTuple().getValue(this.d))
			return 1;
		else if(this.getTuple().getValue(this.d) < o.getTuple().getValue(this.d))
			return -1;
		else
			return 0;
	}
}
