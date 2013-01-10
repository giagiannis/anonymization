package anonymity.algorithms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

//import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;
import anonymity.Algorithm;

public class AnonymizationByNCP extends Algorithm {

	private HashMap<Boolean, EquivalenceClass> visitedTuples;
	private int count;
	
	public AnonymizationByNCP(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
		this.visitedTuples = new HashMap<Boolean, EquivalenceClass>();
		this.visitedTuples.put(false, (EquivalenceClass)this.getData().clone());
		this.visitedTuples.put(true, new EquivalenceClass());
	}
	
	@Override
	public void run() {
		this.count=0;
		Random rand = new Random();
//		Tuple previous, next;
		while(this.visitedTuples.get(false).size()>=this.getK()){
			Tuple t = this.visitedTuples.get(false).get(rand.nextInt(this.visitedTuples.get(false).size()));
			EquivalenceClass created=this.createECByNCP(t);
			if(created!=null)
				this.addToResults(created);
		}
		for(Tuple t:this.visitedTuples.get(false))
			this.findMostCloseEC(t).add(t);
	}
	
	private EquivalenceClass createECByNCP(Tuple t){
		EquivalenceClass ec = new EquivalenceClass(this.getQID());
		ec.add(t);
		this.visitedTuples.get(false).remove(t);
		this.visitedTuples.get(true).add(t);
		
		for(int i=0;i<this.getK()-1;i++)										// k iterations
		{
			Tuple choosenTuple=null;
			Double minDistance=Double.MAX_VALUE;
			for(Tuple o:this.visitedTuples.get(false)){								// O(n) iterations
				Double currentDistance=ec.getNCPwithOtherTuple(o,this.qid, this.generalRanges);			// 	|qid|
				if(currentDistance<=minDistance){
					choosenTuple=o;
					minDistance=currentDistance;
				}
			}
			ec.add(choosenTuple);
			this.visitedTuples.get(false).remove(choosenTuple);
			this.visitedTuples.get(true).add(choosenTuple);
		}
		
		if(ec.getNCP(qid, generalRanges)>this.getResults().getMaxNCP(qid, generalRanges)){	//O(n/k)			//this method takes time!!!
			EquivalenceClass other=this.findMostCloseEC(t);
			other.add(t);
			if(ec.getNCP(qid, generalRanges)>=other.getNCP(this.qid, this.generalRanges)){
				this.count++;
				ec.remove(t);
	//			this.visitedTuples.get(true).removeAll(ec);				//cannot be used, the complexity is WAAY to high O(n^2)
				this.visitedTuples.get(false).addAll(ec);
				ec=null;
			}
			else{
				other.remove(t);
			}
		}
		return ec;
	}
	
	private EquivalenceClass findMostCloseEC(Tuple tuple){
		EquivalenceClass cl = this.getResults().get(0);
		double minDiff=Double.MAX_VALUE;
		for(EquivalenceClass eqcl: this.getResults()){					//O(n/k)
			double t1=eqcl.getNCP(this.qid, this.generalRanges), t2;		//O(k*|qid|)
			t2=eqcl.getNCPwithOtherTuple(tuple, this.qid, this.generalRanges);					//O(k*|qid|)
			if(t2-t1<minDiff){
				cl=eqcl;
				minDiff=t2-t1;
			}//may add one or two constr aints for such actions
		}
		return cl;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
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
		
		AnonymizationByNCP algo = new AnonymizationByNCP(qid, data);
		algo.setK(k);
		double start=System.currentTimeMillis();
		algo.run();
		double stop=System.currentTimeMillis()-start;
		
		Algorithm.printResults(algo, stop);

		/*
		 * System.out.print("\t\tCount:"+algo.count+"");
		 * int count=0;
		for(EquivalenceClass a:algo.getResults())
			if(a.size()>2*algo.getK())
				count++;
			System.out.print("\tBig classes:"+count+"\n");
		 */
				
	}

}
