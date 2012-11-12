package anonymity;

import java.util.Arrays;
import data.EquivalenceClass;
import data.Tuple;

public class Mondrian extends Algorithm {

	private int[] generalRanges;
	private boolean relaxedPartitioning=true;
	
	public Mondrian(){
		super();
	}
	
	public Mondrian(int qid[], EquivalenceClass data){
		super(qid,data);
	}
	
	@Override
	public void setData(EquivalenceClass data){
		super.setData(data);
		this.generalRanges = new int[data.get(0).getNumberOfAttributes()];
		for(int dim:this.qid)
			this.generalRanges[dim]=data.getRangeByDimension(dim);

	}

	@Override
	public void run() {
		if(!this.relaxedPartitioning)
			this.stepStrict(this.getData());
		else
			this.stepRelaxed(this.getData());
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
	
	private void stepStrict(EquivalenceClass partition){
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
			stepStrict(right);
			stepStrict(left);
		}
	}
	
	private void stepRelaxed(EquivalenceClass partition){
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
			stepStrict(right);
			stepStrict(left);
		}
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
	
/*	public static void main(String[] args){
		String[] 	t1 = {"25",	"Male",		"53710",	"Flu"},
					t2 = {"25",	"Female",	"53712",	"Hepatites"},
					t3 = {"26",	"Male",		"53711",	"Brochitis"},
					t4 = {"27",	"Male",		"53710",	"Broken Arm"},
					t5 = {"27",	"Female",	"53712",	"AIDS"},
					t6 = {"28",	"Male",		"53711",	"Hang Nail"},
					t7 = {"28",	"Male",		"53710",	"Flu"};
		int[] qid= {0,2};
		EquivalenceClass cl = new EquivalenceClass();
		cl.add(new Tuple(t1));
		cl.add(new Tuple(t2));
		cl.add(new Tuple(t3));
		cl.add(new Tuple(t4));
		cl.add(new Tuple(t5));
		cl.add(new Tuple(t6));
		cl.add(new Tuple(t7));
		Mondrian algo = new Mondrian();
		algo.setK(2);
		algo.setQID(qid);
		algo.setData(cl);
		algo.setRelaxedPartitioning();
		algo.run();
		System.out.println(algo.getResults());
		
	}*/
	
}
