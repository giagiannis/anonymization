package anonymity.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
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
	private int qid[], ranges[], k;
	private Set<GraphNode> nodes;
	private Set<GraphEdge> edges;
	
	public Graph(){			//used when tree is populated outside the class
		this.nodes=new LinkedHashSet<GraphNode>();
		this.edges=new LinkedHashSet<GraphEdge>();
	}
	
	public Graph(int qid[], int ranges[], int k){
		this.qid=qid;
		this.ranges=ranges;
		this.k=k;
		this.nodes=new LinkedHashSet<GraphNode>();
		this.edges=new LinkedHashSet<GraphEdge>();
	}
	
	public Set<GraphNode> getNodes(){
		return this.nodes;
	}
	
	public Set<GraphEdge> getEdges(){
		return this.edges;
	}
	
	public void addEdge(GraphEdge e){
		this.edges.add(e);
		e.getNodeA().addEdge(e);
		e.getNodeB().addEdge(e);
	}
	
	public void removeEdge(GraphEdge e){
		this.edges.remove(e);
		e.getNodeA().getEdges().remove(e);
		e.getNodeB().getEdges().remove(e);
	}
	
	public void addNode(GraphNode n){
		this.nodes.add(n);
	}
	
	public void populateGraph(EquivalenceClass tuples){
		for(Tuple t:tuples)
			this.nodes.add(new GraphNode(t));
		
		Set<GraphNode> addedNodes= new LinkedHashSet<GraphNode>();
		for(GraphNode n:this.nodes){
			addedNodes.clear();
			addedNodes.add(n);
			for(int i=0;i<this.k-1;i++){
				GraphEdge e=getNearestNode(n, addedNodes);
				if(!n.getNeighbors().contains(e.getNodeB()))
					this.addEdge(e);
				addedNodes.add(e.getNodeB());
			}
		}
	}
	
	public void clearEdgesFromNodes(){
		for(GraphNode n:this.nodes)
			n.clearEdges();
	}
	
	public void setEdges(Set<GraphEdge> edges){
		this.edges=edges;
	}
	
	private GraphEdge getNearestNode(GraphNode n, Set<GraphNode> exceptions){
		GraphNode chosen=null;
		Double minDistance=Double.MAX_VALUE, currentDistance;
		for(GraphNode o:this.nodes){
			currentDistance=n.getTuple().getDistance(o.getTuple(), qid, ranges);
			if(!exceptions.contains(o) && currentDistance< minDistance){
				minDistance=currentDistance;
				chosen=o;
			}
		}
		return new GraphEdge(n, chosen, minDistance);
	}
	
	public String toString(){
		String buffer="";
		for(GraphNode n:this.nodes)
			buffer+=n+"\t"+n.getGrade()+"\n";
		buffer+=this.edges;
		return buffer;
	}
}
