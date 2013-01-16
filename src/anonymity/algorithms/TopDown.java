package anonymity.algorithms;

import java.io.IOException;

import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;

public class TopDown extends AbstractAlgorithm {

	@Override
	public void run() {
		
	}
	
	public void step(EquivalenceClass partition, Tuple refPoint){
		
	}
	
	
	public static void main(String[] args) throws IOException{
		DataReader reader = new DataReader(args[0]);
		//String qid="0 1 2 3 4 5 6 7 8 9";
		String qid="0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";
		EquivalenceClass data = new EquivalenceClass();
		
		for(int i=0;i<10000;i++)
			data.add(reader.getNextTuple());

		TopDown topDown = new TopDown();
		topDown.setQID(qid.split(" "));
		topDown.setData(data);
		topDown.setK(10);
		topDown.run();
		System.out.println(topDown.getResults().getGCP(topDown.getQID(), topDown.getRanges(), 10000));
		
	}

}
