package anonymity.algorithms;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;


import readers.ConfReader;
import readers.DataReader;
import data.ECList;
import data.EquivalenceClass;
import data.Tuple;
import anonymity.Algorithm;

/**
 * Optimal Algorithm for k-anonymity. The complexity of the algorithm is exponential, show it must be used for really small data sets.
 * @author Giannis Giannakopoulos
 *
 */
public class OptimalAnonymity extends Algorithm {

	private ECList combinations;
	private HashMap<EquivalenceClass, ECList> foreign;
	private LinkedList<ECList> results;
	
	public OptimalAnonymity(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
		this.combinations = new ECList();
		this.results=new LinkedList<ECList>();
		this.foreign =new HashMap<EquivalenceClass, ECList>();
	}
	
	@Override
	public void run() {
		createCombinations();
		System.out.println("Combinations number:"+this.combinations.size());
		System.out.println(this.foreign.get(this.combinations.get(0)));
		
	}
	
	private void recursiveCreation(int depth){
		if(depth==0)
			return;
		else
			depth--;
		
		LinkedList<ECList> res = new LinkedList<ECList>();
		
		
		
		this.results = res;
		recursiveCreation(depth);
	}
	
	
	private void createCombinations(){
		Double count = Math.pow(2, this.getData().size());

		for(Integer i=0;i<count.intValue();i++){
			if(this.getOnes(Integer.toBinaryString(i))>=this.getK() && this.getOnes(Integer.toBinaryString(i))<=(this.getData().size()-this.getK()))
				this.combinations.add(createEC(getBoolArray(Integer.toBinaryString(i))));
		}
		for(EquivalenceClass c:this.combinations)
			this.foreign.put(c, new ECList());
		
		for(EquivalenceClass c1:this.combinations){
			for(EquivalenceClass c2:this.combinations){
				this.foreign.get(c2).add(c1);
				for(Tuple t:c1){
					if(c2.contains(t))
						this.foreign.get(c2).remove(c1);
				}
			}
		}
	}
	
	private EquivalenceClass createEC(Boolean[] flags){
		EquivalenceClass res = new EquivalenceClass();
		for(int i=0;i<flags.length;i++)
			if(flags[i])
				res.add(this.getData().get(i));		
		return res;
	}
	
	private Boolean[] getBoolArray(String array){
		while(array.length()<this.getData().size()){
			array="0"+array;
		}
		Boolean[] res = new Boolean[array.length()];
		
		for(int i=0;i<array.toCharArray().length;i++)
			if(array.toCharArray()[i]=='1')
				res[i]=true;
			else
				res[i]=false;
		return res;
	}
	
	private int getOnes(String number){
		int count=0;
		for(char c:number.toCharArray())
			if(c=='1')
				count++;
		return count;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		EquivalenceClass data = new EquivalenceClass();//.getTuples();
		for(int i=0;i<10;i++)
			data.add(reader.getNextTuple());
		String qid=conf.getValue("QID");
		Integer k = new Integer(conf.getValue("K"));
		
		Algorithm algo = new OptimalAnonymity(qid, data);
		algo.setK(k);
		algo.run();
		
//		Double gcp=algo.getResults().getGCP(algo.getQID(), algo.getRanges(), algo.getData().size());
		//System.out.println("GCP:\t"+gcp);
		//System.out.println("Sum of NCP:\t"+algo.getResults().getSumOfNCP(algo.getQID(), algo.getRanges()));
		//System.out.println("DM:\t"+algo.getResults().getDM());
		//System.out.println("Number of EC:\t"+algo.getResults().size());

	}

}
