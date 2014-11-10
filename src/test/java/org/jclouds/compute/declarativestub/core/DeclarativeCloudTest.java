package org.jclouds.compute.declarativestub.core;

import java.util.Iterator;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageStatus;
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

import edu.mit.csail.sdg.squander.log.Log;
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

	// Il problema e' che sono immutable ?
	public static Set<Image> createDefaultImagesForTest(Set<Location> locations) {
		// Note we need to guarantee that Images are registered in their
		Iterator<Location> i = locations.iterator();
		//
		Location l = i.next();
		Location l1 = i.next();

		assert l != l1;
		// locations
		Builder<Image> images = ImmutableSet.builder();
		int id = 0;
		Image image = new ImageBuilder().ids("IMAGE" + ++id).name("IMAGE" + id).location(l)
				.operatingSystem(new OperatingSystem(OsFamily.LINUX, "desc", "version", null, "desc", false))
				.description("desc").status(ImageStatus.AVAILABLE).build();

		images.add(image);

		image = new ImageBuilder().ids("IMAGE" + ++id).name("IMAGE" + id).location(l)
				.operatingSystem(new OperatingSystem(OsFamily.WINDOWS, "desc", "version", null, "desc", true))
				.description("desc").status(ImageStatus.DELETED).build();
		images.add(image);

		image = new ImageBuilder().ids("IMAGE" + ++id).name("Broken-IMAGE" + id).location(l1)
				.operatingSystem(new OperatingSystem(OsFamily.WINDOWS, "desc", "version", null, "desc", true))
				.description("broken-image").status(ImageStatus.ERROR).build();
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

		// flavors.add(new HardwareBuilder().ids("HW-" + id++).name("medium")
		// .processors(ImmutableList.of(new Processor(4, 1.0))).ram(7680).location(locations.iterator().next())
		// .volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 850, true, false))).build());
		//
		// flavors.add(new HardwareBuilder().ids("HW-" + id++).name("large")
		// .processors(ImmutableList.of(new Processor(8, 1.0))).ram(15360).location(locations.iterator().next())
		// .volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 1690, true, false))).build());

		return flavors.build();
	}

	/*
	 * SUT
	 */
	DeclarativeCloud cloud;

	@BeforeClass
	public static void injectObjectSerializers() {
		ObjSerFactory.addSer(new ImageSer());
		ObjSerFactory.addSer(new HardwareSer());
		ObjSerFactory.addSer(new LocationSer());

		SquanderGlobalOptions.INSTANCE.log_level = Log.Level.DEBUG;
	}

	@BeforeMethod
	public void initializeCloud() {
		Set<Location> defaultLocations = createDefaultLocationsForTest();
		cloud = new DeclarativeCloud(createDefaultImagesForTest(defaultLocations));
	}

	@Test
	public void testInit() {
		// Create a copy of the original set of objects

		Set<Image> fromCloud = cloud.getImages();
		for (Image original : createDefaultImagesForTest(createDefaultLocationsForTest())) {
			Assert.assertTrue(fromCloud.contains(original), " Cloud does not contains " + original + " List "
					+ fromCloud);
		}
		// Assert.assertEquals(cloud.getAllHardwares(), createDefaultHardwaresForTest(createDefaultLocationsForTest()));
		// Assert.assertEquals(cloud.getAllLocations(), createDefaultLocationsForTest());
	}
}
