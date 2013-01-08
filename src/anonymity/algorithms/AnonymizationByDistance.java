package anonymity.algorithms;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map.Entry;
import java.util.TreeMap;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;
import anonymity.Algorithm;

/**
 * This class implements a knn-like algorithm in order to achieve k-anonymity.
 * The algorithm at each step finds the k-1 nearest tuples for a given tuple (for distance measuring NCP is used). 
 * This procedure terminates when there are no tuples left ungrouped.
 * For now, the algorithm does not handle equivalence classes with more or less than k tuples (should be fixed later).
 * Also, this algorithm has high complexity (O(n^2)) and may be hard to be parallelized (needs to be fixed in the future). 
 * @author Giannis Giannakopoulos
 *
 */
public class AnonymizationByDistance extends Algorithm {
	
	private HashMap<Tuple, Boolean> isAdded;
	private HashMap<Boolean, EquivalenceClass> tuplesErased;

	public AnonymizationByDistance(String qid, EquivalenceClass tuples){
		super(qid.split(" "), tuples);
		this.isAdded = new HashMap<Tuple,Boolean>();
		this.tuplesErased = new HashMap<Boolean, EquivalenceClass>();
		this.tuplesErased.put(false, new EquivalenceClass());
		this.tuplesErased.put(true, new EquivalenceClass());
		for(Tuple t:tuples){
			isAdded.put(t, false);
			this.tuplesErased.get(false).add(t);
		}
	}
	
	@Override
	public void run() {
		Random rand = new Random();
		while(!tuplesErased.get(false).isEmpty()){
			Tuple t = this.tuplesErased.get(false).get(rand.nextInt(this.tuplesErased.get(false).size()));
			EquivalenceClass ec=this.createECByDistance(t);
			if(ec!=null)
				this.addToResults(ec);
		}
		
	}
	
	private EquivalenceClass createECByDistance(Tuple tuple){
		if(this.isAdded.get(tuple))
			return null;
		EquivalenceClass ec = new EquivalenceClass();
		TreeMap<Double, EquivalenceClass> map = new TreeMap<Double, EquivalenceClass>();
		for(Tuple t:this.getData()){
			if(!this.isAdded.get(t))
			{
				double dist=tuple.getDistance(t, this.qid, this.generalRanges);
				if(!map.containsKey(dist) )
					map.put(dist, new EquivalenceClass(t));
				else
					map.get(dist).add(t);
			}
		}
		for(Entry<Double, EquivalenceClass> s:map.entrySet()){
			for(Tuple t:s.getValue()){
				if(ec.size()<this.getK()){
					ec.add(t);
					this.isAdded.put(t, true);
					this.tuplesErased.get(false).remove(t);
					this.tuplesErased.get(true).add(t);
				}
				else 
					break;
			}
			if(ec.size()>=this.getK())
				break;
		}
		int notAdded=0;
		for(Entry<Tuple, Boolean> e:this.isAdded.entrySet()){
			if(!e.getValue())
				notAdded+=1;
		}

		if(notAdded<this.getK()){
			for(Entry<Tuple, Boolean> e:this.isAdded.entrySet()){
				if(!e.getValue()){
					ec.add(e.getKey());
					e.setValue(true);
					this.tuplesErased.get(false).remove(e.getKey());
					this.tuplesErased.get(true).add(e.getKey());
				}
			}
		}
		//System.out.println(ec.getNCP(this.getQID(), this.getRanges()));
		return ec;
	}
	
	public static void main(String args[]) throws IOException{
		/*	ConfReader conf = new ConfReader(args[0]);
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
		DataReader reader = new DataReader(Algorithm.getArgument(args, "-file"));
		String qid=Algorithm.getArgument(args, "-qid");
		Integer k = new Integer(Algorithm.getArgument(args, "-k")), 
				numberOfTuples=new Integer(Algorithm.getArgument(args, "-tuples"));
		
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<numberOfTuples;i++)
			data.add(reader.getNextTuple());
		
		
		AnonymizationByDistance algo = new AnonymizationByDistance(qid, data);
		algo.setK(k);
		double start=System.currentTimeMillis();
		algo.run();
		double stop = System.currentTimeMillis()-start;
		
		Algorithm.printResults(algo, stop);
	}
}
