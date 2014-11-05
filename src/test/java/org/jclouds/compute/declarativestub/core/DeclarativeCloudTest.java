package org.jclouds.compute.declarativestub.core;

import java.util.HashSet;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageStatus;
import org.jclouds.compute.domain.NodeMetadataStatus;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
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

	/*
	 * SUT
	 */
	DeclarativeCloud cloud;

	@BeforeMethod
	public void initializeCloud() {
		cloud = new DeclarativeCloud(
		//
				createDefaultImagesForTest(),
				//
				createDefaultFlavorsForTest(),
				//
				createDefaultLocationsForTest()
		//
		);
	}

	@Test
	public void testInitEmptyCloud() {
		cloud = new DeclarativeCloud();
		System.out.println("DeclarativeCloudTest.testInit() " + cloud);
	}

	@Test
	public void testInitWithImages() {
		System.out.println("DeclarativeCloudTest.testInit() " + cloud);
		Assert.assertEquals(cloud.getAllImages().size(), this
				.createDefaultImagesForTest().size());
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

	public void testListImagesEmptyCloud() {
		DeclarativeCloud c = new DeclarativeCloud();
		System.out.println("DeclarativeCloudTest.testInit() "
				+ c.getAllImages());
	}

	@Test
	public void testListImages() {
		Assert.assertEquals(cloud.getAllImages(), createDefaultImagesForTest());
	}

	@Test
	public void testListFlavors() {
		System.out.println("DeclarativeCloudTest.testListFlavors() "
				+ cloud.getAllFlavors());

		Assert.assertEquals(cloud.getAllFlavors(),
				createDefaultFlavorsForTest());
	}

	@Test
	public void testListLocations() {
		System.out.println("DeclarativeCloudTest.testListLocations() "
				+ cloud.getAllLocations());

		Assert.assertEquals(cloud.getAllLocations(),
				createDefaultLocationsForTest());
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

	private Set<Location> createDefaultLocationsForTest() {
		ImmutableSet.Builder<Location> locations = ImmutableSet.builder();
		int id = 1;
		locations.add(new LocationBuilder().id("" + id)
				.description("Location-description").scope(LocationScope.ZONE)
				.build());
		return locations.build();
	}

	private Set<Hardware> createDefaultFlavorsForTest() {
		int id = 1;
		ImmutableSet.Builder<Hardware> flavors = ImmutableSet.builder();

		flavors.add(new HardwareBuilder()
				.ids("" + id)
				.name("small")
				.processors(ImmutableList.of(new Processor(1, 1.0)))
				.ram(1740)
				.volumes(
						ImmutableList.<Volume> of(new VolumeImpl((float) 160,
								true, false))).build());

		flavors.add(new HardwareBuilder()
				.ids("" + id++)
				.name("medium")
				.processors(ImmutableList.of(new Processor(4, 1.0)))
				.ram(7680)
				.volumes(
						ImmutableList.<Volume> of(new VolumeImpl((float) 850,
								true, false))).build());

		flavors.add(new HardwareBuilder()
				.ids("" + id++)
				.name("large")
				.processors(ImmutableList.of(new Processor(8, 1.0)))
				.ram(15360)
				.volumes(
						ImmutableList.<Volume> of(new VolumeImpl((float) 1690,
								true, false))).build());

		return flavors.build();
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
	public void testCreateNodeWithImage() {
		// Only one image

		Image image = createDefaultImagesForTest().iterator().next();
		Hardware flavor = createDefaultFlavorsForTest().iterator().next();
		Location location = createDefaultLocationsForTest().iterator().next();
		cloud = new DeclarativeCloud(//
				ImmutableSet.<Image> builder().add(image).build(),//
				ImmutableSet.<Hardware> builder().add(flavor).build(),//
				ImmutableSet.<Location> builder().add(location).build());

		Assert.assertEquals(cloud.getAllImages().size(), 1);

		// Exec
		DeclarativeNode n = cloud.createNode();
		Assert.assertNotNull(n);
		Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
		// NOT SURE THE EQUALS IS FINE !
		Assert.assertEquals(n.getImage(), image);
	}

	public void testCreateNode() {
		DeclarativeNode n = cloud.createNode();
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

		System.out.println("DeclarativeCloudTest.testAddNodes() Node 1: " + n);
		System.out.println("DeclarativeCloudTest.testAddNodes() Node 2: " + n1);
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

	/**
	 * Test Utility method
	 * 
	 * @param nodes
	 * @param node
	 * @return
	 */
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
	public void testGetImage() {
		String imageId = createDefaultImagesForTest().iterator().next().getId();

		Image image = cloud.getImage(imageId);
		// Assert ID
		Assert.assertEquals(image.getId(), imageId);
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
				NodeMetadataStatus.RUNNING);
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
				NodeMetadataStatus.RUNNING);
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
