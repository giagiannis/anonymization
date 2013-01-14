package anonymity.graph;


/**
 * Simple structure representing a graph edge with minimum allowed operations nodeB.
 * The class contains two {@link GraphNode} elements and a weight between them.
 * Plain action are allowed from Class's objects like setting and getting the nodes and the weight.
 * @author Giannis Giannakopoulos
 *
 */
public class GraphEdge implements Comparable<GraphEdge>{

	private GraphNode nodeA, nodeB;
	private Double weight;
	
	public GraphEdge(GraphNode nodeA, GraphNode nodeB, Double weight){
		this.nodeA=nodeA;
		this.nodeB=nodeB;
		this.weight=weight;
	}
	
	public GraphNode getNodeA(){
		return this.nodeA;
	}
	
	public GraphNode getNodeB(){
		return this.nodeB;
	}
	
	public Double getWeight(){
		return this.weight;
	}

	@Override
	public int compareTo(GraphEdge o) {
		if(this.getWeight()>o.getWeight())
			return 1;
		else if(this.getWeight()<o.getWeight())
			return -1;
		else
			return 0;
	}
	
	
	public String toString(){
		return this.nodeA+"--("+String.format("%.2f", this.weight)+")-->"+this.nodeB;
	}
}
