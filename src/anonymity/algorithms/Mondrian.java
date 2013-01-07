package anonymity.algorithms;

import java.util.Arrays;

import anonymity.Algorithm;
import anonymity.graph.GraphCreator;

import readers.ConfReader;
import readers.DataReader;
import data.EquivalenceClass;
import data.ECList;
import data.Tuple;

/**
 * Implementation of Mondrian algorithm. The algorithm may run in different modes and manners (eg, BFS or DFS creation of recursion tree,
 * relaxed or strict partitioning, etc).
 * @author Giannis Giannakopoulos
 *
 */
public class Mondrian extends Algorithm {

	private boolean relaxedPartitioning=true;
	private boolean bfs=true;
	
	public Mondrian(){ 
		super();   
	}   
	
	public Mondrian(String qid, EquivalenceClass data){
		super(qid.split(" "),data);
	}
	
	public Mondrian(int qid[], EquivalenceClass data){
		super(qid,data);
	}
	
	@Override
	public void run() {
		ECList current = new ECList();
		current.add(this.getData());
		
		while(!current.isEmpty()){
			ECList temp;
			if(!this.relaxedPartitioning)
				temp=this.stepStrict(current.remove(0));
			else
				temp=this.stepRelaxed(current.remove(0));
			int index=0;
			if(this.bfs)
				index = current.size();
			for(EquivalenceClass cl:temp)
				current.add(index, cl);
		}
		
	}
	
	private int chooseDimension(EquivalenceClass partition){
		double maxRange=partition.getRangeByDimension(this.qid[0])/(1.0*this.generalRanges[this.qid[0]]);
		int index=this.qid[0];
		for(int i=0;i<this.qid.length;i++){
			double currentRange=partition.getRangeByDimension(this.qid[i])/(1.0*this.generalRanges[this.qid[i]]);
			if(currentRange>maxRange || 
					(currentRange==maxRange && this.generalRanges[index]<this.generalRanges[this.qid[i]])){
				maxRange=currentRange;
				index=this.qid[i];
			}
		}
		return index;
	}
	
	private ECList stepStrict(EquivalenceClass partition){
		ECList res = new ECList();
		if(partition.size()<2*this.getK())
			this.addToResults(partition);
		else{
			int dim=this.chooseDimension(partition);
			int median = this.findMedian(partition,dim);
			EquivalenceClass right = new EquivalenceClass(),left = new EquivalenceClass();
			for(Tuple t:partition){
				if(t.getValue(dim)<=median)
					right.add(t);
				else
					left.add(t);
			}
			while(left.size()<this.getK()){
				for(int i=0;i<right.size();i++){
					if(right.get(i).getValue(dim)==median){
						left.add(right.remove(i));
					}
				}
				
			}
			res.add(left);
			res.add(right);
		}
		return res;
	}
	
	private ECList stepRelaxed(EquivalenceClass partition){
		ECList res = new ECList();
		if(partition.size()<2*this.getK())
			this.addToResults(partition);
		else{
			int dim=this.chooseDimension(partition);
			int median = this.findMedian(partition,dim);
			EquivalenceClass right = new EquivalenceClass(),left = new EquivalenceClass(), center=new EquivalenceClass();
			for(Tuple t:partition){
				if(t.getValue(dim)<median)
					right.add(t);
				else if(t.getValue(dim)>median)
					left.add(t);
				else
					center.add(t);
			}
			for(int i=0;i<center.size();i++){
				if(right.size()<left.size())
					right.add(center.get(i));
				else
					left.add(center.get(i));
			}
			res.add(left);
			res.add(right);
		}
		return res;
	}
	
	private int findMedian(EquivalenceClass partition, int dimension){
		int[] values=partition.getValuesByDimension(dimension);
		Arrays.sort(values);
		return values[partition.size()/2];
	}
	
	public void setRelaxedPartitioning(){
		this.relaxedPartitioning=true;
	}
	
	public void setStrictPartitioning(){
		this.relaxedPartitioning=false;
	}
	
	public void setBFS(){
		this.bfs=true;
	}
	
	public void setDFS(){
		this.bfs=false;
	}
	
	public static void main(String[] args) throws Exception{
		
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		String qid=conf.getValue("QID");
		EquivalenceClass data=reader.getTuples();
		Mondrian algo = new Mondrian(qid, data);
		algo.setK(new Integer(conf.getValue("K")));
		algo.setStrictPartitioning();
		algo.setDFS();
		algo.run();
		System.out.println(algo.getResults().getGCP(algo.getQID(), algo.getRanges(), data.size()));
		System.out.println("Sum of NCP:\t"+algo.getResults().getSumOfNCP(algo.getQID(), algo.getRanges()));
		System.out.println("DM:\t"+algo.getResults().getDM());
	}
}
