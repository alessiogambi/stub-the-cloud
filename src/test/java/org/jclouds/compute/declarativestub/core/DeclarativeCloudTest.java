package org.jclouds.compute.declarativestub.core;

import java.util.HashSet;
import java.util.Iterator;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.serializer.HardwareSer;
import edu.mit.csail.sdg.squander.serializer.ImageSer;
import edu.mit.csail.sdg.squander.serializer.LocationSer;
import edu.mit.csail.sdg.squander.serializer.special.ObjSerFactory;

/**
 * Testing the Specifications of a generic cloud. {@link DeclarativeCloud} must be bridged to jclouds using an adapter,
 * which is the standard approach in jclouds.
 * 
 * @author alessiogambi
 *
 */
public class DeclarativeCloudTest {

	/*
	 * SUT
	 */
	DeclarativeCloud cloud;

	@BeforeClass
	public static void injectObjectSerializers() {
		SquanderGlobalOptions.INSTANCE.log_level = Level.DEBUG;
		//
		// ObjSerFactory.addSer(new ImageSer());
		// ObjSerFactory.addSer(new HardwareSer());
		// ObjSerFactory.addSer(new LocationSer());
	}

	@BeforeMethod
	public void initializeCloud() {
		Set<Location> defaultLocations = createDefaultLocationsForTest();
		cloud = new DeclarativeCloud(
		//
				createDefaultImagesForTest(defaultLocations),
				//
				createDefaultHardwaresForTest(defaultLocations),
				//
				defaultLocations
		//
		);

	}

	@Test
	public void testFailInitIfWrongPreconditionsEmptySets() {
		try {
			// This fail with assertions because the set is empty and Squander cannot understand that the set is
			// actually a set of Images !
			// This should fail because thre are no Images !
			cloud = new DeclarativeCloud(ImmutableSet.<Image> builder().build(), ImmutableSet.<Hardware> builder()
					.build(), ImmutableSet.<Location> builder().build());
			Assert.fail("pre-condition is not satisfied not raised for empty cloud!");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	@Test
	public void testFailInitIfWrongPreconditions() {
		try {
			// TODO Note that an empty set by google erase the type parameter and then squander can read that any=longer !
			// By using a standard implementation of Sets this works just fine !!!!
			// Google is to Blame !!
			cloud = new DeclarativeCloud(
					new HashSet<Image>(),
//					ImmutableSet.<Image> builder().build(),
					new HashSet<Hardware>(),
//					ImmutableSet.<Hardware> builder().build(),
					new HashSet<Location>()
//					ImmutableSet.<Location> builder().build()
					);
			Assert.fail("pre-condition is not satisfied not raised for empty cloud!");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	@Test
	public void testInit() {
		// Assert.assertEquals(cloud.getAllImages(), createDefaultImagesForTest(createDefaultLocationsForTest()));
		// Assert.assertEquals(cloud.getAllHardwares(), createDefaultHardwaresForTest(createDefaultLocationsForTest()));
		// Assert.assertEquals(cloud.getAllLocations(), createDefaultLocationsForTest());
	}

	@Test
	public void testCannotCreateNodeWithMalformedInit() {
		try {
			// Split locations
			Set<Location> originalLocations = createDefaultLocationsForTest();
			Iterator<Location> i = originalLocations.iterator();
			Location location1 = i.next();
			Location location2 = i.next();

			System.out.println("DeclarativeCloudTest.testMalformedInit() location 1 " + location1);
			System.out.println("DeclarativeCloudTest.testMalformedInit() location 2 " + location2);

			assert location1 != location2;

			// Set 1 & Set 2 == empty
			Set<Location> set1 = ImmutableSet.<Location> builder().add(location1).build();
			Set<Location> set2 = ImmutableSet.<Location> builder().add(location2).build();

			cloud = new DeclarativeCloud(createDefaultImagesForTest(set1),
			//
					createDefaultHardwaresForTest(set2),
					//
					originalLocations);

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
	public void testListImages() {
		Assert.assertEquals(cloud.getAllImages(), createDefaultImagesForTest(createDefaultLocationsForTest()));
	}

	@Test
	public void testListHardwares() {
		System.out.println("DeclarativeCloudTest.testListFlavors() " + cloud.getAllHardwares());

		Assert.assertEquals(cloud.getAllHardwares(), createDefaultHardwaresForTest(createDefaultLocationsForTest()));
	}

	@Test
	public void testListLocations() {
		System.out.println("DeclarativeCloudTest.testListLocations() " + cloud.getAllLocations());

		Assert.assertEquals(cloud.getAllLocations(), createDefaultLocationsForTest());
	}

	public static Set<Image> createDefaultImagesForTest(Set<Location> locations) {
		// Note we need to guarantee that Images are registered in their
		// locations
		Builder<Image> images = ImmutableSet.builder();
		int id = 0;
		Image image = new ImageBuilder().ids("IMAGE" + ++id).name("IMAGE" + id).location(locations.iterator().next())
				.operatingSystem(new OperatingSystem(OsFamily.LINUX, "desc", "version", null, "desc", false))
				.description("desc").status(ImageStatus.AVAILABLE).build();

		images.add(image);

		image = new ImageBuilder().ids("IMAGE" + ++id).name("IMAGE" + id).location(locations.iterator().next())
				.operatingSystem(new OperatingSystem(OsFamily.WINDOWS, "desc", "version", null, "desc", true))
				.description("desc").status(ImageStatus.AVAILABLE).build();
		images.add(image);

		return images.build();
	}

	public static Set<Location> createDefaultLocationsForTest() {
		ImmutableSet.Builder<Location> locations = ImmutableSet.builder();
		int id = 1;
		locations.add(new LocationBuilder().id("LOCATION-" + id++).description("Location-description")
				.scope(LocationScope.ZONE).build());
		locations.add(new LocationBuilder().id("LOCATION-" + id++).description("Another-Location-description")
				.scope(LocationScope.ZONE).build());
		return locations.build();
	}

	public static Set<Hardware> createDefaultHardwaresForTest(Set<Location> locations) {
		int id = 1;
		ImmutableSet.Builder<Hardware> flavors = ImmutableSet.builder();

		flavors.add(new HardwareBuilder().ids("HW-" + id++).name("small")
				.processors(ImmutableList.of(new Processor(1, 1.0))).ram(1740).location(locations.iterator().next())
				.volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 160, true, false))).build());

		flavors.add(new HardwareBuilder().ids("HW-" + id++).name("medium")
				.processors(ImmutableList.of(new Processor(4, 1.0))).ram(7680).location(locations.iterator().next())
				.volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 850, true, false))).build());

		flavors.add(new HardwareBuilder().ids("HW-" + id++).name("large")
				.processors(ImmutableList.of(new Processor(8, 1.0))).ram(15360).location(locations.iterator().next())
				.volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 1690, true, false))).build());

		return flavors.build();
	}

	@Test
	public void testFailCreateNodeIfNoHardware() {
		try {
			cloud = new DeclarativeCloud(createDefaultImagesForTest(createDefaultLocationsForTest()), ImmutableSet
					.<Hardware> builder().build(), createDefaultLocationsForTest());
			DeclarativeNode n = cloud.createNode();
			System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
			Assert.fail("pre-condition is not satisfied not raised for empty cloud!");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	@Test
	public void testFailCreateNodeIfNoImages() {
		try {
			// Initialize the cloud with no Images
			cloud = new DeclarativeCloud(ImmutableSet.<Image> builder().build(),
					createDefaultHardwaresForTest(createDefaultLocationsForTest()), createDefaultLocationsForTest());
			DeclarativeNode n = cloud.createNode();
			System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
			Assert.fail("pre-condition is not satisfied not raised for empty cloud!");
		} catch (RuntimeException e) {
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	@Test
	public void testFailCreateNodeIfNoAvailableImages() {
		Location location = createDefaultLocationsForTest().iterator().next();
		// Force the use of the very same location (as set)
		Image nonAvailableImage = new ImageBuilder().ids("Non-Available-IMAGE").name("Non-Available-IMAGE")
				.location(location)
				.operatingSystem(new OperatingSystem(OsFamily.WINDOWS, "desc", "version", null, "desc", true))
				.description("desc").status(ImageStatus.DELETED).build();

		Hardware flavor = createDefaultHardwaresForTest(ImmutableSet.<Location> builder().add(location).build())
				.iterator().next();

		cloud = new DeclarativeCloud(//
				ImmutableSet.<Image> builder().add(nonAvailableImage).build(),//
				ImmutableSet.<Hardware> builder().add(flavor).build(),//
				ImmutableSet.<Location> builder().add(location).build());

		try {
			DeclarativeNode n = cloud.createNode();
			System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
			Assert.fail("pre-condition is not satisfied not raised for non-available image !");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	// image =

	@Test
	public void testCreateNodeWithImage() {
		// Only one image

		Location location = createDefaultLocationsForTest().iterator().next();

		// Force the use of the very same location (as set)
		Image image = createDefaultImagesForTest(ImmutableSet.<Location> builder().add(location).build()).iterator()
				.next();

		Hardware flavor = createDefaultHardwaresForTest(ImmutableSet.<Location> builder().add(location).build())
				.iterator().next();

		assert image.getLocation() == location;
		assert flavor.getLocation() == location;

		cloud = new DeclarativeCloud(//
				ImmutableSet.<Image> builder().add(image).build(),//
				ImmutableSet.<Hardware> builder().add(flavor).build(),//
				ImmutableSet.<Location> builder().add(location).build());

		Assert.assertEquals(cloud.getAllImages().size(), 1);
		Assert.assertEquals(cloud.getAllImages().iterator().next().getLocation(), location);
		Assert.assertEquals(cloud.getAllImages().iterator().next().getStatus(), ImageStatus.AVAILABLE);

		// Exec - No Preconditions
		DeclarativeNode n = cloud.createNode();

		Assert.assertNotNull(n);
		Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
		// NOT SURE THE EQUALS IS FINE !
		Assert.assertEquals(n.getImage(), image);
		// Assert SAME Location
		Assert.assertEquals(n.getImage().getLocation(), n.getLocation());
	}

	public void testCreateNode() {
		DeclarativeNode n = cloud.createNode();
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
		Assert.assertNotNull(n);
		Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
		// Assert SAME Location
		Assert.assertEquals(n.getImage().getLocation(), n.getLocation());
		Assert.assertEquals(n.getHardware().getLocation(), n.getLocation());
	}

	@Test
	public void testCreateNodeWithImages() {
		// Exec
		DeclarativeNode n = cloud.createNode();
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
		Assert.assertNotNull(n);
		Assert.assertNotNull(n.getImage());
		Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
		// Assert SAME location
		Assert.assertEquals(n.getImage().getLocation(), n.getLocation());
		Assert.assertEquals(n.getHardware().getLocation(), n.getLocation());
	}

	@Test
	public void testCreateAndListNode() {
		cloud.createNode();
		Set<DeclarativeNode> nodes = cloud.getAllNodes();
		System.out.println("DeclarativeCloudTest.testAddAndListNode() Nodes " + nodes);
		Assert.assertEquals(nodes.size(), 1);

	}

	@Test
	public void testCreateNodes() {
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		// Assert SAME location
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

	// @Test
	// public void testGetNode() {
	// DeclarativeNode n = cloud.createNode();
	// // Return the right node
	// DeclarativeNode _n = cloud.getNode(n);
	// // Assert ID
	// Assert.assertTrue(_n.getId() == n.getId());
	// // Assert STATE
	// Assert.assertEquals(cloud.getNode(n).getStatus(),
	// NodeMetadataStatus.RUNNING);
	// }

	@Test
	public void testGetImage() {
		String imageId = createDefaultImagesForTest(createDefaultLocationsForTest()).iterator().next().getId();

		Image image = cloud.getImage(imageId);
		// Assert ID
		Assert.assertEquals(image.getId(), imageId);
	}

	@Test
	public void testGetImagePreCondFail() {
		try {
			String wrongID = "";
			Image i = cloud.getImage(wrongID);
			System.out.println("DeclarativeCloudTest.testGetImagePreCondFail() Wrong Image found " + i);
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
	public void testGetNodeByID() {
		DeclarativeNode n = cloud.createNode();
		// Return the right node
		DeclarativeNode _n = cloud.getNode(n.getId());
		// Assert ID
		Assert.assertTrue(_n.getId() == n.getId());
		// Assert STATE
		Assert.assertEquals(n.getStatus(), _n.getStatus());
		Assert.assertEquals(_n.getStatus(), NodeMetadataStatus.RUNNING);
		// Assert IMAGE
		Assert.assertEquals(n.getImage(), _n.getImage());
		// Assert HARDWARE
		Assert.assertEquals(n.getHardware(), _n.getHardware());
	}

	private boolean hasStatus(Set<DeclarativeNode> nodes, DeclarativeNode node, NodeMetadataStatus status) {
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

		System.out.println("DeclarativeCloudTest.testGetNodeBySetID() NODES BY ID " + nodes);
		// Check
		Assert.assertTrue(containsID(nodes, n));
		Assert.assertFalse(containsID(nodes, n1));
		Assert.assertFalse(containsID(nodes, n2));

	}

	// TODO Depends on cloud.getNode() !
	@Test
	public void testSuspendNode() {
		DeclarativeNode n = cloud.createNode();
		//
		cloud.suspendNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
	}

	// TODO Depends on cloud.getNode() !
	@Test
	public void testSuspendOnlyTheNode() {
		// WARNING: This test depends on the correctness of getNode!

		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();
		//
		cloud.suspendNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.suspendNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);

		//
		cloud.suspendNode(n1.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		//
		cloud.suspendNode(n2.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);

		System.out.println("DeclarativeCloudTest.testSuspendOnlyTheNode() " + cloud.getAllNodes());
	}

	// TODO Depends on cloud.getNode() !
	@Test
	public void testStartSuspendNode() {
		DeclarativeNode n = cloud.createNode();
		// WARNING: This test depends on the correctness of suspend !
		cloud.suspendNode(n.getId());
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Execution
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
	}

	// TODO Depends on cloud.getNode() !
	@Test
	public void testStartOnlyTheSuspendNode() {
		DeclarativeNode n = cloud.createNode();
		DeclarativeNode n1 = cloud.createNode();
		DeclarativeNode n2 = cloud.createNode();

		// WARNING: This test depends on the correctness of suspend and get !
		cloud.suspendNode(n.getId());
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);

		// Execution
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		cloud.startNode(n.getId());
		//
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Act on the others
		cloud.suspendNode(n1.getId());
		cloud.suspendNode(n2.getId());
		Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
	}
}
