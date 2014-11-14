package org.jclouds.compute.declarativestub.config;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.declarativestub.core.DeclarativeCloud;
import org.jclouds.compute.declarativestub.core.impl.DeclarativeCloudStub;
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
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.stub.extensions.StubSecurityGroupExtension;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.location.Provider;
import org.jclouds.predicates.SocketOpen;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * This module should contains the declarative logic that implements the specification of the cloud and that can be
 * replaced at startup time !
 * 
 * @author alessiogambi
 *
 */
public class DeclarativeStubComputeServiceDependenciesModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<SecurityGroupExtension>() {
		}).to(StubSecurityGroupExtension.class);

	}

	// STUB STUFF STATIC SO MULTIPLE CONTEXTS CAN SEE IT
	protected static final LoadingCache<String, ConcurrentMap<String, NodeMetadata>> backing = CacheBuilder
			.newBuilder().build(new CacheLoader<String, ConcurrentMap<String, NodeMetadata>>() {

				@Override
				public ConcurrentMap<String, NodeMetadata> load(String arg0) throws Exception {
					return new ConcurrentHashMap<String, NodeMetadata>();
				}

			});

	@Provides
	private Set<Location> providesLocations(Supplier<Location> location) {
		// Empty location is fine ?!
		ImmutableSet.Builder<Location> locations = ImmutableSet.builder();
		// Add the one provided via injection
		locations.add(location.get());
		return locations.build();
	}

	/**
	 * Provided via TODO Check me {@link BaseComputeServiceContextModule#provideOsVersionMap}
	 */
	//

	@Provides
	protected Set<Image> providesImages(Set<Location> locations, Map<OsFamily, Map<String, String>> osToVersionMap) {
		Location location = locations.iterator().next();
		ImmutableSet.Builder<Image> images = ImmutableSet.<Image> builder();
		int id = 1;

		// Let's work only with few images first !

		int count = 0;
		// TODO
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

					count = count + 1;
					if (count == MAX_Count) {
						break;
					}
				}
				if (count == MAX_Count) {
					break;
				}
			}
			if (count == MAX_Count) {
				break;
			}
		}
		return images.build();
	}

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

	@Provides
	@Singleton
	protected DeclarativeCloud providesCloud(Set<Image> images, Set<Hardware> hardwares, Set<Location> locations) {
		try {
			System.out
					.println("\n\n\nDeclarativeStubComputeServiceAdapter.DeclarativeStubComputeServiceAdapter()\n\n\n");
			System.out.println("location " + locations);
			System.out.println("hardwares " + hardwares);
			System.out.println("images " + images);
			System.out.println("===================================");
			// Note this !
			DeclarativeCloud cloud = new DeclarativeCloudStub(images, hardwares, locations);
			System.out.println("\n\n\n cloud == " + cloud + "\n\n\n");
			return cloud;
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("DeclarativeStubComputeServiceDependenciesModule.providesCloud() WAIT USER INPUT: ");
			try {
				System.in.read();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		}
	}

	@Provides
	@Singleton
	/**
	 * This provides the nodes element to {@link DeclarativeStubComputeServiceAdapter}
	 */
	protected ConcurrentMap<String, NodeMetadata> provideNodesForIdentity(
	/**
	 * This is provided by who ? jclouds itself ?
	 */
	@Provider Supplier<Credentials> creds// @Provider => Rest Service ?
	) throws ExecutionException {
		return backing.get(creds.get().identity);
	}

	protected static final LoadingCache<String, ConcurrentMap<String, SecurityGroup>> groupBacking = CacheBuilder
			.newBuilder().build(new CacheLoader<String, ConcurrentMap<String, SecurityGroup>>() {

				@Override
				public ConcurrentMap<String, SecurityGroup> load(String arg0) throws Exception {
					return new ConcurrentHashMap<String, SecurityGroup>();
				}

			});

	@Provides
	@Singleton
	protected ConcurrentMap<String, SecurityGroup> provideGroups(
	/**
	 * This is provided by who ? jclouds itself ?
	 */
	@Provider Supplier<Credentials> creds) throws ExecutionException {
		return groupBacking.get(creds.get().identity);
	}

	protected static final LoadingCache<String, Multimap<String, SecurityGroup>> groupsForNodeBacking = CacheBuilder
			.newBuilder().build(new CacheLoader<String, Multimap<String, SecurityGroup>>() {

				@Override
				public Multimap<String, SecurityGroup> load(String arg0) throws Exception {
					return LinkedHashMultimap.create();
				}

			});

	@Provides
	@Singleton
	protected Multimap<String, SecurityGroup> provideGroupsForNode(@Provider Supplier<Credentials> creds)
			throws ExecutionException {
		return groupsForNodeBacking.get(creds.get().identity);
	}

	protected static final LoadingCache<String, AtomicInteger> nodeIds = CacheBuilder.newBuilder().build(
			new CacheLoader<String, AtomicInteger>() {

				@Override
				public AtomicInteger load(String arg0) throws Exception {
					return new AtomicInteger(0);
				}

			});

	@Provides
	@Named("NODE_ID")
	protected Integer provideNodeIdForIdentity(@Provider Supplier<Credentials> creds) throws ExecutionException {
		return nodeIds.get(creds.get().identity).incrementAndGet();
	}

	protected static final LoadingCache<String, AtomicInteger> groupIds = CacheBuilder.newBuilder().build(
			new CacheLoader<String, AtomicInteger>() {

				@Override
				public AtomicInteger load(String arg0) throws Exception {
					return new AtomicInteger(0);
				}

			});

	@Provides
	@Named("GROUP_ID")
	protected Integer provideGroupIdForIdentity(@Provider Supplier<Credentials> creds) throws ExecutionException {
		return groupIds.get(creds.get().identity).incrementAndGet();
	}

	@Singleton
	@Provides
	@Named("PUBLIC_IP_PREFIX")
	String publicIpPrefix() {
		return "144.175.1.";
	}

	@Singleton
	@Provides
	@Named("PRIVATE_IP_PREFIX")
	String privateIpPrefix() {
		return "10.1.1.";
	}

	@Singleton
	@Provides
	@Named("PASSWORD_PREFIX")
	String passwordPrefix() {
		return "password";
	}

	@Singleton
	@Provides
	SocketOpen socketOpen(StubSocketOpen in) {
		return in;
	}

	@Singleton
	public static class StubSocketOpen implements SocketOpen {
		private final ConcurrentMap<String, NodeMetadata> nodes;
		private final String publicIpPrefix;

		@Inject
		public StubSocketOpen(ConcurrentMap<String, NodeMetadata> nodes,
				@Named("PUBLIC_IP_PREFIX") String publicIpPrefix) {
			this.nodes = nodes;
			this.publicIpPrefix = publicIpPrefix;
		}

		@Override
		public boolean apply(HostAndPort input) {
			if (input.getHostText().indexOf(publicIpPrefix) == -1)
				return false;
			String id = input.getHostText().replace(publicIpPrefix, "");
			NodeMetadata node = nodes.get(id);
			return node != null && node.getStatus() == NodeMetadataStatus.RUNNING;
		}
	}

	// static Hardware stub(String type, int cores, int ram, float disk) {
	// return new org.jclouds.compute.domain.HardwareBuilder()
	// .ids(type)
	// .name(type)
	// .processors(ImmutableList.of(new Processor(cores, 1.0)))
	// .ram(ram)
	// .volumes(
	// ImmutableList.<Volume> of(new VolumeImpl(disk, true,
	// false))).build();
	// }

}
