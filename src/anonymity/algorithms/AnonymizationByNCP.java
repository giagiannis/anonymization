package anonymity.algorithms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;
import anonymity.Algorithm;

public class AnonymizationByNCP extends Algorithm {

	private HashMap<Boolean, EquivalenceClass> visitedTuples;
	
	public AnonymizationByNCP(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
		this.visitedTuples = new HashMap<Boolean, EquivalenceClass>();
		this.visitedTuples.put(false, (EquivalenceClass)this.getData().clone());
		this.visitedTuples.put(true, new EquivalenceClass());
	}
	
	@Override
	public void run() {
		Random rand = new Random();
		while(!this.visitedTuples.get(false).isEmpty()){
			Tuple t = this.visitedTuples.get(false).get(rand.nextInt(this.visitedTuples.get(false).size()));
			EquivalenceClass created=this.createECByNCP(t);
			//System.out.println("Chosen:\t"+t+" EQClass:\t"+created);
			this.addToResults(created);
		}
	}
	
	private EquivalenceClass createECByNCP(Tuple t){
		EquivalenceClass ec = new EquivalenceClass(t);
		this.visitedTuples.get(false).remove(t);
		this.visitedTuples.get(true).add(t);
		
		for(int i=0;i<this.getK()-1;i++)
		{
			Tuple choosenTuple=null;
			Double minDistance=Double.MAX_VALUE;
			for(Tuple o:this.visitedTuples.get(false)){
				ec.add(o);
				Double currentDistance=ec.getNCP(this.qid, this.generalRanges);
				if(currentDistance<=minDistance){
					choosenTuple=o;
					minDistance=currentDistance;
				}
				ec.remove(o);
			}
			ec.add(choosenTuple);
			this.visitedTuples.get(false).remove(choosenTuple);
			this.visitedTuples.get(true).add(choosenTuple);
		}
			
		if(this.visitedTuples.get(false).size()<this.getK()){
			ec.merge(this.visitedTuples.get(false));
			this.visitedTuples.get(true).merge(this.visitedTuples.get(false));
			this.visitedTuples.get(false).clear();
		}
		return ec;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		EquivalenceClass data = reader.getTuples();
		String qid=conf.getValue("QID");
		Integer k = new Integer(conf.getValue("K"));
		
		AnonymizationByNCP algo = new AnonymizationByNCP(qid, data);
		algo.setK(k);
		algo.run();
		Double gcp=algo.getResults().getGCP(algo.getQID(), algo.getRanges(), algo.getData().size());
		System.out.println("GCP:\t"+gcp);
		System.out.println("Sum of NCP:\t"+algo.getResults().getSumOfNCP(algo.getQID(), algo.getRanges()));
		System.out.println("DM:\t"+algo.getResults().getDM());
		
	}

}
