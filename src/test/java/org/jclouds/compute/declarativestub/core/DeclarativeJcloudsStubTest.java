package org.jclouds.compute.declarativestub.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageStatus;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataStatus;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import at.ac.tuwien.cloud.JcloudsStub;
import at.ac.tuwien.cloud.core.DeclarativeCloud;

import com.google.common.collect.ImmutableList;
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
public class DeclarativeJcloudsStubTest {

	/*
	 * SUT. The ID generation fails if multiple tests are run concurrently ?!
	 */
	JcloudsStub jcloudsStub;

	@BeforeMethod(alwaysRun = true)
	public void initializeCloud() {

		SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;

		Set<Location> defaultLocations = createDefaultLocationsForTest();
		jcloudsStub = new JcloudsStub(
		//
				createDefaultImagesForTest(defaultLocations),
				//
				createDefaultHardwaresForTest(defaultLocations),
				//
				defaultLocations,
				//
				new HashSet<NodeMetadata>());
	}

	@AfterMethod
	public void resetTheCloud() {

	}

	private JcloudsStub emptyCloud() {
		return new JcloudsStub(new HashSet<Image>(), new HashSet<Hardware>(), new HashSet<Location>(),
				new HashSet<NodeMetadata>());
	}

	@Test
	public void testInitEmptyCloud() {
		jcloudsStub = emptyCloud();
		System.out.println("DeclarativeCloudTest.testInit() " + jcloudsStub);
		Assert.assertNotNull(jcloudsStub.getAllLocations());
		Assert.assertNotNull(jcloudsStub.getAllHardwares());
		Assert.assertNotNull(jcloudsStub.getAllImages());
		Assert.assertNotNull(jcloudsStub.getAllNodes());
	}

	@Test
	public void testInit() {
		Assert.assertEquals(jcloudsStub.getAllImages(), createDefaultImagesForTest(createDefaultLocationsForTest()));
		Assert.assertEquals(jcloudsStub.getAllHardwares(),
				createDefaultHardwaresForTest(createDefaultLocationsForTest()));
		Assert.assertEquals(jcloudsStub.getAllLocations(), createDefaultLocationsForTest());
	}

	@Test
	public void testCannotCreateNodeWithMalformedInit() {
		try {
			// Split Locations
			Set<Location> originalLocations = createDefaultLocationsForTest();
			Iterator<Location> i = originalLocations.iterator();
			Location Location1 = i.next();
			Location Location2 = i.next();

			System.out.println("DeclarativeCloudTest.testMalformedInit() Location 1 " + Location1);
			System.out.println("DeclarativeCloudTest.testMalformedInit() Location 2 " + Location2);

			assert Location1 != Location2;

			// Set 1 & Set 2 == empty
			Set<Location> set1 = ImmutableSet.<Location> builder().add(Location1).build();
			Set<Location> set2 = ImmutableSet.<Location> builder().add(Location2).build();

			jcloudsStub = new JcloudsStub(createDefaultImagesForTest(set1),
			//
					createDefaultHardwaresForTest(set2),
					//
					originalLocations, new HashSet<NodeMetadata>());

			// This should fail !
			Template template = new TemplateImpl(createDefaultImagesForTest(set1).iterator().next(),
					createDefaultHardwaresForTest(set2).iterator().next(), originalLocations.iterator().next(),
					new TemplateOptions());

			jcloudsStub.createNode("group", "name", template);

			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	private static int sizeOf(Iterable<?> iterable) {
		if (iterable instanceof Collection<?>) {
			return ((Collection<?>) iterable).size();
		} else {
			int size = 0;
			Iterator<?> it = iterable.iterator();
			while (it.hasNext()) {
				it.next();
				size++;
			}
			return size;
		}
	}

	@Test
	public void testListNodesEmpty() {
		jcloudsStub = new JcloudsStub(new HashSet<Image>(), new HashSet<Hardware>(), new HashSet<Location>(),
				new HashSet<NodeMetadata>());
		Assert.assertEquals(sizeOf(jcloudsStub.getAllNodes()), 0);
	}

	//

	private Template defaultTemplate() {
		return new TemplateImpl(createDefaultImagesForTest(createDefaultLocationsForTest()).iterator().next(),
				createDefaultHardwaresForTest(createDefaultLocationsForTest()).iterator().next(),
				createDefaultLocationsForTest().iterator().next(), new TemplateOptions());
	}

	@Test
	public void testPreConditionFailOnCreateNode() {
		try {
			// PreConditions must fail
			jcloudsStub = new JcloudsStub(new HashSet<Image>(), new HashSet<Hardware>(), new HashSet<Location>(),
					new HashSet<NodeMetadata>());

			jcloudsStub.createNode("group", "name", defaultTemplate());
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	public void testListImagesEmptyCloud() {
		jcloudsStub = new JcloudsStub(new HashSet<Image>(), new HashSet<Hardware>(), new HashSet<Location>(),
				new HashSet<NodeMetadata>());
		System.out.println("DeclarativeCloudTest.testInit() " + jcloudsStub.getAllImages());
	}

	@Test
	public void testListImages() {
		// Assert.assertEquals(cloud.getAllImages(),
		// createDefaultImagesForTest(createDefaultLocationsForTest()));
		Assert.assertEquals(sizeOf(jcloudsStub.getAllImages()),
				sizeOf(createDefaultImagesForTest(createDefaultLocationsForTest())));
	}

	@Test
	public void testListHardwares() {
		System.out.println("DeclarativeCloudTest.testListFlavors() " + jcloudsStub.getAllHardwares());

		// The Stub returns a different type of objects !
		// Assert.assertEquals(cloud.getAllHardwares(),
		// createDefaultHardwaresForTest(createDefaultLocationsForTest()));
		Assert.assertEquals(sizeOf(jcloudsStub.getAllHardwares()),
				sizeOf(createDefaultHardwaresForTest(createDefaultLocationsForTest())));
	}

	@Test
	public void testListLocations() {
		System.out.println("DeclarativeCloudTest.testListLocations() " + jcloudsStub.getAllLocations());

		// Assert.assertEquals(cloud.getAllLocations(), createDefaultLocationsForTest());
		Assert.assertEquals(sizeOf(jcloudsStub.getAllLocations()), sizeOf(createDefaultLocationsForTest()));
	}

	public static Set<Image> createDefaultImagesForTest(Set<Location> locations) {
		// Note we need to guarantee that Images are registered in their
		// Locations
		Builder<Image> images = ImmutableSet.builder();
		int id = 0;
		Image image = new ImageBuilder().ids("Image" + ++id).name("Image" + id).location(locations.iterator().next())
				.operatingSystem(new OperatingSystem(OsFamily.LINUX, "desc", "version", null, "desc", false))
				.description("desc").status(ImageStatus.AVAILABLE).build();

		images.add(image);

		image = new ImageBuilder().ids("Image" + ++id).name("Image" + id).location(locations.iterator().next())
				.operatingSystem(new OperatingSystem(OsFamily.WINDOWS, "desc", "version", null, "desc", true))
				.description("desc").status(ImageStatus.AVAILABLE).build();
		images.add(image);

		return images.build();
	}

	public static Set<Location> createDefaultLocationsForTest() {
		ImmutableSet.Builder<Location> locations = ImmutableSet.builder();
		int id = 1;
		locations.add(new LocationBuilder().id("Location-" + id++).description("Location-description")
				.scope(LocationScope.ZONE).build());
		locations.add(new LocationBuilder().id("Location-" + id++).description("Another-Location-description")
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
	public void testCreateNodeWithParameters() {
		// Prepare the inputs
		String _name = "test";
		String _group = "test";
		//
		Location location = jcloudsStub.getAllLocations().iterator().next();
		Hardware hardware = jcloudsStub.getAllHardwares().iterator().next();
		Image image = jcloudsStub.getAllImages().iterator().next();

		// TODO Setup template
		Template template = new TemplateImpl(image, hardware, location, new TemplateOptions());
		// Exec
		NodeMetadata node = jcloudsStub.createNode(_name, _group, template);

		System.out.println("DeclarativeCloudStubTest.testCreateNodeWithParameters() " + node);
	}

	@Test
	public void testCreateNodesWithParameters() {
		// Prepare the inputs
		String _name = "test";
		String _group = "test";
		Location location = jcloudsStub.getAllLocations().iterator().next();
		Hardware hardware = jcloudsStub.getAllHardwares().iterator().next();
		Image image = jcloudsStub.getAllImages().iterator().next();

		Template template = new TemplateImpl(image, hardware, location, new TemplateOptions());
		// Exec
		NodeMetadata node_1 = jcloudsStub.createNode(_name, _group, template);
		NodeMetadata node_2 = jcloudsStub.createNode(_name, _group, template);

		System.out.println("DeclarativeCloudStubTest.testCreateNodeWithParameters() " + node_1);
		System.out.println("DeclarativeCloudStubTest.testCreateNodeWithParameters() " + node_2);
	}

	@Test
	public void testFailCreateNodeIfNoImages() {
		try {
			jcloudsStub = new JcloudsStub(new HashSet<Image>(),
					createDefaultHardwaresForTest(createDefaultLocationsForTest()), createDefaultLocationsForTest(),
					new HashSet<NodeMetadata>());
			NodeMetadata n = jcloudsStub.createNode("group", "name", defaultTemplate());

			System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
			Assert.fail("pre-condition is not satisfied not raised for empty cloud!");
		} catch (RuntimeException e) {
			e.printStackTrace();
			Assert.assertTrue(e.getMessage().contains("pre-condition is not satisfied"));
		}
	}

	@Test
	public void testCreateRandomNode() {
		NodeMetadata n = jcloudsStub.createNode("group", "name", defaultTemplate());

		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
		Assert.assertNotNull(n);
		Assert.assertNotNull(n.getId());
		// Assert.assertEquals(n.getStatus(), NodeMetadataStatus.RUNNING);
		// Assert SAME Location

		Assert.assertEquals(jcloudsStub.getImage(n.getImageId()).getLocation(), n.getLocation());
		Assert.assertEquals(n.getHardware().getLocation(), n.getLocation());
	}

	@Test
	public void testCreateAndListNode() {
		NodeMetadata newNode = jcloudsStub.createNode("group", "name", defaultTemplate());

		// FIXME Exec - This must return a copy of the cloud, not the cloud itself, meaning that all its nodes must be
		// fresh
		// instances as well? !!
		Iterable<NodeMetadata> nodes = jcloudsStub.getAllNodes();
		System.out.println("DeclarativeCloudTest.testAddAndListNode() Nodes " + nodes);
		Assert.assertEquals(sizeOf(nodes), 1);
		Assert.assertTrue(containsID(nodes, newNode));

	}

	@Test
	public void testCreateNodes() {
		NodeMetadata n = jcloudsStub.createNode("group", "name", defaultTemplate());
		NodeMetadata n1 = jcloudsStub.createNode("group", "name", defaultTemplate());

		System.out.println("DeclarativeJcloudsStubTest.testCreateNodes() N_0 \t" + n);
		System.out.println("DeclarativeJcloudsStubTest.testCreateNodes() N_1 \t" + n1);

		// Assert SAME Location
		Assert.assertEquals(jcloudsStub.getImage(n.getImageId()).getLocation(), n.getLocation());
		Assert.assertEquals(n.getHardware().getLocation(), n.getLocation());

		Assert.assertEquals(jcloudsStub.getImage(n1.getImageId()).getLocation(), n1.getLocation());
		Assert.assertEquals(n1.getHardware().getLocation(), n1.getLocation());

		// Sure n and n1 ?!
		Assert.assertEquals(jcloudsStub.getImage(n.getImageId()).getLocation(), n1.getLocation());
		Assert.assertEquals(n.getHardware().getLocation(), n1.getLocation());
	}

	@Test
	public void testDestroyNodeByID() {
		NodeMetadata n = jcloudsStub.createNode("group", "name", defaultTemplate());
		Assert.assertTrue(sizeOf(jcloudsStub.getAllNodes()) == 1);

		//
		jcloudsStub.destroyNode(n.getId());
		Iterable<NodeMetadata> nodes = jcloudsStub.getAllNodes();
		System.out.println("DeclarativeCloudTest.testdestroyNode() Nodes" + nodes);
		Assert.assertTrue(sizeOf(nodes) == 0);
	}

	/**
	 * Test Utility method
	 * 
	 * @param nodes
	 * @param node
	 * @return
	 */
	private boolean containsID(Iterable<NodeMetadata> nodes, NodeMetadata node) {
		for (NodeMetadata n : nodes) {
			if (node.getId() == n.getId()) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void testdestroyNodes() {
		NodeMetadata n = jcloudsStub.createNode("group", "name1", defaultTemplate());
		NodeMetadata n1 = jcloudsStub.createNode("group", "name2", defaultTemplate());
		NodeMetadata n2 = jcloudsStub.createNode("group", "name3", defaultTemplate());

		// Removing n must result in a smaller cloud, but the other nodes must
		// be there !
		Iterable<NodeMetadata> nodes = jcloudsStub.getAllNodes();
		int oldSize = sizeOf(nodes);
		jcloudsStub.destroyNode(n.getId());
		nodes = jcloudsStub.getAllNodes();

		Assert.assertTrue(sizeOf(nodes) == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));

		oldSize = sizeOf(nodes);
		// Repeat again, just in case ;)
		jcloudsStub.destroyNode(n1.getId());

		nodes = jcloudsStub.getAllNodes();

		Assert.assertTrue(sizeOf(nodes) == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n2));

	}

	@Test
	public void testdestroyNodesByID() {
		NodeMetadata n = jcloudsStub.createNode("group", "name1", defaultTemplate());
		NodeMetadata n1 = jcloudsStub.createNode("group", "name2", defaultTemplate());
		NodeMetadata n2 = jcloudsStub.createNode("group", "name3", defaultTemplate());

		System.out.println("DeclarativeCloudStubTest.testdestroyNodesByID() " + n);
		System.out.println("DeclarativeCloudStubTest.testdestroyNodesByID() " + n1);
		System.out.println("DeclarativeCloudStubTest.testdestroyNodesByID() " + n2);

		// Removing n must result in a smaller cloud, but the other nodes must
		// be there !
		Iterable<NodeMetadata> nodes = jcloudsStub.getAllNodes();

		System.out.println("DeclarativeCloudStubTest.testdestroyNodesByID() Before " + nodes);
		int oldSize = sizeOf(nodes);

		jcloudsStub.destroyNode(n.getId());

		nodes = jcloudsStub.getAllNodes();
		System.out.println("DeclarativeCloudStubTest.testdestroyNodesByID() After " + nodes);

		Assert.assertTrue(sizeOf(nodes) == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n1));
		Assert.assertTrue(containsID(nodes, n2));

		oldSize = sizeOf(nodes);
		// Repeat again, just in case ;)
		jcloudsStub.destroyNode(n1.getId());
		nodes = jcloudsStub.getAllNodes();
		Assert.assertTrue(sizeOf(nodes) == (oldSize - 1));
		Assert.assertTrue(containsID(nodes, n2));
	}

	@Test
	public void testGetImage() {
		String imageId = createDefaultImagesForTest(createDefaultLocationsForTest()).iterator().next().getId();

		Image Image1 = jcloudsStub.getImage(imageId);
		Image Image2 = jcloudsStub.getImage(imageId);
		//
		Assert.assertEquals(Image1, Image2, "Not Same image object !");
		//
		Assert.assertEquals(Image1.getLocation(), Image2.getLocation(), "Not Same location object !");
	}

	@Test
	public void testGetHardware() {
		String hardwareId = createDefaultHardwaresForTest(createDefaultLocationsForTest()).iterator().next().getId();

		Hardware Hardware1 = jcloudsStub.getHardware(hardwareId);
		Hardware Hardware2 = jcloudsStub.getHardware(hardwareId);
		System.out.println("DeclarativeCloudStubTest.testGetHardware() " + Hardware1);
		System.out.println("DeclarativeCloudStubTest.testGetHardware() " + Hardware2);
		//
		Assert.assertEquals(Hardware1, Hardware2, "Not the same hardware object !");
		//
		System.out.println("DeclarativeCloudStubTest.testGetImage() " + Hardware1.getLocation());
		System.out.println("DeclarativeCloudStubTest.testGetImage() " + Hardware2.getLocation());
		Assert.assertEquals(Hardware1.getLocation(), Hardware2.getLocation(), "Not the same location object !");

		Assert.assertEquals(Hardware1.getLocation().getId(), Hardware2.getLocation().getId());
	}

	@Test
	public void testGetLocation() {
		String locationId = createDefaultLocationsForTest().iterator().next().getId();

		Location Location1 = jcloudsStub.getLocation(locationId);
		Location Location2 = jcloudsStub.getLocation(locationId);
		//
		Assert.assertEquals(Location1, Location2, "Not Same image object !");
	}

	@Test(enabled = false, description = "Now getImage has no preconditions as it returns false !")
	public void testgetImagePreCondFail() {
		try {
			String wrongID = "";
			Image i = jcloudsStub.getImage(wrongID);
			System.out.println("DeclarativeCloudTest.testgetImagePreCondFail() Wrong Image found " + i);
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
		NodeMetadata _n = jcloudsStub.getNode("");
		// Assert ID
		Assert.assertNull(_n);
	}

	@Test
	public void testGetNodeByID() {
		NodeMetadata n = jcloudsStub.createNode("group", "name", defaultTemplate());

		// EXEC
		NodeMetadata _n = jcloudsStub.getNode(n.getId());
		// Assert ID
		Assert.assertTrue(_n.getId() == n.getId());
		// Assert STATE
		Assert.assertEquals(n.getStatus(), _n.getStatus());
		Assert.assertEquals(_n.getStatus(), NodeMetadataStatus.RUNNING);
		// Assert Image
		Assert.assertEquals(n.getImageId(), _n.getImageId());
		// Assert Hardware
		Assert.assertEquals(n.getHardware(), _n.getHardware());
	}

	private boolean hasStatus(Iterable<NodeMetadata> nodes, NodeMetadata node, NodeMetadataStatus status) {
		for (NodeMetadata n : nodes) {
			if (node.getId() == n.getId()) {
				return status.equals(n.getStatus());
			}
		}
		return false;
	}

	@Test
	public void testGetNodesBySetID() {
		NodeMetadata n = jcloudsStub.createNode("group", "name1", defaultTemplate());
		NodeMetadata n1 = jcloudsStub.createNode("group", "name2", defaultTemplate());
		NodeMetadata n2 = jcloudsStub.createNode("group", "name3", defaultTemplate());

		// Build the input
		Set<String> ids = new HashSet<String>();
		ids.add(n.getId());
		ids.add(n1.getId());
		ids.add(n2.getId());

		// Exec
		Iterable<NodeMetadata> nodes = jcloudsStub.getAllNodesById(ids);
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
		NodeMetadata n = jcloudsStub.createNode("group", "name1", defaultTemplate());
		NodeMetadata n1 = jcloudsStub.createNode("group", "name2", defaultTemplate());
		NodeMetadata n2 = jcloudsStub.createNode("group", "name3", defaultTemplate());
		// Build the input
		Set<String> ids = new HashSet<String>();
		ids.add(n.getId());

		// Exec
		Iterable<NodeMetadata> nodes = jcloudsStub.getAllNodesById(ids);

		System.out.println("DeclarativeCloudTest.testGetNodeBySetID() NODES BY ID " + nodes);
		// Check
		Assert.assertTrue(containsID(nodes, n));
		Assert.assertFalse(containsID(nodes, n1));
		Assert.assertFalse(containsID(nodes, n2));

	}

	@Test
	public void testSuspendNode() {
		NodeMetadata n = jcloudsStub.createNode("group", "name", defaultTemplate());

		//
		jcloudsStub.suspendNode(n.getId());
		//
		Assert.assertEquals(jcloudsStub.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
	}

	@Test
	public void testSuspendOnlyTheNode() {
		// WARNING: This test depends on the correctness of getNode!

		NodeMetadata n = jcloudsStub.createNode("group", "name1", defaultTemplate());
		NodeMetadata n1 = jcloudsStub.createNode("group", "name2", defaultTemplate());
		NodeMetadata n2 = jcloudsStub.createNode("group", "name3", defaultTemplate());

		//
		jcloudsStub.suspendNode(n.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		jcloudsStub.suspendNode(n.getId());
		// //
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);

		//
		jcloudsStub.suspendNode(n1.getId());
		// //
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		//
		jcloudsStub.suspendNode(n2.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Assert.assertEquals(cloud.getNode(n2.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);

		System.out.println("DeclarativeCloudTest.testSuspendOnlyTheNode() " + jcloudsStub.getAllNodes());
	}

	@Test
	public void testStartSuspendNode() {
		NodeMetadata n = jcloudsStub.createNode("group", "name", defaultTemplate());

		// WARNING: This test depends on the correctness of suspend !
		jcloudsStub.suspendNode(n.getId());
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		// Execution
		jcloudsStub.resumeNode(n.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		jcloudsStub.resumeNode(n.getId());
		//
		// Assert.assertEquals(cloud.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
	}

	@Test
	public void testStartOnlyTheSuspendNode() {
		SquanderGlobalOptions.INSTANCE.log_level = Level.DEBUG;
		NodeMetadata n = jcloudsStub.createNode("group", "name1", defaultTemplate());
		NodeMetadata n1 = jcloudsStub.createNode("group", "name2", defaultTemplate());
		NodeMetadata n2 = jcloudsStub.createNode("group", "name3", defaultTemplate());

		// WARNING: This test depends on the correctness of suspend and get !
		jcloudsStub.suspendNode(n.getId());
		Assert.assertEquals(jcloudsStub.getNode(n.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);

		Assert.assertEquals(jcloudsStub.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(jcloudsStub.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);

		// Execution
		jcloudsStub.resumeNode(n.getId());
		//
		Assert.assertEquals(jcloudsStub.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(jcloudsStub.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(jcloudsStub.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Idempotence
		jcloudsStub.resumeNode(n.getId());
		//
		Assert.assertEquals(jcloudsStub.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(jcloudsStub.getNode(n1.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(jcloudsStub.getNode(n2.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		// Act on the others
		jcloudsStub.suspendNode(n1.getId());
		jcloudsStub.suspendNode(n2.getId());
		//
		Assert.assertEquals(jcloudsStub.getNode(n.getId()).getStatus(), NodeMetadataStatus.RUNNING);
		Assert.assertEquals(jcloudsStub.getNode(n1.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
		Assert.assertEquals(jcloudsStub.getNode(n2.getId()).getStatus(), NodeMetadataStatus.SUSPENDED);
	}
}
