package anonymity;

import data.EquivalenceClass;
import data.Tuple;

public class Mondrian extends Algorithm {

	private int[] generalRanges;
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
		
	}
	
	public int chooseDimension(EquivalenceClass partition){
		double maxRange=partition.getRangeByDimension(this.qid[0])/(1.0*this.generalRanges[this.qid[0]]);
		int index=this.qid[0];
		for(int i=0;i<this.qid.length;i++){
			double currentRange=partition.getRangeByDimension(this.qid[i])/(1.0*this.generalRanges[this.qid[i]]);
			System.out.println("<dim "+this.qid[i]+">:"+currentRange);
			if(currentRange>maxRange || 
					(currentRange==maxRange && this.generalRanges[index]>this.generalRanges[this.qid[i]])){
				maxRange=currentRange;
				index=this.qid[i];
			}
		}
		return index;
	}
	
	

	public static void main(String[] args){
		String[] 	t1 = {"25",	"Male",		"53710",	"Flu"},
					t2 = {"25",	"Female",	"53712",	"Hepatites"},
					t3 = {"26",	"Male",		"53711",	"Brochitis"},
					t4 = {"27",	"Male",		"53710",	"Broken Arm"},
					t5 = {"27",	"Female",	"53712",	"AIDS"},
					t6 = {"28",	"Male",		"53711",	"Hang Nail"};
		int[] qid= {0,2};
		EquivalenceClass cl = new EquivalenceClass();
		cl.add(new Tuple(t1));
		cl.add(new Tuple(t2));
		cl.add(new Tuple(t3));
		cl.add(new Tuple(t4));
		cl.add(new Tuple(t5));
		cl.add(new Tuple(t6));
		Mondrian algo = new Mondrian(qid,cl);
		System.out.println(algo.chooseDimension(algo.getData()));
	}
	
}
