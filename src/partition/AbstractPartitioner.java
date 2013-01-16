package partition;

import data.ECList;
import data.EquivalenceClass;

/**
 * Abstract class for all partitioning methods. This class contains (and implements) all these
 * methods that are needed by partitioning subclasses. 
 * Note: the output of all partitioning classes will be consumed be anonymization algorithms. 
 * @author Giannis Giannakopoulos
 *
 */
public abstract class AbstractPartitioner {

	
	private int qid[];
	private EquivalenceClass data;
	private ECList partitions;
	
	public AbstractPartitioner(){
		this.partitions = new ECList();
	}
	
	public AbstractPartitioner(String qid[], EquivalenceClass data){
		this.setQID(qid);
		this.setData(data);
		this.partitions = new ECList();
	}
	
	public void setQID(int qid[]){
		this.qid=qid;
	}
	
	public void setQID(String qid[]){
		this.qid=new int[qid.length];
		for(int i=0;i<qid.length;i++)
			this.qid[i]=new Integer(qid[i]);
	}
	
	public void setData(EquivalenceClass data){
		this.data=data;
	}
	
	public EquivalenceClass getData(){
		return this.data;
	}
	
	public int[] getQID(){
		return this.qid;
	}
	
	protected void addPartition(EquivalenceClass partition){
		this.partitions.add(partition);
	}
	
	public ECList getPartitions(){
		return this.partitions;
	}
	
	public abstract void createPartitions();
}