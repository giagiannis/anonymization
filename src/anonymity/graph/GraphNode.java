package anonymity.graph;

import java.util.LinkedHashSet;
import java.util.Set;

import data.Tuple;

/**
 * Class that represents a graph node (or vertex). The instance of this class contain a {@link Tuple} object
 * as the "data" part of the node, and a list of edges with which the node is connected to other nodes.  
 * @author Giannis Giannakopoulos
 *
 */
public class GraphNode {

	private Set<GraphEdge> edges;
	private Tuple tuple;
	
	public GraphNode(Tuple t){
		this.edges = new LinkedHashSet<GraphEdge>();
		this.tuple=t;
	}
	
	public Tuple getTuple(){
		return this.tuple;
	}
	
	public void addEdge(GraphEdge e){
		this.edges.add(e);
	}
	
	public Set<GraphEdge> getEdges(){
		return this.edges;
	}
	
	public int getGrade(){
		return this.edges.size();
	}
	
	public Set<GraphNode> getNeighbors(){
		LinkedHashSet<GraphNode> neighbors= new LinkedHashSet<GraphNode>();
		for(GraphEdge e:this.edges){
			if(e.getNodeA().equals(this))
				neighbors.add(e.getNodeB());
			else
				neighbors.add(e.getNodeA());
		}
		return neighbors;
	}
	
	public Set<GraphEdge> getEdgesToNeighbors(Set<GraphNode> neighbors){
		Set<GraphEdge> set  =new LinkedHashSet<GraphEdge>();
		for(GraphEdge e:this.edges)
			if(neighbors.contains(e.getNodeA()) || neighbors.contains(e.getNodeB()))
				set.add(e);
		return set;
	}
	
	public void clearEdges(){
		this.edges.clear();
	}
	
	public String toString(){
		String buffer="";
		buffer+="["+this.getTuple()+"]";
		return buffer;
	}
}
