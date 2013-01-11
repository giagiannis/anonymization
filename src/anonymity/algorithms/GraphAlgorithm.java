package anonymity.algorithms;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
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
		Graph g = new Graph(this.getQID(), this.getRanges());
		g.populateGraph(this.getData(), this.getK());
		
		this.runBFS(g, g.getNodes().getFirst());
	}
	
	public Set<GraphNode> runBFS(Graph g, GraphNode root){
		LinkedList<GraphNode> current=new LinkedList<GraphNode>();
		HashSet<GraphNode> visited = new HashSet<GraphNode>();
		current.add(root);
		while(!current.isEmpty()){
			GraphNode n = current.removeFirst();
			visited.add(n);
			System.out.println("\t"+n);
			n.getNeighbors().removeAll(visited); 
			current.addAll(current, n.getNeighbors());
		}
		return visited;
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
