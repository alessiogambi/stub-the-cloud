package org.jclouds.compute.declarativestub.core;

import java.util.HashSet;
import java.util.Set;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageStatus;
import org.jclouds.compute.domain.NodeMetadataStatus;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Testing the Specifications of a generic cloud. {@link DeclarativeCloud} must
 * be bridged to jclouds using an adapter, which is the standard approach in
 * jclouds.
 * 
 * @author alessiogambi
 *
 */
public class DeclarativeCloudTest {

	/**
	 * SUT
	 */
	DeclarativeCloud c;

	@BeforeMethod
	public void initializeCloud() {
		c = new DeclarativeCloud(createDefaultImagesForTest());
	}

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
	public void testListImagesEmptyCloud() {
		DeclarativeCloud c = new DeclarativeCloud();
		System.out.println("DeclarativeCloudTest.testInit() "
				+ c.getAllImages());
	}

	@Test
	public void testListImages() {
		Builder<Image> images = ImmutableSet.builder();
		int id = 1;
		Image image = new ImageBuilder()
				.ids(id++ + "")
				.name("OS-NAME")
				.location(null)
				.operatingSystem(
						new OperatingSystem(OsFamily.LINUX, "desc", "version",
								null, "desc", false)).description("desc")
				.status(ImageStatus.AVAILABLE).build();

		images.add(image);
		DeclarativeCloud c = new DeclarativeCloud(images.build());
		System.out.println("DeclarativeCloudTest.testInit() "
				+ c.getAllImages());
		Assert.assertEquals(c.getAllImages(), images.build());
	}

	private Set<Image> createDefaultImagesForTest() {
		Builder<Image> images = ImmutableSet.builder();
		int id = 1;
		Image image = new ImageBuilder()
				.ids(id++ + "")
				.name("OS-NAME")
				.location(null)
				.operatingSystem(
						new OperatingSystem(OsFamily.LINUX, "desc", "version",
								null, "desc", false)).description("desc")
				.status(ImageStatus.AVAILABLE).build();

		images.add(image);

		image = new ImageBuilder()
				.ids(id++ + "")
				.name("OS-NAME")
				.location(null)
				.operatingSystem(
						new OperatingSystem(OsFamily.WINDOWS, "desc",
								"version", null, "desc", true))
				.description("desc").status(ImageStatus.AVAILABLE).build();

		images.add(image);
		return images.build();
	}

	@Test
	public void testFailCreateNodeIfNoImages() {
		try {
			DeclarativeCloud c = new DeclarativeCloud();
			DeclarativeNode n = c.createNode();
			System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
			Assert.fail("pre-condition is not satisfied not raised for empty cloud!");
		} catch (RuntimeException e) {
			Assert.assertTrue(e.getMessage().contains(
					"pre-condition is not satisfied"));
		}

	}

	@Test
	public void testCreateNode() {
		DeclarativeNode n = c.createNode();
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
		Assert.assertNotNull(n);
		Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);

	}

	@Test
	public void testCreateAndListNode() {
		c.createNode();
		Set<DeclarativeNode> nodes = c.getAllNodes();
		System.out.println("DeclarativeCloudTest.testAddAndListNode() Nodes "
				+ nodes);
		Assert.assertTrue(nodes.size() == 1);
	}

	@Test
	public void testCreateNodes() {
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		System.out.println("DeclarativeCloudTest.testAddNodes() Node 1: " + n);
		System.out.println("DeclarativeCloudTest.testAddNodes() Node 2: " + n1);
	}

	@Test
	public void testRemoveNode() {
		DeclarativeNode n = c.createNode();
		c.removeNode(n);
		Set<DeclarativeNode> nodes = c.getAllNodes();
		System.out.println("DeclarativeCloudTest.testRemoveNode() Nodes"
				+ nodes);
		Assert.assertTrue(nodes.size() == 0);
	}

	@Test
	public void testRemoveNodeByID() {
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
		DeclarativeNode n = c.createNode();
		// Return the right node
		DeclarativeNode _n = c.getNode(n);
		// Assert ID
		Assert.assertTrue(_n.getId() == n.getId());
		// Assert STATE
		Assert.assertEquals(c.getNode(n).getStatus(),
				NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testGetNodeByID() {
		DeclarativeNode n = c.createNode();
		// Return the right node
		DeclarativeNode _n = c.getNode(n);
		// Assert ID
		Assert.assertTrue(_n.getId() == n.getId());
		// Assert STATE
		Assert.assertEquals(c.getNode(n).getStatus(),
				NodeMetadataStatus.RUNNING);
	}

	private boolean hasStatus(Set<DeclarativeNode> nodes, DeclarativeNode node,
			NodeMetadataStatus status) {
		for (DeclarativeNode n : nodes) {
			if (node.getId() == n.getId()) {
				return status.equals(n.getStatus());
			}
		}
		return false;
	}

	@Test
	public void testGetNodesByID() {
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();
		// Build the input
		Set<String> ids = new HashSet<String>();
		ids.add(n.getId());
		ids.add(n1.getId());
		ids.add(n2.getId());

		// Exec
		Set<DeclarativeNode> nodes = c.getNodes(ids);
		// Check ID
		Assert.assertTrue(containsID(nodes, n));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));
		// Check States
		Assert.assertTrue(hasStatus(nodes, n, NodeMetadataStatus.RUNNING));
		Assert.assertTrue(hasStatus(nodes, n1, NodeMetadataStatus.RUNNING));
		Assert.assertTrue(hasStatus(nodes, n2, NodeMetadataStatus.RUNNING));
	}

	@Test
	public void testGetNodeBySetID() {
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();
		// Build the input
		Set<String> ids = new HashSet<String>();
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

	@Test
	public void testSuspendNode() {
		DeclarativeNode n = c.createNode();
		//
		c.suspendNode(n.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
	}

	@Test
	public void testSuspendOnlyTheNode() {
		// WARNING: This test depends on the correctness of getNode!

		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();
		//
		c.suspendNode(n.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);

		// Idempotence
		c.suspendNode(n.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);

		//
		c.suspendNode(n1.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);

		//
		c.suspendNode(n2.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);

		System.out.println("DeclarativeCloudTest.testSuspendOnlyTheNode() "
				+ c.getAllNodes());
	}

	@Test
	public void testStartSuspendNode() {
		DeclarativeNode n = c.createNode();
		// WARNING: This test depends on the correctness of suspend !
		c.suspendNode(n.getId());
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		// Execution
		c.startNode(n.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		// Idempotence
		c.startNode(n.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testStartOnlyTheSuspendNode() {
		DeclarativeNode n = c.createNode();
		DeclarativeNode n1 = c.createNode();
		DeclarativeNode n2 = c.createNode();

		// WARNING: This test depends on the correctness of suspend and get !
		c.suspendNode(n.getId());
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);

		// Execution
		c.startNode(n.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		// Idempotence
		c.startNode(n.getId());
		//
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		// Act on the others
		c.suspendNode(n1.getId());
		c.suspendNode(n2.getId());
		Assert.assertEquals(c.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(c.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(c.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
	}
}
