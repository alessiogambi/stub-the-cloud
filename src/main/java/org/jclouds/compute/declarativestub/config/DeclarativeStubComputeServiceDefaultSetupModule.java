package org.jclouds.compute.declarativestub.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Singleton;

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

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DeclarativeStubComputeServiceDefaultSetupModule extends AbstractModule {

	@Override
	protected void configure() {
		System.err.println("DeclarativeStubComputeServiceDefaultSetupModule.configure()");
	}

	@Provides
	@Singleton
	protected Set<Location> providesLocations(Supplier<Location> location) {
		// Empty location is fine ?!
		ImmutableSet.Builder<Location> locations = ImmutableSet.builder();
		// Add the one provided via injection
		locations.add(location.get());
		return locations.build();
	}

	@Provides
	@Singleton
	protected Set<Image> providesImages(Set<Location> locations, Map<OsFamily, Map<String, String>> osToVersionMap) {
		Location location = locations.iterator().next();
		ImmutableSet.Builder<Image> images = ImmutableSet.<Image> builder();
		int id = 1;

		// Let's work only with few images first !
		int MAX_Count = 2;

		for (boolean is64Bit : new boolean[] { true, false }) {
			for (Entry<OsFamily, Map<String, String>> osVersions : osToVersionMap.entrySet()) {

				for (String version : ImmutableSet.copyOf(osVersions.getValue().values())) {

					String desc = String.format("declarative-stub %s %s", osVersions.getKey(), is64Bit);

					// THIS SHOULD BE CREATED USING THE SPEC OF THE CLOUD AND A
					// SMART
					// PRECONDITION
					Image image = new ImageBuilder()
							.ids(id++ + "")
							.name(osVersions.getKey().name())
							.location(location)
							.operatingSystem(
									new OperatingSystem(osVersions.getKey(), desc, version, null, desc, is64Bit))
							.description(desc).status(ImageStatus.AVAILABLE).build();

					images.add(image);

				}
			}
		}
		// Now Take only the first MAX_Count
		ImmutableSet.Builder<Image> filteredImages = ImmutableSet.<Image> builder();
		int c = 0;
		Iterator<Image> i = images.build().iterator();
		while (c < MAX_Count && i.hasNext()) {
			filteredImages.add(i.next());
			c++;
		}

		System.err.println("DeclarativeStubComputeServiceDefaultSetupModule.providesImages() "
				+ filteredImages.build().size());

		return filteredImages.build();
	}

	@Singleton
	@Provides
	protected Set<Hardware> providesHardwares(Set<Location> locations) {
		// This is similar to listImage
		Location location = locations.iterator().next();
		ImmutableSet.Builder<Hardware> flavors = ImmutableSet.builder();

		flavors.add(new HardwareBuilder().ids("1").name("small").location(location)
				.processors(ImmutableList.of(new Processor(1, 1.0))).ram(1740)
				.volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 160, true, false))).build());

		flavors.add(new HardwareBuilder().ids("2").name("medium").location(location)
				.processors(ImmutableList.of(new Processor(4, 1.0))).ram(7680)
				.volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 850, true, false))).build());

		flavors.add(new HardwareBuilder().ids("3").name("large").location(location)
				.processors(ImmutableList.of(new Processor(8, 1.0))).ram(15360)
				.volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 1690, true, false))).build());

		return flavors.build();
	}

}
