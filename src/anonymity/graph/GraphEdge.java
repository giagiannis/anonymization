package anonymity.graph;


/**
 * Simple structure representing a graph edge with minimum allowed operations to.
 * The class contains two {@link GraphNode} elements and a weight between them.
 * Plain action are allowed to Class's objects like setting and getting the nodes and the weight.
 * @author Giannis Giannakopoulos
 *
 */
public class GraphEdge {
	private GraphNode from, to;
	private Double weight;
	
	public GraphEdge(GraphNode from, GraphNode to, Double weight){		//used like a weighted egde
		this.from=from;
		this.to=to;
		this.weight=weight;
	}
	public GraphEdge(GraphNode from, GraphNode to){						//used like an un-weighted edge
		this.from=from;
		this.to=to;
		this.weight=Double.NaN;
	}
	
	public GraphNode getFrom(){
		return this.from;
	}
	
	public GraphNode getTo(){
		return this.to;
	}
	
	public Double getWeight(){
		return this.weight;
	}
	
	public void updateWeight(Double weight){
		this.weight=weight;
	}
	
	public String toString(){
		String buffer=this.from+"\t---("+String.format("%.2f", this.weight)+")---->\t"+this.to;
		return buffer;
	}	
}
