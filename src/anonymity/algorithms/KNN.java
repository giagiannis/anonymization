package anonymity.algorithms;

import java.io.IOException;

import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;
import anonymity.Algorithm;

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
		//String qid="0 1 2 3 4 5 6 7 8 9";
		String qid="0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";
		EquivalenceClass data = new EquivalenceClass();
		
		for(int i=0;i<10000;i++)
			data.add(reader.getNextTuple());

		KNN knn = new KNN();
		knn.setQID(qid.split(" "));
		knn.setData(data);
		knn.setK(10);
		knn.run();
		System.out.println(knn.getResults().getGCP(knn.getQID(), knn.getRanges(), 10000));
		
	}

}
