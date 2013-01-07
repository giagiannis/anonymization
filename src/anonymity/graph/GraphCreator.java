package anonymity.graph;

import java.io.IOException;
import java.util.LinkedList;

import anonymity.Algorithm;

import readers.ConfReader;
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
		this.populateNodes();		//creates edges
		LinkedList<GraphNode> roots = new LinkedList<GraphNode>();
		for(GraphNode n:this.getNodes())
			if(!n.getVisited())
				roots.add(this.runBFS(n).get(0));
		for(GraphNode n:this.getNodes())
			n.setVisited(false);
		
		System.out.println(roots);
		for(GraphNode root:this.getNodes())
			travelToGraph(root);
	}
	
	public void travelToGraph(GraphNode node){
		System.out.println("Managing as root:\t"+node);
		EquivalenceClass cl = new EquivalenceClass();
		node.setVisited(true);
		cl.add(node.getTuple());
		GraphNode next=null;
		
		for(GraphNode n:node.getLinkTo()){
			if(!n.getVisited())
				cl.add(n.getTuple());
			n.setVisited(true);
		}
		
		for(GraphNode n:node.getLinkTo()){
			for(GraphNode j:n.getLinkTo())
				if(!j.getVisited())
					next=j;
		}
		System.out.println(cl);
		if(next!=null){
			travelToGraph(next);
		}
			
		
	}
	
	/* Old runner method using cuts to single-dimension list
	 * LinkedList<LinkedList<GraphNode>> trees = new LinkedList<LinkedList<GraphNode>>();
		for(GraphNode n:this.getNodes()){
			if(!n.getVisited())
				trees.add(this.runBFS(n));
		}
		System.out.println("Number of trees:\t"+trees.size());
		for(GraphNode n:this.getNodes())
			n.setVisited(false);
		for(LinkedList<GraphNode> l:trees)
			createRes(l);
		
		int sum=0;
		for(EquivalenceClass cl:this.getResults())
			if(cl.size()<this.getK())
				sum+=1;
		System.out.println("Classes with less than k tuples:\t"+sum+"/"+this.getResults().size());
		System.out.println("GCP:\t"+this.getResults().getGCP(this.qid, this.generalRanges, this.getData().size()));
	 */
	
	public void createRes(LinkedList<GraphNode> nodes){
		EquivalenceClass cl = new EquivalenceClass();
		for(int i=0;i<nodes.size();i++){
			if(i<=nodes.size()-this.getK() && cl.size()>=this.getK()){
				this.addToResults(cl);
				cl = new EquivalenceClass();
			}
			//if(i<this.nodes.size()-2*this.getK())
			cl.add(nodes.get(i).getTuple());

		}
		this.addToResults(cl);
	}
	
	public EquivalenceClass createEquivalenceClass(GraphNode node){
		EquivalenceClass cl = new EquivalenceClass();
		cl.add(node.getTuple());
		for(GraphNode n:node.getLinks())
			if(!n.getVisited()){
				cl.add(n.getTuple());
				n.setVisited(true);
			}
		return cl; 
	}
	
	private void populateNodes(){				// complexity: O(k*|qid|*n^2)
		for(GraphNode node:this.nodes){			// complexity: O(k*|qid|*n) (per iteration)
			double maxDistance=0.0;
			for(int i=0;i<this.getK()-1;i++)				// k
				maxDistance=this.getNearestNode(node, this.getNodes());
	//		LinkedList<GraphNode> closeNodes=
			this.getNodesByDistance(node, this.nodes, maxDistance);
/*			node.getLinkTo().removeAll(closeNodes);
			
			while(node.getLinkTo().size()<this.getK()-1){				// add nodes based on the NCP value
				EquivalenceClass cl = node.getEC();
				double minNCP=Double.MAX_VALUE;
				GraphNode chosenTuple=null;
				for(GraphNode nod: closeNodes){
					cl.add(nod.getTuple());
					if(minNCP>cl.getNCP(this.qid, this.generalRanges)){
						minNCP=cl.getNCP(this.qid, this.generalRanges);
						chosenTuple=nod;
					}
					cl.remove(nod.getTuple());
				}
				node.addLinkTo(chosenTuple);
			}
		*/
			
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
		node.addLinkTo(closest,minDistance);
		closest.addLinkBy(node, minDistance);
		return minDistance;
	}
	
	private LinkedList<GraphNode> getNodesByDistance(GraphNode node, LinkedList<GraphNode> nodes, double distance){
		LinkedList<GraphNode> closestNodes = new LinkedList<GraphNode>();
		for(GraphNode cur: nodes){
			if(node.getTuple().getDistance(cur.getTuple(), this.qid, this.generalRanges)==distance && cur!=node && !node.getLinkTo().contains(cur)){
				node.addLinkTo(cur, distance);
				cur.addLinkBy(node, distance);
				//closestNodes.add(cur);
			}
		}
		return closestNodes;
	}
	
	public LinkedList<GraphNode> runBFS(GraphNode node){
		LinkedList<GraphNode> visited = new LinkedList<GraphNode>(), nodes = new LinkedList<GraphNode>();
		nodes.add(node);
//		visited.add(node);
		while(!nodes.isEmpty()){
			GraphNode current=nodes.get(0);
			current.setVisited(true);
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
			current.setVisited(true);
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
		ConfReader conf = new ConfReader(args[0]);
		
		DataReader reader = new DataReader(conf.getValue("FILE"));
		String qid=conf.getValue("QID");
		GraphCreator gr = new GraphCreator(qid, reader.getTuples());
		gr.setK(new Integer(conf.getValue("K")));
		gr.run();
	}	
}
