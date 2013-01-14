package anonymity.algorithms;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import data.EquivalenceClass;
import readers.ConfReader;
import readers.DataReader;
import anonymity.Algorithm;
import anonymity.graph.Graph;
import anonymity.graph.GraphEdge;
import anonymity.graph.GraphNode;

public class GraphAlgorithm extends Algorithm {

	public GraphAlgorithm(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
	}
	@Override
	public void run() {
		Graph g = new Graph(this.getQID(), this.getRanges(), this.getK());
		g.populateGraph(this.getData());
		System.out.println(g);
		g.clearEdgesFromNodes();
		this.runKruskal(g);
		System.out.println(g);
	}
	
	public void runKruskal(Graph g){
		Set<GraphEdge> edges = g.getEdges();
		int numberOfNodes=g.getNodes().size();
		LinkedList<GraphEdge> list = new LinkedList<GraphEdge>(edges);
		Set<GraphEdge> finalEdges = new LinkedHashSet<GraphEdge>();
		Collections.sort(list);
		for(GraphEdge e:list){
			e.getNodeA().addEdge(e);
			e.getNodeB().addEdge(e);
			try {
				this.runBFS(e.getNodeA(), true);
				finalEdges.add(e);
				if(finalEdges.size()>=numberOfNodes-1)
					break;
			} catch (Exception e2) {
				e.getNodeA().getEdges().remove(e);
				e.getNodeB().getEdges().remove(e);
			}
		}
		g.setEdges(finalEdges);
	}
	
	public Set<GraphNode> runBFS(GraphNode n, boolean throwExceptionAtCycle) throws Exception{
		Set<GraphNode> visited=new LinkedHashSet<GraphNode>();
		HashMap<GraphNode, GraphNode> addedBy=new HashMap<GraphNode, GraphNode>();
		
		LinkedList<GraphNode> list=new LinkedList<GraphNode>();
		list.add(n);
		while(!list.isEmpty()){
			GraphNode current=list.removeFirst();
			visited.add(current);
			Set<GraphNode> neig=current.getNeighbors();
			neig.remove(addedBy.get(current));
			if(neig.removeAll(visited) && throwExceptionAtCycle)
				throw new Exception("cycle");
			for(GraphNode no:neig)
				addedBy.put(no, current);
			list.addAll(neig);
		}
		return visited;
	}
	
	public Set<GraphNode> runBFS(GraphNode n){
		Set<GraphNode> res=null;
		try {
			res=this.runBFS(n, false);
		} catch (Exception e) {
			;
		}
		return res;
	}
	
	public boolean graphContainsCycle(Graph g){
		boolean res = false;
		try {
			Set<GraphNode> erased = new LinkedHashSet<GraphNode>();
			for (GraphNode n : g.getNodes()) {
				if (!erased.contains(n)) {
					Set<GraphNode> set;
					set = this.runBFS(n, true);
					erased.addAll(set);
				}
			}
		} catch (Exception e) {
			res=true;
		}
		return res;
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ConfReader conf = new ConfReader(args[0]);
		DataReader reader = new DataReader(conf.getValue("FILE"));
		String qid=conf.getValue("QID");
		Integer k = new Integer(conf.getValue("K")), numberOfTuples=new Integer(conf.getValue("TUPLES"));
		EquivalenceClass data = new EquivalenceClass();
		for(int i=0;i<numberOfTuples;i++)
			data.add(reader.getNextTuple());
		
		Algorithm algo = new GraphAlgorithm(qid,data);
		algo.setK(k);
		algo.run();
		

	}

}
