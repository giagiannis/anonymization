package anonymity.algorithms;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;

/**
 * Implementation of a simple, linear time algorithm that separates tuples to equivalence classes based on their values at the most important 
 * attributes. At the first phase of the execution, the attributes of qid are ordered based on their importance (smaller range of values makes
 * an attribute more important. Then, all tuples are sorted based on their values and their attributes' importance and are packed in Equivalence Classes
 * of k elements. 
 * @author Giannis Giannakopoulos
 *
 */
public class SortAlgorithm extends AbstractAlgorithm {

	private EquivalenceClass sortData;
	public SortAlgorithm(String qid, EquivalenceClass data) {
		super(qid.split(" "), data);
		this.sortData = new EquivalenceClass();
	}
	
	@Override
	public void run() {
		this.sortQID();
		this.createComparableTuples();
		
		int count=0;
		EquivalenceClass bucket= new EquivalenceClass();
		for(Tuple t:this.sortData){
			if(count>=this.getK()){
				this.addToResults(bucket);
				bucket = new EquivalenceClass();
				count=0;
			}
			bucket.add(t);
			count++;
		}
		if(bucket.size()<this.getK())
			this.getResults().get(this.getResults().size()-1).merge(bucket);
		else
			this.addToResults(bucket);
	}
	
	private void sortQID(){
		TreeMap<Integer, LinkedList<Integer>> sortQid = new TreeMap<Integer, LinkedList<Integer>>();
		for(int d:this.getQID()){
			Integer range=this.getData().getRangeByDimension(d);
			if(sortQid.containsKey(range))
				sortQid.get(range).add(d);
			else{
				LinkedList<Integer> temp = new LinkedList<Integer>();
				temp.add(d);
				sortQid.put(range, temp);
			}
		}

		int i=0;
		for(Entry<Integer, LinkedList<Integer>> e:sortQid.entrySet()){
			for(Integer d:e.getValue()){
				this.qid[i]=d;
				this.generalRanges[i]=e.getKey();
				i++;
			}
		}
	}
	
	private void createComparableTuples(){
		TreeMap<TupleComparable, Integer> foo = new TreeMap<TupleComparable, Integer>();
		for(Tuple t:this.getData()){
			TupleComparable tc = new TupleComparable(qid, t);
			if(foo.containsKey(tc)){
				foo.put(tc, foo.get(tc)+1);
			}
			else
				foo.put(tc, 1);
		}
		for(Entry<TupleComparable, Integer> c:foo.entrySet())
			for(int i=0;i<c.getValue();i++)
				this.sortData.add(c.getKey().getTuple());
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	/*	
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		String qid=conf.getValue("QID");
		Integer k = new Integer(conf.getValue("K")), numberOfTuples=new Integer(conf.getValue("TUPLES"));
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<numberOfTuples;i++)
			data.add(reader.getNextTuple());
		*/
		if(args.length<2){
			System.err.println("I need arguments (-file, -qid, -k, -tuples)");
			System.exit(1);
		}
		DataReader reader = new DataReader(AbstractAlgorithm.getArgument(args, "-file"));
		String qid=AbstractAlgorithm.getArgument(args, "-qid");
		Integer k = new Integer(AbstractAlgorithm.getArgument(args, "-k")), 
				numberOfTuples=new Integer(AbstractAlgorithm.getArgument(args, "-tuples"));
		
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<numberOfTuples;i++)
			data.add(reader.getNextTuple());
		
		AbstractAlgorithm algo = new SortAlgorithm(qid, data);
		algo.setK(k);
		double start=System.currentTimeMillis();
		algo.run();
		double stop=System.currentTimeMillis()-start;
		
		AbstractAlgorithm.printResults(algo,stop);
		
	}
}

class TupleComparable implements Comparable<TupleComparable>{

	private Tuple tuple;
	private int qid[];
	public TupleComparable(int qid[], Tuple t){
		this.tuple=t;
		this.qid=qid;
	}
	
	public Tuple getTuple(){
		return this.tuple;
	}
	
	@Override
	public int compareTo(TupleComparable o) {
		for(int i:this.qid){
			if(this.tuple.getValue(i)>o.getTuple().getValue(i))
				return 1;
			else if(this.tuple.getValue(i)<o.getTuple().getValue(i))
				return -1;
		}
		return 0;
	}
}
