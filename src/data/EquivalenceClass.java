package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.plaf.metal.MetalIconFactory.TreeLeafIcon;

/**
 * @author Giannis Giannakopoulos	
 * <p>
 * Container class that is used to keep data-tuples. It extends ArrayList class.
 * </p>
 * */
public class EquivalenceClass extends ArrayList<Tuple> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[] qid =null;
	private HashMap<Integer, Integer> max,min;
	
	public EquivalenceClass(){
		super();
	}
	
	public EquivalenceClass(Tuple t){
		super();
		this.add(t);
	}
	
	public EquivalenceClass(int[] qid){
		super();
		this.qid=qid;
		this.max =new HashMap<Integer, Integer>();
		this.min = new HashMap<Integer, Integer>();
	}
	
	public boolean add(Tuple t){
		super.add(t);
		if(this.qid!=null){
			for(int i:this.qid){
				if((!this.max.containsKey(i))  ||  (this.max.get(i)<t.getValue(i)))
					this.max.put(i, t.getValue(i));
				if(!this.min.containsKey(i) || this.min.get(i)>t.getValue(i))
					this.min.put(i, t.getValue(i));
			}
		}
		return true;
	}
	
	public boolean remove(Object t){
		super.remove(t);
		if(this.qid!=null){
			this.max.clear();
			this.min.clear();
			for(Tuple a:this){
				for(int i:this.qid){
					if((!this.max.containsKey(i))  ||  (this.max.get(i)<a.getValue(i)))
						this.max.put(i, a.getValue(i));
					if(!this.min.containsKey(i) || this.min.get(i)>a.getValue(i))
						this.min.put(i, a.getValue(i));
				}
			}
		}
		return true;
	}
	
	public int getRangeByDimension(int dimension){
		if(this.qid!=null){
			return this.max.get(dimension)-this.min.get(dimension);
		}
		else
		{
			int max=this.get(0).getValue(dimension),min=this.get(0).getValue(dimension);
			for(Tuple t:this){
				int tempValue=t.getValue(dimension);
				if(tempValue>max)
					max=tempValue;
				if(tempValue<min)
					min=tempValue;
			}
			return max-min;
		}
	}
	
	public int[] getValuesByDimension(int dimension){
		int[] values = new int[this.size()];
		for(int i=0;i<this.size();i++)
			values[i]=this.get(i).getValue(dimension);
		return values;
		
	}
	
	public int getNumberOfTuples(){
		return this.size();
	}
	
	public double getNCP(int qid[], int ranges[]){
		double sum=0;
		for(int i=0;i<qid.length;i++)
			sum+=getRangeByDimension(qid[i])/(1.0*ranges[i]);
		return sum;
	}
	
	public double getNCPwithOtherTuple(Tuple t, int qid[], int ranges[]){
		double sum=0;
		if(this.qid!=null)
		{
			int count=0;
			for(int d:this.qid){
				int min=this.min.get(d), max=this.max.get(d);
				if(max<t.getValue(d))
					max=t.getValue(d);
				if(min>t.getValue(d))
					min=t.getValue(d);
				sum+=(max-min)*1.0/(ranges[count]);
				count++;
			}
		}
		else{
			this.add(t);
			sum=this.getNCP(qid, ranges);
			this.remove(t);
		}
		return sum;
	
	}
	
	public String toString(){
		String buffer="[";
		int i;
		if(this.size()>=1){
			for(i=0;i<this.size()-1;i++)
				buffer+="("+this.get(i)+"),";
			buffer+="("+this.get(i)+")]";
		}
		else
			buffer+="]";
		return buffer;
	}
	
	public String toStringFileFormat(){
		String buffer="";
		for(Tuple cur:this){
			buffer+=cur.toString()+"\n";
		}
		return buffer;
	}
	
	public void merge(EquivalenceClass other){
		for(Tuple tuple:other)
			this.add(tuple);
	}
	
	public boolean containsTuple(Tuple t){
		if(this.qid!=null){
			for(int d:this.qid)
				if(t.getValue(d)<this.min.get(d) || t.getValue(d)>this.max.get(d))
					return false;
		}
		else
			return false;
		return true;
	}
	
	public ECList split(int qid[], int ranges[]){			//splits one equivalence class to two almost equal equivalence classes
		if(this.qid==null)
			return null;
		ECList results = new ECList();
		double maxNormalDistance=Double.MIN_VALUE;
		int dim=-1;
		for(int i=0;i<this.qid.length;i++){
			double current=(this.max.get(this.qid[i])-this.min.get(this.qid[i]))/(1.0*ranges[i]);
			if(current>maxNormalDistance){
				dim=this.qid[i];
				maxNormalDistance=current;
			}
		}
		TreeMap<Integer, EquivalenceClass> sorter = new TreeMap<Integer, EquivalenceClass>();
		for(Tuple t:this){
			int value=t.getValue(dim);
			if(sorter.containsKey(value))
				sorter.get(value).add(t);
			else
				sorter.put(value, new EquivalenceClass(t));
		}
		EquivalenceClass temp = new EquivalenceClass();
		for(EquivalenceClass eq:sorter.values())
			temp.merge(eq);
		EquivalenceClass one = new EquivalenceClass();
		for(int i=0;i<temp.size()/2;i++)
			one.add(temp.get(i));
		results.add(one);
		
		one.clear();
		for(int i=temp.size()/2;i<temp.size();i++)
			one.add(temp.get(i));
		results.add(one);
		return results;
	}
	
	public static void main(String[] args){
		String a="1 2", b = "2 2", c="2 1", d="1 20";
		int qid[]={0,1}, ranges[]={1,19};
		EquivalenceClass ec = new EquivalenceClass(qid);
		ec.add(new Tuple(a.split(" ")));
		ec.add(new Tuple(b.split(" ")));
		ec.add(new Tuple(c.split(" ")));
		Tuple t=new Tuple(d.split(" "));
		ec.add(t);
		for(int i:qid)
			System.out.println(ec.getRangeByDimension(i));
		ec.remove(t);
		for(int i:qid)
			System.out.println(ec.getRangeByDimension(i));
		System.out.println(ec.getNCP(qid, ranges));
		System.out.println(ec.getNCPwithOtherTuple(t,qid, ranges));
	}
}
