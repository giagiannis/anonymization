package anonymity.algorithms;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import data.EquivalenceClass;
import readers.ConfReader;
import readers.DataReader;
import anonymity.graph.Graph;
import anonymity.graph.GraphEdge;
import anonymity.graph.GraphNode;

public class GraphAlgorithm extends AbstractAlgorithm {

	public GraphAlgorithm(String qid, EquivalenceClass data){
		super(qid.split(" "), data);
	}
	@Override
	public void run() {
		Graph g = new Graph(this.getQID(), this.getRanges(), this.getK());
		long start=System.currentTimeMillis();
		g.populateGraph(this.getData());
		System.out.println("Graph populated at:\t"+(System.currentTimeMillis()-start)+"ms");
//		start=System.currentTimeMillis();
//		g.clearEdgesFromNodes();
//		this.runKruskal(g);
//		System.out.println("Kruskal completed at:\t"+(System.currentTimeMillis()-start)+"ms");
		
		Set<GraphNode> visited=new LinkedHashSet<GraphNode>();
		for(GraphNode n:g.getNodes()){
			if(!visited.contains(n)){
				Set<GraphNode> subGraph=this.runBFS(n);
				visited.addAll(subGraph);
				EquivalenceClass data = new EquivalenceClass();
				for(GraphNode n1:subGraph)
					data.add(n1.getTuple());
				Mondrian mondrian = new Mondrian(this.qid,data);
				mondrian.setK(this.getK());
				mondrian.setStrictPartitioning();
				mondrian.run();
				System.out.println("Mondrian (with subgraph):\t"+mondrian.getResultsGCP());
			}
			else if(visited.size()==g.getNodes().size())
					break;
		}
	//	System.out.println("Discrete parts:\t"+discreteParts);
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
		
		Mondrian mondrian = new Mondrian(qid, data);
		mondrian.setK(k);
		mondrian.setStrictPartitioning();
		mondrian.run();
		System.out.println("Mondrian:\t"+mondrian.getResultsGCP());
		
		AbstractAlgorithm algo = new GraphAlgorithm(qid,data);
		algo.setK(k);
		algo.run();
		

	}

}
