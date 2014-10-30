package org.jclouds.compute.declarativestub.core;

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Testing the Specifications of a generic cloud. {@link DeclarativeCloud} must
 * be bridged to jclouds using an adapter, which is the standard approach in
 * jclouds.
 * 
 * @author alessiogambi
 *
 */
public class DeclarativeCloudTest {

	@Test
	public void testInit() {
		DeclarativeCloud c = new DeclarativeCloud();
		System.out.println("DeclarativeCloudTest.testInit() " + c);
	}

	@Test
	public void testListNodesEmpty() {
		DeclarativeCloud c = new DeclarativeCloud();
		System.out
				.println("DeclarativeCloudTest.testInit() " + c.getAllNodes());
	}

	@Test
	public void testAddNode() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
	}

	@Test
	public void testAddAndListNode() {
		DeclarativeCloud c = new DeclarativeCloud();
		c.createNode();
		Set<DeclarativeNode> nodes = c.getAllNodes();
		System.out.println("DeclarativeCloudTest.testAddAndListNode() Nodes "
				+ nodes);
		Assert.assertTrue(nodes.size() == 1);
	}

	@Test
	public void testAddNodes() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		System.out.println("DeclarativeCloudTest.testAddNodes() Node 1: " + n);
		System.out.println("DeclarativeCloudTest.testAddNodes() Node 2: " + n1);
	}

	@Test
	public void testRemoveNode() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		c.removeNode(n);
		Set<DeclarativeNode> nodes = c.getAllNodes();
		System.out.println("DeclarativeCloudTest.testRemoveNode() Nodes"
				+ nodes);
		Assert.assertTrue(nodes.size() == 0);
	}

	@Test
	public void testRemoveNodeByID() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		c.removeNode(n.getId());
		Set<DeclarativeNode> nodes = c.getAllNodes();
		System.out.println("DeclarativeCloudTest.testRemoveNode() Nodes"
				+ nodes);
		Assert.assertTrue(nodes.size() == 0);
	}

	private boolean containsID(Set<DeclarativeNode> nodes, DeclarativeNode node) {
		for (DeclarativeNode n : nodes) {
			if (node.getId() == n.getId()) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void testRemoveNodes() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();

		// Removing n must result in a smaller cloud, but the other nodes must
		// be there !
		Set<DeclarativeNode> nodes = c.getAllNodes();
		int oldSize = nodes.size();
		c.removeNode(n);
		nodes = c.getAllNodes();

		Assert.assertTrue(nodes.size() == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));

		oldSize = nodes.size();
		// Repeat again, just in case ;)
		c.removeNode(n1);
		nodes = c.getAllNodes();
		Assert.assertTrue(nodes.size() == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n2));

		// Additional comments:
		// This might not work because getAllNodes creates every time a new
		// structure
		// Assert.assertTrue(c.getAllNodes().contains( n1 ) );

		// This assumes that c.getNode is ok
		// Assert.assertTrue(c.getNode(n1.id).id == n1.id);
		// Assert.assertTrue(c.getNode(n2.id).id == n2.id);
	}

	@Test
	public void testRemoveNodesByID() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();

		// Removing n must result in a smaller cloud, but the other nodes must
		// be there !
		Set<DeclarativeNode> nodes = c.getAllNodes();
		int oldSize = nodes.size();
		c.removeNode(n.getId());
		nodes = c.getAllNodes();

		Assert.assertTrue(nodes.size() == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));

		oldSize = nodes.size();
		// Repeat again, just in case ;)
		c.removeNode(n1.getId());
		nodes = c.getAllNodes();
		Assert.assertTrue(nodes.size() == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n2));
	}

	@Test
	public void testGetNode() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		// Return the right node
		Assert.assertTrue(c.getNode(n).getId() == n.getId());
	}

	@Test
	public void testGetNodeByID() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		// Return the right node
		Assert.assertTrue(c.getNode(n.getId()).getId() == n.getId());
	}

	@Test
	public void testGetNodesByID() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();
		// Build the input
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(n.getId());
		ids.add(n1.getId());
		ids.add(n2.getId());

		// Exec
		Set<DeclarativeNode> nodes = c.getNodes(ids);
		// Check
		Assert.assertTrue(containsID(nodes, n));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));

	}

	@Test
	public void testGetNodeBySetID() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();
		// Build the input
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(n.getId());

		// Exec
		Set<DeclarativeNode> nodes = c.getNodes(ids);

		System.out
				.println("DeclarativeCloudTest.testGetNodeBySetID() NODES BY ID "
						+ nodes);
		// Check
		Assert.assertTrue(containsID(nodes, n));
		Assert.assertFalse(containsID(nodes, n1));
		Assert.assertFalse(containsID(nodes, n2));

	}
	
	
}
