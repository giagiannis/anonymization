package anonymity.graph;

import data.Tuple;

/**
 * Class that represents a graph node (or vertex). The instance of this class contain a {@link Tuple} object
 * as the "data" part of the node, and a list of edges with which the node is connected to other nodes.  
 * @author Giannis Giannakopoulos
 *
 */
public class GraphNode {

	private Tuple tuple;
	
	public GraphNode(){
		
	}
	
	public GraphNode(Tuple t){
		this.tuple=t;
	}
	
	public Tuple getTuple(){
		return this.tuple;
	}
}
