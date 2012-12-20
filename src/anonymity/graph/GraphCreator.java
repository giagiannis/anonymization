package anonymity.graph;

import java.io.IOException;
import java.util.Random;

import anonymity.Algorithm;

import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;

public class GraphCreator extends Algorithm{

	public GraphCreator(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
	}
	
	@Override
	public void run() {
		for(Tuple current: this.getData())
		{
			System.out.println("Chosen tuple:\t"+current);
			System.out.println("Closest tuples:\t"+findKNN(current, this.getData()));
			System.out.println("Tuple left:\t"+this.getData().size());
		}
	}
	
	private EquivalenceClass findKNN(Tuple tuple, EquivalenceClass tuples){
		EquivalenceClass results=new EquivalenceClass();

		for(int i=0;i<this.getK();i++){
			Tuple closer=tuples.get(0);
			for(Tuple tup:tuples){
				if(tuple.getDistance(closer, this.qid, this.generalRanges)>tuple.getDistance(tup, this.qid, this.generalRanges))
					closer=tup;
			}
			tuples.remove(closer);
			results.add(closer);
		}
//		tuples.merge(results);
		return results;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DataReader reader = new DataReader(args[0]);
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<500;i++)
			data.add(reader.getNextTuple());
		String qid="0 1 2";
		GraphCreator gr = new GraphCreator(qid, data);
		gr.setK(5);
		gr.run();

	}


}
