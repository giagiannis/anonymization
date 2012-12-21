package anonymity.graph;

import java.io.IOException;
import java.util.LinkedList;

import anonymity.Algorithm;

import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;

/**
 * This algorithm creates a graph of tuples (edges are directed) and then applies
 * k-anonymity property by using graph algorithms and minimizing NCP values of equivalence classes.<br/>
 * TODO: Under development
 * @author Giannis Giannakopoulos
 *
 */

public class GraphCreator extends Algorithm{

	private LinkedList<GraphNode> nodes;
	
	public GraphCreator(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
	}
	
	@Override
	public void setData(EquivalenceClass data){
		super.setData(data);
		this.nodes= new LinkedList<GraphNode>();
		for(Tuple t:this.getData())
			this.nodes.add(new GraphNode(t));
	}
	
	public LinkedList<GraphNode> getNodes(){
		return this.nodes;
	}
	
	@Override
	public void run() {									// general complexity O(k*|qid|*n^2) for graph creation
		
		for(GraphNode node:this.getNodes())
			this.populateTuple(node);
		for(GraphNode n:this.nodes){
			System.out.println(n);
		}
	}
	
	private void populateTuple(GraphNode node){
		double maxDistance=0.0;
		for(int i=0;i<this.getK()-1;i++)
			maxDistance=this.getNearestNode(node, this.getNodes());
		this.addTuplesByDistance(node, this.nodes, maxDistance);			
	}
	
	private double getNearestNode(GraphNode node, LinkedList<GraphNode> nodes){				// complexity: O(k*|qid|*n)
		double minDistance=Double.MAX_VALUE;
		GraphNode closest=null;
		for(GraphNode cur: nodes){
			double distance=node.getTuple().getDistance(cur.getTuple(), this.qid, this.generalRanges);
			if(distance<minDistance && cur!=node && !node.getLinkTo().contains(cur)){
				closest=cur;
				minDistance=distance;
			}
		}
		node.addLinkTo(closest);
		closest.addLinkBy(node);
		return minDistance;
	}
	
	private void addTuplesByDistance(GraphNode node, LinkedList<GraphNode> nodes, double distance){
		for(GraphNode cur: nodes){
			if(node.getTuple().getDistance(cur.getTuple(), this.qid, this.generalRanges)==distance && cur!=node && !node.getLinkTo().contains(cur)){
				node.addLinkTo(cur);
				cur.addLinkBy(node);
			}
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DataReader reader = new DataReader(args[0]);
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<20;i++)
			data.add(reader.getNextTuple());
//		String qid="0 1 2 3 4 5 6 7 8 9";
		String qid="0 1";
		GraphCreator gr = new GraphCreator(qid, data);
		gr.setK(2);
		gr.run();
	}	


}
