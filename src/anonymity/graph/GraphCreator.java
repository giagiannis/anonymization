package anonymity.graph;

import java.io.IOException;
import java.util.Random;

import anonymity.Algorithm;

import readers.DataReader;
import data.EquivalenceClass;

public class GraphCreator extends Algorithm{

	
	public GraphCreator(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
	}
	
	@Override
	public void run() {
		
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

	}


}
