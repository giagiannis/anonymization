package anonymity.graph;

import java.util.HashMap;
import java.util.LinkedList;

import data.EquivalenceClass;
import data.Tuple;

/**
 * Objects of this class will be nodes on a graph  
 * @author Giannis Giannakopoulos
 *
 */
public class GraphNode {

	private Tuple tuple;
	private LinkedList<GraphNode> linkTo, linkBy, link;
	private HashMap<GraphNode, Double> weights;
	private boolean visited;

	public GraphNode(Tuple tuple){
		this.tuple=tuple;
		this.linkTo= new LinkedList<GraphNode>();
		this.linkBy= new LinkedList<GraphNode>();
		this.link= new LinkedList<GraphNode>();
		this.visited=false;
		this.weights = new HashMap<GraphNode,Double>();
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
	
	public void addLinkTo(GraphNode node, double weight){
		this.linkTo.add(node);
		if(!this.link.contains(node)){
			this.weights.put(node, weight);
			this.link.add(node);
		}
	}
	
	
	public void addLinkBy(GraphNode node, double weight){
		this.linkBy.add(node);
		if(!this.link.contains(node)){
			this.weights.put(node, weight);
			this.link.add(node);
		}
	}
	
	public int getGradeIn(){
		return this.linkBy.size();
	}
	
	public int getGradeOut(){
		return this.linkTo.size();
	}
	
	public int getGrade(){
		return this.link.size();
	}
	
	public EquivalenceClass getEC(){
		EquivalenceClass cl = new EquivalenceClass();
		cl.add(this.getTuple());
		for(GraphNode node:this.getLinkTo()){
			cl.add(node.getTuple());
		}
		return cl;
	}
	
	public void setVisited(boolean flag){
		this.visited=flag;
	}
	
	public boolean getVisited(){
		return this.visited;
	}
	public String toString(){
		String buffer="("+this.getTuple().toString()+")";
		return buffer;
	/*	buffer+="\nPoint to:\t[";
		for(GraphNode node:this.linkTo){
			buffer+="("+node.getTuple().toString()+")";
			buffer+="["+this.weights.get(node)+"]";
		}
		buffer+="]";
		buffer+="\nPoint by:\t[";
		for(GraphNode node:this.linkBy){
			buffer+="("+node.getTuple().toString()+")";
			buffer+="["+this.weights.get(node)+"]";
		}
		buffer+="]";
		return buffer;*/
	}
	
}
