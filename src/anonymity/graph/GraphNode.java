package anonymity.graph;

import java.util.HashSet;

import data.Tuple;

/**
 * Class that represents a graph node (or vertex). The instance of this class contain a {@link Tuple} object
 * as the "data" part of the node, and a list of edges with which the node is connected to other nodes.  
 * @author Giannis Giannakopoulos
 *
 */
public class GraphNode {

	private HashSet<GraphEdge> edgesOut, edgesIn, edgesUndirected;
	private HashSet<GraphNode> neighbors;
	private Tuple tuple;
	
	public GraphNode(Tuple t){
		this.tuple=t;
		this.edgesOut= new HashSet<GraphEdge>();
		this.edgesIn= new HashSet<GraphEdge>();
		this.edgesUndirected= new HashSet<GraphEdge>();
		this.neighbors = new HashSet<GraphNode>();
	}
	
	public Tuple getTuple(){
		return this.tuple;
	}
	
	public void addEdgeTo(GraphEdge e){
		if(!this.edgesOut.contains(e))
			this.edgesOut.add(e);
		this.addEdge(e);

	}
	
	public HashSet<GraphNode> getNeighbors(){
		return this.neighbors;
	}
	
	public void addEdgeFrom(GraphEdge e){
		if(!this.edgesIn.contains(e))
			this.edgesIn.add(e);
		this.addEdge(e);
	}
	
	public final void addEdge(GraphEdge e){
		if(!this.edgesUndirected.contains(e))
			this.edgesUndirected.add(e);
		if(e.getFrom().equals(this))
			this.neighbors.add(e.getTo());
		else
			this.neighbors.add(e.getFrom());
	}

	public HashSet<GraphEdge> getEdgesTo() {
		return edgesOut;
	}

	public HashSet<GraphEdge> getEdgesFrom() {
		return edgesIn;
	}

	public HashSet<GraphEdge> getEdgesUndirected() {
		return edgesUndirected;
	}
	
	public String toString(){
		String buffer="";
		buffer="["+this.getTuple().toString()+"]";
		return buffer;
	}
	
}
