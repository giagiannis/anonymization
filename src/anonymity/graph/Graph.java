package anonymity.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


import data.EquivalenceClass;
import data.Tuple;

/**
 * Class representing a graph. Graphs contain vertices (nodes) and edges. The graph represented may be used
 * both as an undirected non-weighted graph or a directed weighted graph.
 * @author Giannis Giannakopoulos
 * @see {@link GraphNode}, {@link GraphEdge}
 *
 */

public class Graph {
	private LinkedList<GraphEdge> edges;
	private LinkedList<GraphNode> nodes;
	private int[] qid;
	private int[] ranges;
	
	public Graph(int qid[], int ranges[]){
		this.edges=new LinkedList<GraphEdge>();
		this.nodes=new LinkedList<GraphNode>();
		this.qid=qid;
		this.ranges=ranges;
	}
	
	
	public LinkedList<GraphEdge> getEdges() {
		return edges;
	}


	public LinkedList<GraphNode> getNodes() {
		return nodes;
	}


	public void addNode(GraphNode n){
		this.nodes.add(n);
	}
	
	public void addEdge(GraphEdge e){
		this.edges.add(e);
		e.getFrom().addEdgeTo(e);
		e.getTo().addEdgeFrom(e);
	}
	
	public void populateGraph(EquivalenceClass data, int k){
		for(Tuple t:data)
			this.addNode(new GraphNode(t));
		for(GraphNode n1:this.nodes){
			HashSet<GraphNode> set = new HashSet<GraphNode>();
			set.add(n1);
			for(int i=0;i<k-1;i++){
				GraphEdge e= this.getNearestNeighbor(n1, set);
				if(!n1.getNeighbors().contains(e.getTo()))
					this.addEdge(e);			//if an edge is already there i don't want to it
				set.add(e.getTo());
			}
		}
	}
	
	private GraphEdge getNearestNeighbor(GraphNode n, Set<GraphNode> set){
		GraphNode chosen=null;
		Double minDistance=Double.MAX_VALUE;
		for(GraphNode o:this.nodes){
			double currentDist= o.getTuple().getDistance(n.getTuple(), qid, ranges);
			if(!set.contains(o) && minDistance>currentDist){
				chosen=o;
				minDistance=currentDist;
			}
		}
		return new GraphEdge(n, chosen, minDistance);
	}
	
	public String toString(){
		String buffer="";
		for(GraphNode n:this.nodes)
			buffer+=n.toString()+"\n";
		for(GraphEdge e:this.edges){
			buffer+=e.toString()+"\n";
		}
		return buffer;
	}
	
	public EquivalenceClass findNearestNeighbor(Tuple t, EquivalenceClass data){
		EquivalenceClass res  = new EquivalenceClass();
		if(data.size()<=2)
			return res;
		
		return res;
	}
	
	/*public static void main(String [] args) throws NumberFormatException, IOException{
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		Integer numberOfTuples=new Integer(conf.getValue("TUPLES"));
		EquivalenceClass data = new EquivalenceClass();
		
		int qid[]={0,1}, ranges[]={10,20};
		for(int i=0;i<numberOfTuples;i++)
			data.add(reader.getNextTuple());
		Graph g = new Graph();
		g.populateGraph(data, qid, ranges);
	//	System.out.println(g);
	}*/
	
}
