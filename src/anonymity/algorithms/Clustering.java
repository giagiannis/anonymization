package anonymity.algorithms;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import data.EquivalenceClass;
import data.Tuple;
import readers.ConfReader;
import readers.DataReader;
import anonymity.Algorithm;

/**
 * Clustering based algorithm for applying homogeneous k-anonymity property by local recoding.
 * @author Giannis Giannakopoulos
 *
 */

public class Clustering extends Algorithm {

	public Clustering(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
	}
	
	@Override
	public void run() {
		Set<Tuple> visited=new LinkedHashSet<Tuple>();
		EquivalenceClass notVisited = (EquivalenceClass)this.getData().clone();
		Tuple current=null;
		double start= System.currentTimeMillis();
		while(notVisited.size()>=this.getK()){							//this method runs as long as there exist plenty of data to be grouped
			current=chooseTuple(current, visited, notVisited);
			
			EquivalenceClass res = new EquivalenceClass(this.getQID());
			for(int i=0;i<this.getK();i++){
				Tuple t=this.getClosestTuple(current, visited);
				notVisited.remove(t);
				visited.add(t);
				res.add(t);
			}
			this.addToResults(res);
		}
		
		for(Tuple t:notVisited)											// the remainders (something like n mod k) are grouped to the closest groups
			this.getClosestEquivalenceClass(t).add(t);

		Algorithm.printResults(this, System.currentTimeMillis()-start);
		
	}
	
	private Tuple chooseTuple(Tuple previous, Set<Tuple> visited, EquivalenceClass notVisited){
		Tuple chosenTuple=null;
		Random rand = new Random();
		if(previous==null)
			chosenTuple=notVisited.get(rand.nextInt(notVisited.size()));
		else
			chosenTuple=previous;
		
		for(int i=0;i<3;i++)
			chosenTuple=getMostDistantTuple(chosenTuple, visited);
		return chosenTuple;
	}
	
	private Tuple getMostDistantTuple(Tuple tuple, Set<Tuple> visited){
		Double maxDistance=Double.MIN_VALUE;
		Tuple chosen=null;
		for(Tuple t:this.getData()){
			Double dist = t.getEucleidianDistance(tuple, this.getQID(), this.getRanges());
			if(!visited.contains(t) && dist>maxDistance){
				chosen=t;
				maxDistance=dist;
			}
		}
		return chosen;
	}
	
	private EquivalenceClass getClosestEquivalenceClass(Tuple t){
		EquivalenceClass res=null;
		Double minNCP=Double.MAX_VALUE;
		for(EquivalenceClass eq:this.getResults()){
			Double current=(eq.getNCPwithOtherTuple(t, this.getQID(), this.getRanges())-eq.getNCP(this.getQID(), this.getRanges()));
			if(current<minNCP){
				res=eq;
				minNCP=current;
			}
		}
		return res;
	}
	
	private Tuple getClosestTuple(Tuple tuple, Set<Tuple> visited){
		Double minDistance=Double.MAX_VALUE;
		Tuple chosen=null;
		for(Tuple t:this.getData()){
			Double dist = t.getEucleidianDistance(tuple, this.getQID(), this.getRanges());
			if(!visited.contains(t) && dist<minDistance){
				chosen=t;
				minDistance=dist;
			}
		}
		return chosen;
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/*ConfReader conf = new ConfReader(args[0]);
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
		
		Algorithm clustering = new Clustering(qid, data);
		clustering.setK(k);
		clustering.run();
		
	}

}
