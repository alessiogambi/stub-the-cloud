package org.jclouds.compute.declarativestub.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.jclouds.compute.domain.NodeMetadataStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import at.ac.tuwien.cloud.core.DeclarativeCloud;
import at.ac.tuwien.cloud.core.DeclarativeHardware;
import at.ac.tuwien.cloud.core.DeclarativeImage;
import at.ac.tuwien.cloud.core.DeclarativeLocation;
import at.ac.tuwien.cloud.core.DeclarativeNode;
import at.ac.tuwien.cloud.core.impl.DeclarativeCloudImpl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * Testing the Specifications of a generic cloud. {@link DeclarativeCloud} must be bridged to jclouds using an adapter,
 * which is the standard approach in jclouds.
 * 
 * @author alessiogambi
 *
 *
 *         TODO Create an abstract test for the interface and a concrete test here
 */
public class DeclarativeCloudTest {

	/*
	 * SUT. The ID generation fails if multiple tests are run concurrently ?!
	 */
	DeclarativeCloud cloud;

	@BeforeMethod(alwaysRun = true)
	public void initializeCloud() {

		// System.out.println("DeclarativeCloudStubTest.initializeCloud() " + System.getProperties());

		SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;

		Set<DeclarativeLocation> defaultDeclarativeLocations = createDefaultDeclarativeLocationsForTest();
		cloud = new DeclarativeCloudImpl(
		//
				createDefaultDeclarativeImagesForTest(defaultDeclarativeLocations),
				//
				createDefaultDeclarativeHardwaresForTest(defaultDeclarativeLocations),
				//
				defaultDeclarativeLocations
		//
		);

	}

	@AfterMethod
	public void resetTheCloud() {

	}

	@Test
	public void testInitEmptyCloud() {
		cloud = new DeclarativeCloudImpl();
		System.out.println("DeclarativeCloudTest.testInit() " + cloud);
		Assert.assertNotNull(cloud.getAllLocations());
		Assert.assertNotNull(cloud.getAllHardwares());
		Assert.assertNotNull(cloud.getAllImages());
		Assert.assertNotNull(cloud.getAllNodes());
	}

	@Test
	public void testInit() {
		// Assert.assertEquals(cloud.getAllImages(),
		// createDefaultDeclarativeImagesForTest(createDefaultDeclarativeLocationsForTest()));
		// Assert.assertEquals(cloud.getAllHardwares(),
		// createDefaultDeclarativeHardwaresForTest(createDefaultDeclarativeLocationsForTest()));
		// Assert.assertEquals(cloud.getAllLocations(), createDefaultDeclarativeLocationsForTest());
	}

	@Test
	public void testCannotCreateNodeWithMalformedInit() {
		try {
			// Split DeclarativeLocations
			Set<DeclarativeLocation> originalDeclarativeLocations = createDefaultDeclarativeLocationsForTest();
			Iterator<DeclarativeLocation> i = originalDeclarativeLocations.iterator();
			DeclarativeLocation DeclarativeLocation1 = i.next();
			DeclarativeLocation DeclarativeLocation2 = i.next();

			System.out
					.println("DeclarativeCloudTest.testMalformedInit() DeclarativeLocation 1 " + DeclarativeLocation1);
			System.out
					.println("DeclarativeCloudTest.testMalformedInit() DeclarativeLocation 2 " + DeclarativeLocation2);

			assert DeclarativeLocation1 != DeclarativeLocation2;

			// Set 1 & Set 2 == empty
			Set<DeclarativeLocation> set1 = ImmutableSet.<DeclarativeLocation> builder().add(DeclarativeLocation1)
					.build();
			Set<DeclarativeLocation> set2 = ImmutableSet.<DeclarativeLocation> builder().add(DeclarativeLocation2)
					.build();

			cloud = new DeclarativeCloudImpl(createDefaultDeclarativeImagesForTest(set1),
			//
					createDefaultDeclarativeHardwaresForTest(set2),
					//
					originalDeclarativeLocations);

			// This should fail !
			cloud.createNode();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	@Test
	public void testListNodesEmpty() {
		cloud = new DeclarativeCloudImpl();
		Assert.assertEquals(cloud.getAllNodes().size(), 0);
	}

	@Test
	public void testPreConditionFailOnCreateNode() {
		try {
			// PreConditions must fail
			cloud = new DeclarativeCloudImpl();
			cloud.createNode();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	public void testListDeclarativeImagesEmptyCloud() {
		DeclarativeCloud c = new DeclarativeCloudImpl();
		System.out.println("DeclarativeCloudTest.testInit() " + c.getAllImages());
	}

	@Test
	public void testListDeclarativeImages() {
		// Assert.assertEquals(cloud.getAllImages(),
		// createDefaultDeclarativeImagesForTest(createDefaultDeclarativeLocationsForTest()));
		Assert.assertEquals(cloud.getAllImages().size(),
				createDefaultDeclarativeImagesForTest(createDefaultDeclarativeLocationsForTest()).size());
	}

	@Test
	public void testListDeclarativeHardwares() {
		System.out.println("DeclarativeCloudTest.testListFlavors() " + cloud.getAllHardwares());

		// The Stub returns a different type of objects !
		// Assert.assertEquals(cloud.getAllHardwares(),
		// createDefaultDeclarativeHardwaresForTest(createDefaultDeclarativeLocationsForTest()));
		Assert.assertEquals(cloud.getAllHardwares().size(),
				createDefaultDeclarativeHardwaresForTest(createDefaultDeclarativeLocationsForTest()).size());
	}

	@Test
	public void testListDeclarativeLocations() {
		System.out.println("DeclarativeCloudTest.testListDeclarativeLocations() " + cloud.getAllLocations());

		// Assert.assertEquals(cloud.getAllLocations(), createDefaultDeclarativeLocationsForTest());
		Assert.assertEquals(cloud.getAllLocations().size(), createDefaultDeclarativeLocationsForTest().size());
	}

	public static Set<DeclarativeImage> createDefaultDeclarativeImagesForTest(Set<DeclarativeLocation> locations) {
		// Note we need to guarantee that DeclarativeImages are registered in their
		// DeclarativeLocations
		Builder<DeclarativeImage> images = ImmutableSet.builder();
		int id = 0;

		DeclarativeImage image = new DeclarativeImage();
		image.setId("DeclarativeImage" + ++id);
		image.setName("DeclarativeImage" + id);
		image.setLocation(locations.iterator().next());

		images.add(image);

		image = new DeclarativeImage();
		image.setId("DeclarativeImage" + ++id);
		image.setName("DeclarativeImage" + id);
		image.setLocation(locations.iterator().next());
		images.add(image);

		return images.build();
	}

	public static Set<DeclarativeLocation> createDefaultDeclarativeLocationsForTest() {
		ImmutableSet.Builder<DeclarativeLocation> locations = ImmutableSet.builder();
		int id = 1;

		DeclarativeLocation location;

		location = new DeclarativeLocation();
		location.setId("DeclarativeLocation-" + id++);
		locations.add(location);

		location = new DeclarativeLocation();
		location.setId("DeclarativeLocation-" + id++);
		locations.add(location);

		return locations.build();
	}

	public static Set<DeclarativeHardware> createDefaultDeclarativeHardwaresForTest(Set<DeclarativeLocation> locations) {
		int id = 1;
		ImmutableSet.Builder<DeclarativeHardware> flavors = ImmutableSet.builder();

		DeclarativeHardware hardware;
		hardware = new DeclarativeHardware();
		hardware.setId("DeclarativeHardware-" + ++id);
		hardware.setLocation(locations.iterator().next());
		hardware.setName("DeclarativeHardware-" + id);
		flavors.add(hardware);

		hardware = new DeclarativeHardware();
		hardware.setId("DeclarativeHardware-" + ++id);
		hardware.setLocation(locations.iterator().next());
		hardware.setName("DeclarativeHardware-" + id);
		flavors.add(hardware);

		hardware = new DeclarativeHardware();
		hardware.setId("DeclarativeHardware-" + ++id);
		hardware.setLocation(locations.iterator().next());
		hardware.setName("DeclarativeHardware-" + id);
		flavors.add(hardware);

		return flavors.build();
	}

	@Test
	public void testCreateNodeWithParameters() {
		// Prepare the inputs
		String newNodeID = UUID.randomUUID().toString();
		String _name = "test";
		String _group = "test";
		DeclarativeLocation _location = cloud.getAllLocations().iterator().next();
		DeclarativeHardware _hardware = cloud.getAllHardwares().iterator().next();
		DeclarativeImage _image = cloud.getAllImages().iterator().next();

		// Exec
		DeclarativeNode node = cloud.createNode(newNodeID, _name, _group, _location, _hardware, _image);

		System.out.println("DeclarativeCloudStubTest.testCreateNodeWithParameters() " + node);
	}

	@Test
	public void testCreateNodesWithParameters() {
		// Prepare the inputs
		String newNodeID_1 = UUID.randomUUID().toString();
		String newNodeID_2 = UUID.randomUUID().toString();
		String _name = "test";
		String _group = "test";
		DeclarativeLocation _location = cloud.getAllLocations().iterator().next();
		DeclarativeHardware _hardware = cloud.getAllHardwares().iterator().next();
		DeclarativeImage _image = cloud.getAllImages().iterator().next();

		// Exec
		DeclarativeNode node_1 = cloud.createNode(newNodeID_1, _name, _group, _location, _hardware, _image);
		DeclarativeNode node_2 = cloud.createNode(newNodeID_2, _name, _group, _location, _hardware, _image);

		System.out.println("DeclarativeCloudStubTest.testCreateNodeWithParameters() " + node_1);
		System.out.println("DeclarativeCloudStubTest.testCreateNodeWithParameters() " + node_2);
	}

	@Test
	public void testFailCreateNodeIfNoDeclarativeImages() {
		try {
			cloud = new DeclarativeCloudImpl();
			DeclarativeNode n = cloud.createNode();
			System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
			Assert.fail("pre-condition is not satisfied not raised for empty cloud!");
		} catch (RuntimeException e) {
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	@Test
	public void testCreateRandomNode() {
		DeclarativeNode n = cloud.createNode();
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
		Assert.assertNotNull(n);
		Assert.assertNotNull(n.getId());
		// Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
		// Assert SAME DeclarativeLocation

		Assert.assertEquals(n.getImage().getLocation(), n.getLocation());
		Assert.assertEquals(n.getHardware().getLocation(), n.getLocation());
	}

	@Test
	public void testCreateAndListNode() {
		DeclarativeNode newNode = cloud.createNode();
		// FIXME Exec - This must return a copy of the cloud, not the cloud itself, meaning that all its nodes must be
		// fresh
		// instances as well? !!
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
		System.out.println("DeclarativeCloudTest.testAddAndListNode() Nodes " + nodes);
		Assert.assertEquals(nodes.size(), 1);
		Assert.assertTrue(nodes.contains(newNode));

	}

	@Test
	public void testCreateNodes() {
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		// Assert SAME DeclarativeLocation
		Assert.assertEquals(n.getImage().getLocation(), n.getLocation());
		Assert.assertEquals(n.getHardware().getLocation(), n.getLocation());
		Assert.assertEquals(n1.getImage().getLocation(), n.getLocation());
		Assert.assertEquals(n1.getHardware().getLocation(), n.getLocation());
	}

	@Test
	public void testRemoveNodeByID() {
		DeclarativeNode n = cloud.createNode();
		cloud.removeNode(n.getId());
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
		System.out.println("DeclarativeCloudTest.testRemoveNode() Nodes" + nodes);
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

	// @Test
	// public void testRemoveNodes() {
	// DeclarativeNode n = cloud.createNode();
	// DeclarativeNode n1 = cloud.createNode();
	// DeclarativeNode n2 = cloud.createNode();
	//
	// // Removing n must result in a smaller cloud, but the other nodes must
	// // be there !
	// Set<DeclarativeNode> nodes = cloud.getAllNodes();
	// int oldSize = nodes.size();
	// cloud.removeNode(n);
	// nodes = cloud.getAllNodes();
	//
	// Assert.assertTrue(nodes.size() == (oldSize - 1));
	// Assert.assertTrue(containsID(nodes, n1));
	// Assert.assertTrue(containsID(nodes, n2));
	//
	// oldSize = nodes.size();
	// // Repeat again, just in case ;)
	// cloud.removeNode(n1);
	// nodes = cloud.getAllNodes();
	// Assert.assertTrue(nodes.size() == (oldSize - 1));
	// Assert.assertTrue(containsID(nodes, n2));
	//
	// // Additional comments:
	// // This might not work because getAllNodes creates every time a new
	// // structure
	// // Assert.assertTrue(c.getAllNodes().contains( n1 ) );
	//
	// // This assumes that c.getNode is ok
	// // Assert.assertTrue(c.getNode(n1.id).id == n1.id);
	// // Assert.assertTrue(c.getNode(n2.id).id == n2.id);
	// }

	@Test
	public void testRemoveNodesByID() {
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();

		System.out.println("DeclarativeCloudStubTest.testRemoveNodesByID() " + n);
		System.out.println("DeclarativeCloudStubTest.testRemoveNodesByID() " + n1);
		System.out.println("DeclarativeCloudStubTest.testRemoveNodesByID() " + n2);

		// Removing n must result in a smaller cloud, but the other nodes must
		// be there !
		Set<DeclarativeNode> nodes = cloud.getAllNodes();

		System.out.println("DeclarativeCloudStubTest.testRemoveNodesByID() Before " + nodes);
		int oldSize = nodes.size();

		cloud.removeNode(n.getId());

		nodes = cloud.getAllNodes();
		System.out.println("DeclarativeCloudStubTest.testRemoveNodesByID() After " + nodes);

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
	public void testGetImage() {
		String imageId = createDefaultDeclarativeImagesForTest(createDefaultDeclarativeLocationsForTest()).iterator()
				.next().getId();

		DeclarativeImage declarativeImage1 = cloud.getImage(imageId);
		DeclarativeImage declarativeImage2 = cloud.getImage(imageId);
		//
		Assert.assertEquals(declarativeImage1, declarativeImage2, "Not Same image object !");
		//
		Assert.assertEquals(declarativeImage1.getLocation(), declarativeImage2.getLocation(),
				"Not Same location object !");
	}

	@Test
	public void testGetHardware() {
		String hardwareId = createDefaultDeclarativeHardwaresForTest(createDefaultDeclarativeLocationsForTest())
				.iterator().next().getId();

		DeclarativeHardware declarativeHardware1 = cloud.getHardware(hardwareId);
		DeclarativeHardware declarativeHardware2 = cloud.getHardware(hardwareId);
		System.out.println("DeclarativeCloudStubTest.testGetHardware() " + declarativeHardware1);
		System.out.println("DeclarativeCloudStubTest.testGetHardware() " + declarativeHardware2);
		//
		Assert.assertEquals(declarativeHardware1, declarativeHardware2, "Not the same hardware object !");
		//
		System.out.println("DeclarativeCloudStubTest.testGetImage() " + declarativeHardware1.getLocation());
		System.out.println("DeclarativeCloudStubTest.testGetImage() " + declarativeHardware2.getLocation());
		Assert.assertEquals(declarativeHardware1.getLocation(), declarativeHardware2.getLocation(),
				"Not the same location object !");

		Assert.assertEquals(declarativeHardware1.getLocation().getId(), declarativeHardware2.getLocation().getId());
	}

	@Test
	public void testGetLocation() {
		String locationId = createDefaultDeclarativeLocationsForTest().iterator().next().getId();

		DeclarativeLocation declarativeLocation1 = cloud.getLocation(locationId);
		DeclarativeLocation declarativeLocation2 = cloud.getLocation(locationId);
		//
		Assert.assertEquals(declarativeLocation1, declarativeLocation2, "Not Same image object !");
	}

	@Test(enabled = false, description = "Now getImage has no preconditions as it returns false !")
	public void testgetImagePreCondFail() {
		try {
			String wrongID = "";
			DeclarativeImage i = cloud.getImage(wrongID);
			System.out.println("DeclarativeCloudTest.testgetImagePreCondFail() Wrong DeclarativeImage found " + i);
			Assert.fail("Pre condition not wrong ?!");
		} catch (RuntimeException e) {
			if (e.getMessage() == null || "pre".indexOf(e.getMessage()) != -1) {
				// e.printStackTrace();
				// Assert.fail("Wrong Exception Message " + e.getMessage());
				throw e;
			}
		}
	}

	@Test
	public void testGetNodeByIDReturnNull() {
		// EXEC
		DeclarativeNode _n = cloud.getNode("");
		// Assert ID
		Assert.assertNull(_n);
	}

	@Test
	public void testGetNodeByID() {
		DeclarativeNode n = cloud.createNode();
		// EXEC
		DeclarativeNode _n = cloud.getNode(n.getId());
		// Assert ID
		Assert.assertTrue(_n.getId() == n.getId());
		// Assert STATE
		// Assert.assertEquals(n.getStatus(), _n.getStatus());
		// Assert.assertEquals(_n.getStatus(), NodeMetadataStatus.RUNNING);
		// Assert DeclarativeImage
		Assert.assertEquals(n.getImage(), _n.getImage());
		// Assert DeclarativeHardware
		Assert.assertEquals(n.getHardware(), _n.getHardware());
	}

	// private boolean hasStatus(Set<DeclarativeNode> nodes, DeclarativeNode node, NodeMetadataStatus status) {
	// for (DeclarativeNode n : nodes) {
	// if (node.getId() == n.getId()) {
	// return status.equals(n.getStatus());
	// }
	// }
	// return false;
	// }

	@Test
	public void testGetNodesBySetID() {
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
		// Assert.assertTrue(hasStatus(nodes, n, NodeMetadataStatus.RUNNING));
		// Assert.assertTrue(hasStatus(nodes, n1, NodeMetadataStatus.RUNNING));
		// Assert.assertTrue(hasStatus(nodes, n2, NodeMetadataStatus.RUNNING));
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

		System.out.println("DeclarativeCloudTest.testGetNodeBySetID() NODES BY ID " + nodes);
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
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
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
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.suspendNode(n.getId());
		// //
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);

		//
		cloud.suspendNode(n1.getId());
		// //
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		//
		cloud.suspendNode(n2.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);

		System.out.println("DeclarativeCloudTest.testSuspendOnlyTheNode() " + cloud.getAllNodes());
	}

	@Test
	public void testStartSuspendNode() {
		DeclarativeNode n = cloud.createNode();
		// WARNING: This test depends on the correctness of suspend !
		cloud.suspendNode(n.getId());
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Execution
		cloud.startNode(n.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.startNode(n.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testStartOnlyTheSuspendNode() {
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();

		// WARNING: This test depends on the correctness of suspend and get !
		cloud.suspendNode(n.getId());
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);

		// Execution
		cloud.startNode(n.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.startNode(n.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Act on the others
		cloud.suspendNode(n1.getId());
		cloud.suspendNode(n2.getId());
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
	}
}
