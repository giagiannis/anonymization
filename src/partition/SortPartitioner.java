package partition;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;

public class SortPartitioner extends AbstractPartitioner {

	private LinkedList<TupleComparable> sortedData;
	private int[] sortedQID;
	
	public SortPartitioner(){
		this.sortedData = new LinkedList<TupleComparable>();
	}

	public SortPartitioner(String[] qid, EquivalenceClass data) {
		super(qid, data);
		this.sortedData = new LinkedList<TupleComparable>();
	}

	@Override
	public void createPartitions() {
		this.sortQID();
		this.sortData();
		int count;
		
		count=this.sortedData.size()/this.getNumberOfPartitions();
		System.out.println(this.getData().size());
		System.out.println(this.sortedData.size());
		int index=0;
		EquivalenceClass partition=new EquivalenceClass();
		for(TupleComparable t:this.sortedData){
			if(index%count==0 && index!=0){
				this.addPartition(partition);
				partition = new EquivalenceClass();
			}
			else if(index==count)
				break;
			partition.add(t.getTuple());
			index++;
		}
		if(index%count==0)
			this.addPartition(partition);
		else
			this.getPartitions().get(this.getPartitions().size()-1).merge(partition);
		
	}
	
	private void sortQID(){
		this.sortedQID = new int[this.getQID().length];
		
		TreeMap<Integer, LinkedList<Integer>> dim = new TreeMap<Integer, LinkedList<Integer>>();
		for(int d:this.getQID()){
			int range=this.getData().getRangeByDimension(d);
			if(dim.containsKey(range))
				dim.get(range).add(d);
			else{
				LinkedList<Integer> temp = new LinkedList<Integer>();
				temp.add(d);
				dim.put(range, temp);
			}
		}
		
		int i=0;
		for(Entry<Integer, LinkedList<Integer>> current: dim.entrySet()){
			for(Integer dimension:current.getValue()){
				this.sortedQID[i]=dimension;
				i++;
			}
		}
	}
	
	private void sortData(){
		for(Tuple t:this.getData())
			this.sortedData.add(new TupleComparable(t, this.sortedQID));
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

class TupleComparable implements Comparable<TupleComparable>{

	private int qid[];
	private Tuple tuple;
	
	public TupleComparable(Tuple tuple, int [] qid) {
		this.qid=qid;
		this.tuple=tuple;
	}
	
	public Tuple getTuple(){
		return this.tuple;
	}
	
	public String toString(){
		return this.tuple.toString();
	}
	@Override
	public int compareTo(TupleComparable o) {
		for(int d:this.qid){
			if(this.getTuple().getValue(d)>o.getTuple().getValue(d))
				return 1;
			else if((this.getTuple().getValue(d)<o.getTuple().getValue(d)))
				return -1;
		}
		return 0;
	}
	
}
