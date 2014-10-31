package at.ac.tuwien.stubthecloud;

import java.util.Map;

import org.jclouds.compute.domain.Image;
import org.testng.annotations.Test;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.examples.graph.Graph;
import edu.mit.csail.sdg.squander.examples.graph.Graph.Node;

public class SquanderTest {
	// /**
	// * Graph k-Coloring problem: assign up to k different colors to graph
	// nodes
	// * such that no two adjacent nodes have the same color.
	// */
	// @Ensures({ "return.keys = this.nodes.elts",
	// "all c : return.vals | c > 0 && c <= k",
	// "all e : this.edges.elts | return.elts[e.src] != return.elts[e.dst]" })
	// @Modifies("return.elts")
	// @Options(ensureAllInts = true)
	// @FreshObjects(cls = Map.class, typeParams = { Node.class, Integer.class
	// }, num = 1)
	// public Map<Node, Integer> color(int k) {
	// return Squander.exe(this, k);
	// }
	//
	// public static void main(String[] args) {
	// Graph graph = new Graph();
	// Node a = new Node(1);
	// Node b = new Node(2);
	// Node c = new Node(3);
	// Node d = new Node(4);
	// Node e = new Node(5);
	// graph.addNode(a);
	// graph.addNode(b);
	// graph.addNode(c);
	// graph.addNode(d);
	// graph.addNode(e);
	// graph.newEdge(a, b);
	// graph.newEdge(a, c);
	// graph.newEdge(a, d);
	// graph.newEdge(a, e);
	//
	// graph.newEdge(b, c);
	// graph.newEdge(b, d);
	//
	// graph.newEdge(c, d);
	// graph.newEdge(c, e);
	//
	// graph.newEdge(e, d);
	//
	// // Graph g = readGraph(args[0]); // reads graph from file
	// // int k = Integer.parseInt(args[1]);
	// int k = 5;
	// Map<Node, Integer> colors = graph.color(k);
	// System.out.println(colors);
	// }

	@Test
	public void stupidTest() {

		String resName = Image.class.getName().replaceAll("\\.", "/")
				+ ".jfspec";
		System.out.println("SquanderTest.stupidTest() " + resName);
	}
}
