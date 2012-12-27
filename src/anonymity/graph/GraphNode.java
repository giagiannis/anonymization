package anonymity.graph;

import java.util.LinkedList;
import data.Tuple;

/**
 * Objects of this class will be nodes on a graph  
 * @author Giannis Giannakopoulos
 *
 */
public class GraphNode {

	private Tuple tuple;
	private LinkedList<GraphNode> linkTo, linkBy;

	public GraphNode(Tuple tuple){
		this.tuple=tuple;
		this.linkTo= new LinkedList<GraphNode>();
		this.linkBy= new LinkedList<GraphNode>();
	}
	
	public LinkedList<GraphNode> getLinkBy() {
		return linkBy;
	}
	
	public LinkedList<GraphNode> getLinkTo() {
		return this.linkTo;
	}

	public Tuple getTuple(){
		return this.tuple; 
	}
	
	public void addLinkTo(GraphNode node){
		this.linkTo.add(node);
	}
	
	public void addLinkBy(GraphNode node){
		this.linkBy.add(node);
	}
	
	public String toString(){
		String buffer="";
		buffer+=this.getTuple().toString()+"\nPoint to:\t[";
		for(GraphNode node:this.linkTo){
			buffer+="("+node.getTuple().toString()+")";
		}
		buffer+="]";
		buffer+="\nPoint by:\t[";
		for(GraphNode node:this.linkBy){
			buffer+="("+node.getTuple().toString()+")";
		}
		buffer+="]";
		return buffer;
	}
	
}
