package anonymity.algorithms;

import java.io.IOException;

import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;
import anonymity.Algorithm;

/**
 * This class implements a knn-like algorithm in order to achieve k-anonymity.
 * The algorithm at each step finds the k-1 nearest tuples for a given tuple (for distance measuring NCP is used). 
 * This procedure terminates when there are no tuples left ungrouped.
 * For now, the algorithm does not handle equivalence classes with more or less than k tuples (should be fixed later).
 * Also, this algorithm has a very high complexity and may be hard to be parallelized (needs to be fixed in the future). 
 * @author Giannis Giannakopoulos
 *
 */
public class KNN extends Algorithm {

	@Override
	public void run() {
		while(this.getData().size()>0)
			this.addToResults(this.findKNN(this.getData().get(0)));
		
	}
	
	private EquivalenceClass findKNN(Tuple tuple){
		EquivalenceClass results=new EquivalenceClass();
		if(this.getData().remove(tuple))
			results.add(tuple);
		
		for(int i=0;i<this.getK()-1;i++){			// k-1 iterations are executed so as to have k tuples/Equivalence Class
			Tuple closer=this.getData().get(0);
			for(Tuple tup:this.getData())
				if(tuple.getDistance(closer, this.qid, this.generalRanges)>tuple.getDistance(tup, this.qid, this.generalRanges))
					closer=tup;
			this.getData().remove(closer);
			results.add(closer);
		}
		return results;
	}
	
	public static void main(String args[]) throws IOException{
		DataReader reader = new DataReader(args[0]);
		String qid="0 1 2 3 4 5 6 7 8 9";
		EquivalenceClass data = new EquivalenceClass();
		
		for(int i=0;i<1000;i++)
			data.add(reader.getNextTuple());

		KNN knn = new KNN();
		knn.setQID(qid.split(" "));
		knn.setData(data);
		knn.setK(10);
		knn.run();
		System.out.println(knn.getResults().getGCP(knn.getQID(), knn.getRanges(), 10000));
		
	}

}
