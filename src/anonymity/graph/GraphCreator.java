package anonymity.graph;

import java.io.IOException;
import java.util.LinkedList;

import anonymity.Algorithm;

import readers.DataReader;
import data.EquivalenceClass;
import data.Tuple;

/**
 * This algorithm creates a graph of tuples (edges are directed) and then applies
 * k-anonymity property by using graph algorithms and minimizing NCP values of equivalence classes.
 * Firstly, the algorithm constructs a graph based on the tuples by using GraphNode class. Graph
 * creation is performed by populating nodes with directed edges that point out the locality between nodes.	 
 * TODO: Under development
 * @author Giannis Giannakopoulos
 * @see {@link GraphNode}, {@link Algorithm}
 *
 */

public class GraphCreator extends Algorithm{

	private LinkedList<GraphNode> nodes;
	
	public GraphCreator(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
	}
	
	@Override
	public void setData(EquivalenceClass data){
		super.setData(data);
		this.nodes= new LinkedList<GraphNode>();
		for(Tuple t:this.getData())
			this.nodes.add(new GraphNode(t));
	}
	
	public LinkedList<GraphNode> getNodes(){
		return this.nodes;
	}
	
	@Override
	public void run() {									// graph creation: O(k*|qid|*n^2) 
		this.populateNodes();
		LinkedList<LinkedList<GraphNode>> trees = new LinkedList<LinkedList<GraphNode>>();
		for(GraphNode node:this.getNodes()){
			boolean contains=false;
			for(LinkedList<GraphNode> tree:trees){
				if(tree.contains(node)){
					contains=true;
					break;
				}
			}
			if(!contains)
				trees.add(this.runBFS(node));
		}
		for(LinkedList<GraphNode> tree:trees){
			System.out.println(tree.size());
		}
	}
	
	private void populateNodes(){				// complexity: O(k*|qid|*n^2)
		for(GraphNode node:this.nodes){			// complexity: O(k*|qid|*n) (per iteration)
			double maxDistance=0.0;
			for(int i=0;i<this.getK()-1;i++)				// k
				maxDistance=this.getNearestNode(node, this.getNodes());
			this.addTuplesByDistance(node, this.nodes, maxDistance);
		}
	}
	
	private double getNearestNode(GraphNode node, LinkedList<GraphNode> nodes){				
		double minDistance=Double.MAX_VALUE;
		GraphNode closest=null;
		for(GraphNode cur: nodes){				// n
			double distance=node.getTuple().getDistance(cur.getTuple(), this.qid, this.generalRanges);		//|qid|
			if(distance<minDistance && cur!=node && !node.getLinkTo().contains(cur)){
				closest=cur;
				minDistance=distance;
			}
		}
		node.addLinkTo(closest);
		closest.addLinkBy(node);
		return minDistance;
	}
	
	private void addTuplesByDistance(GraphNode node, LinkedList<GraphNode> nodes, double distance){
		for(GraphNode cur: nodes){
			if(node.getTuple().getDistance(cur.getTuple(), this.qid, this.generalRanges)==distance && cur!=node && !node.getLinkTo().contains(cur)){
				node.addLinkTo(cur);
				cur.addLinkBy(node);
			}
		}
	}
	
	public LinkedList<GraphNode> runBFS(GraphNode node){
		LinkedList<GraphNode> visited = new LinkedList<GraphNode>(), nodes = new LinkedList<GraphNode>();
		nodes.add(node);
//		visited.add(node);
		while(!nodes.isEmpty()){
			GraphNode current=nodes.get(0);
			visited.add(current);
			nodes.remove(current);
			for(GraphNode linkto:current.getLinks()){
				if(!visited.contains(linkto) && !nodes.contains(linkto))
					nodes.addLast(linkto);
			}
		}
		return visited;
	}
	
	public LinkedList<GraphNode> runDFS(GraphNode node){
		LinkedList<GraphNode> visited = new LinkedList<GraphNode>(), nodes = new LinkedList<GraphNode>();
		nodes.add(node);
//		visited.add(node);
		while(!nodes.isEmpty()){
			GraphNode current=nodes.get(0);
			visited.add(current);
			nodes.remove(current);
			for(GraphNode linkto:current.getLinks()){
				if(!visited.contains(linkto) && !nodes.contains(linkto))
					nodes.addFirst(linkto);
			}
		}
		return visited;
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		DataReader reader = new DataReader(args[0]);
//		EquivalenceClass data = new EquivalenceClass();
//		for(int i=0;i<20;i++)
//			data.add(reader.getNextTuple());
		String qid="0 1 2 3 4 5 6 7 8 9";
//		String qid="0 1";
//		String qid="8 7 9";
		GraphCreator gr = new GraphCreator(qid, reader.getTuples());
		gr.setK(3);
		gr.run();
	}	
}
