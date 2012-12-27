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
	private LinkedList<GraphNode> linkTo, linkBy, link;

	public GraphNode(Tuple tuple){
		this.tuple=tuple;
		this.linkTo= new LinkedList<GraphNode>();
		this.linkBy= new LinkedList<GraphNode>();
		this.link= new LinkedList<GraphNode>();
	}
	
	public LinkedList<GraphNode> getLinkBy() {
		return linkBy;
	}
	
	public LinkedList<GraphNode> getLinkTo() {
		return this.linkTo;
	}
	
	public LinkedList<GraphNode> getLinks(){
		return this.link;
	}

	public Tuple getTuple(){
		return this.tuple; 
	}
	
	public void addLinkTo(GraphNode node){
		this.linkTo.add(node);
		if(!this.link.contains(node))
			this.link.addLast(node);
	}
	
	public void addLinkBy(GraphNode node){
		this.linkBy.add(node);
		if(!this.link.contains(node))
			this.link.addLast(node);
	}
	
	public int getGradeIn(){
		return this.linkBy.size();
	}
	
	public int getGradeOut(){
		return this.linkTo.size();
	}
	
	public String toString(){
		String buffer="("+this.getTuple().toString()+")";
		return buffer;
	/*	buffer+="\nPoint to:\t[";
		for(GraphNode node:this.linkTo){
			buffer+="("+node.getTuple().toString()+")";
		}
		buffer+="]";
		buffer+="\nPoint by:\t[";
		for(GraphNode node:this.linkBy){
			buffer+="("+node.getTuple().toString()+")";
		}
		buffer+="]";
		return buffer;*/
	}
	
}
