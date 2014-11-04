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

	// Test Driver
	private Set<Image> availableImages;
	// SUT
	DeclarativeCloud cloud;

	@BeforeMethod
	public void createImageInstances() {
		// Create fake images, note that this would be better to do with
		// assumptions !
		Builder<Image> images = ImmutableSet.<Image> builder();
		int id = 1;

		Image image = new ImageBuilder()
				.ids("FakeImage-" + id++)
				.name("testImage-" + id)
				// OperatingSystem is Mandatory
				.operatingSystem(
						new OperatingSystem(OsFamily.LINUX, "OsFamily.LINUX",
								"version", null, "desc", false))
				.description("desc")
				// Status is mandatory
				.status(ImageStatus.AVAILABLE)
				//
				.build();

		images.add(image);

		// image = new ImageBuilder()
		// .ids("FakeImage-" + id++)
		// .name("testImage-" + id)
		// // OperatingSystem is Mandatory
		// .operatingSystem(
		// new OperatingSystem(OsFamily.CENTOS, "OsFamily.CENTOS",
		// "version", null, "desc", false))
		// .description("desc")
		// // Status is mandatory
		// .status(ImageStatus.AVAILABLE)
		// //
		// .build();
		//
		// images.add(image);
		//
		// image = new ImageBuilder().ids("FakeImage-" + id++)
		// .name("testImage-" + id)
		// // OperatingSystem is Mandatory
		// .operatingSystem(
		// new OperatingSystem(OsFamily.WINDOWS,
		// "OsFamily.WINDOWS", "version", null, "desc",
		// false)).description("desc")
		// // Status is mandatory
		// .status(ImageStatus.AVAILABLE)
		// //
		// .build();
		//
		// images.add(image);

		availableImages = images.build();

		// By default use this one, overwrite when necessary
		cloud = new DeclarativeCloud(availableImages);
	}

	@Test
	public void testInitEmptyCloud() {
		cloud = new DeclarativeCloud();
		System.out.println("DeclarativeCloudTest.testInit() " + cloud);
	}

	@Test
	public void testInitWithImages() {
		System.out.println("DeclarativeCloudTest.testInit() " + cloud);
		Assert.assertEquals(cloud.getAllImages().size(), availableImages.size());
	}

	@Test
	public void testListNodesEmpty() {
		cloud = new DeclarativeCloud();
		Assert.assertEquals(cloud.getAllNodes().size(), 0);
	}

	@Test
	public void testPreConditionFailOnCreateNode() {
		try {
			// PreConditions must fail
			cloud = new DeclarativeCloud();
			cloud.createNode();
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertTrue(e.getMessage().contains("pre-condition"));
		}

	}

	@Test
	public void testCreateNodeWithImage() {
		// Only one image
		cloud = new DeclarativeCloud(ImmutableSet.<Image> builder()
				.add(availableImages.iterator().next()).build());

		Assert.assertEquals(cloud.getAllImages().size(), 1);

		// Exec
		DeclarativeNode n = cloud.createNode();
		//
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
		Assert.assertNotNull(n);
		Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testCreateNodeWithImages() {
		// Exec
		DeclarativeNode n = cloud.createNode();
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
		Assert.assertNotNull(n);
		Assert.assertNotNull(n.getImage());
		Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testCreateAndListNode() {
		cloud.createNode();
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
		System.out.println("DeclarativeCloudTest.testAddAndListNode() Nodes "
				+ nodes);
		Assert.assertEquals(nodes.size(), 1);
	}

	@Test
	public void testCreateNodes() {
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();

		System.out.println("DeclarativeCloudTest.testAddNodes() Nodes: \n" + n
				+ "\n" + n1);
	}

	@Test
	public void testRemoveNode() {
		DeclarativeNode n = cloud.createNode();
		//
		cloud.removeNode(n);
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
		System.out.println("DeclarativeCloudTest.testRemoveNode() Nodes"
				+ nodes);
		Assert.assertTrue(nodes.size() == 0);
	}

	@Test
	public void testRemoveNodeByID() {
		DeclarativeNode n = cloud.createNode();
		cloud.removeNode(n.getId());
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
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
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();

		// Removing n must result in a smaller cloud, but the other nodes must
		// be there !
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
		int oldSize = nodes.size();
		cloud.removeNode(n);
		nodes = cloud.getAllNodes();

		Assert.assertTrue(nodes.size() == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));

		oldSize = nodes.size();
		// Repeat again, just in case ;)
		cloud.removeNode(n1);
		nodes = cloud.getAllNodes();
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
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();

		// Removing n must result in a smaller cloud, but the other nodes must
		// be there !
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
		int oldSize = nodes.size();
		cloud.removeNode(n.getId());
		nodes = cloud.getAllNodes();

		Assert.assertTrue(nodes.size() == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));

		oldSize = nodes.size();
		// Repeat again, just in case ;)
		cloud.removeNode(n1.getId());
		nodes = cloud.getAllNodes();
		Assert.assertTrue(nodes.size() == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n2));
	}

	@Test
	public void testGetNode() {
		DeclarativeNode n = cloud.createNode();
		// Return the right node
		DeclarativeNode _n = cloud.getNode(n);
		// Assert ID
		Assert.assertTrue(_n.getId() == n.getId());
		// Assert STATE
		Assert.assertEquals(cloud.getNode(n).getStatus(),
				NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testGetNodeByID() {
		DeclarativeNode n = cloud.createNode();
		// Return the right node
		DeclarativeNode _n = cloud.getNode(n);
		// Assert ID
		Assert.assertTrue(_n.getId() == n.getId());
		// Assert STATE
		Assert.assertEquals(cloud.getNode(n).getStatus(),
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
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();
		// Build the input
		Set<String> ids = new HashSet<String>();
		ids.add(n.getId());
		ids.add(n1.getId());
		ids.add(n2.getId());

		// Exec
		Set<DeclarativeNode> nodes = cloud.getNodes(ids);
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
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();
		// Build the input
		Set<String> ids = new HashSet<String>();
		ids.add(n.getId());

		// Exec
		Set<DeclarativeNode> nodes = cloud.getNodes(ids);

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
		DeclarativeNode n = cloud.createNode();
		//
		cloud.suspendNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
	}

	@Test
	public void testSuspendOnlyTheNode() {
		// WARNING: This test depends on the correctness of getNode!

		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();
		//
		cloud.suspendNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),

		// Idempotence
		cloud.suspendNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);

		//
		cloud.suspendNode(n1.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),
		//
		cloud.suspendNode(n2.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);

		System.out.println("DeclarativeCloudTest.testSuspendOnlyTheNode() "
				+ cloud.getAllNodes());
	}

	@Test
	public void testStartSuspendNode() {
		DeclarativeNode n = cloud.createNode();
		// WARNING: This test depends on the correctness of suspend !
		cloud.suspendNode(n.getId());
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		// Execution
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testStartOnlyTheSuspendNode() {
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();

		// WARNING: This test depends on the correctness of suspend and get !
		cloud.suspendNode(n.getId());
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);

		// Execution
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		// Act on the others
		cloud.suspendNode(n1.getId());
		cloud.suspendNode(n2.getId());
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(),
				NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(),
				NodeMetadataStatus.SUSPENDED);
	}
}
