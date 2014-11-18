package org.jclouds.compute.declarativestub.config;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.declarativestub.core.DeclarativeCloud;
import org.jclouds.compute.declarativestub.core.DeclarativeNode;
import org.jclouds.compute.declarativestub.core.impl.DeclarativeCloudStub;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.predicates.SocketOpen;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;

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
		// FIXME Not sure this is needed:
		// bind(new TypeLiteral<SecurityGroupExtension>() {
		// }).to(StubSecurityGroupExtension.class);
		// Install a private module
	}

	// // STUB STUFF STATIC SO MULTIPLE CONTEXTS CAN SEE IT
	// protected static final LoadingCache<String, ConcurrentMap<String, NodeMetadata>> backing = CacheBuilder
	// .newBuilder().build(new CacheLoader<String, ConcurrentMap<String, NodeMetadata>>() {
	//
	// @Override
	// public ConcurrentMap<String, NodeMetadata> load(String arg0) throws Exception {
	// return new ConcurrentHashMap<String, NodeMetadata>();
	// }
	//
	// });

	// FIXME Not sure this is the right implementation !
	// And I am pretty sure that this is way parallel tests fail: there is always 1 and 1 cloud, that is shared but that
	// it is not designed to be thread safe ! !
	protected static DeclarativeCloud detachedCloud;

	@Singleton
	@Provides
	@Named("DETACHED_CLOUD")
	protected DeclarativeCloud providesCloud(Set<Image> images, Set<Hardware> hardwares, Set<Location> locations) {
		System.out.println("DeclarativeStubComputeServiceDependenciesModule.providesCloud()");
		if (detachedCloud == null) {
			System.out
					.println("DeclarativeStubComputeServiceDependenciesModule.providesDetachedCloud() CREATING - DETACHED CLOUD");
			detachedCloud = new DeclarativeCloudStub(images, hardwares, locations);
		}
		return detachedCloud;
	}

	@Provides
	@Singleton
	@Named("TEST_CLOUD")
	protected DeclarativeCloud providesTestCloud(Set<Image> images, Set<Hardware> hardwares, Set<Location> locations) {
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

	// @Provides
	// @Singleton
	// /**
	// * This provides the nodes element to {@link DeclarativeStubComputeServiceAdapter}
	// */
	// protected ConcurrentMap<String, NodeMetadata> provideNodesForIdentity(
	// /**
	// * This is provided by who ? jclouds itself ?
	// */
	// @Provider Supplier<Credentials> creds// @Provider => Rest Service ?
	// ) throws ExecutionException {
	// return backing.get(creds.get().identity);
	// }

	// protected static final LoadingCache<String, ConcurrentMap<String, SecurityGroup>> groupBacking = CacheBuilder
	// .newBuilder().build(new CacheLoader<String, ConcurrentMap<String, SecurityGroup>>() {
	//
	// @Override
	// public ConcurrentMap<String, SecurityGroup> load(String arg0) throws Exception {
	// return new ConcurrentHashMap<String, SecurityGroup>();
	// }
	//
	// });
	//
	// @Provides
	// @Singleton
	// protected ConcurrentMap<String, SecurityGroup> provideGroups(
	// /**
	// * This is provided by who ? jclouds itself ?
	// */
	// @Provider Supplier<Credentials> creds) throws ExecutionException {
	// return groupBacking.get(creds.get().identity);
	// }

	// protected static final LoadingCache<String, Multimap<String, SecurityGroup>> groupsForNodeBacking = CacheBuilder
	// .newBuilder().build(new CacheLoader<String, Multimap<String, SecurityGroup>>() {
	//
	// @Override
	// public Multimap<String, SecurityGroup> load(String arg0) throws Exception {
	// return LinkedHashMultimap.create();
	// }
	//
	// });
	//
	// @Provides
	// @Singleton
	// protected Multimap<String, SecurityGroup> provideGroupsForNode(@Provider Supplier<Credentials> creds)
	// throws ExecutionException {
	// return groupsForNodeBacking.get(creds.get().identity);
	// }

	// protected static final LoadingCache<String, AtomicInteger> nodeIds = CacheBuilder.newBuilder().build(
	// new CacheLoader<String, AtomicInteger>() {
	//
	// @Override
	// public AtomicInteger load(String arg0) throws Exception {
	// return new AtomicInteger(0);
	// }
	//
	// });
	//
	// @Provides
	// @Named("NODE_ID")
	// protected Integer provideNodeIdForIdentity(@Provider Supplier<Credentials> creds) throws ExecutionException {
	// return nodeIds.get(creds.get().identity).incrementAndGet();
	// }

	// protected static final LoadingCache<String, AtomicInteger> groupIds = CacheBuilder.newBuilder().build(
	// new CacheLoader<String, AtomicInteger>() {
	//
	// @Override
	// public AtomicInteger load(String arg0) throws Exception {
	// return new AtomicInteger(0);
	// }
	//
	// });
	//
	// @Provides
	// @Named("GROUP_ID")
	// protected Integer provideGroupIdForIdentity(@Provider Supplier<Credentials> creds) throws ExecutionException {
	// return groupIds.get(creds.get().identity).incrementAndGet();
	// }

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
		// private final ConcurrentMap<String, Node Metadata> nodes;
		private final String publicIpPrefix;
		private final DeclarativeCloud cloud;

		@Inject
		public StubSocketOpen(
		// ConcurrentMap<String, NodeMetadata> nodes,
				@Named("DETACHED_CLOUD") DeclarativeCloud cloud, @Named("PUBLIC_IP_PREFIX") String publicIpPrefix) {
			System.err.println("DeclarativeStubComputeServiceDependenciesModule.StubSocketOpen.StubSocketOpen()");
			// this.nodes = nodes;
			this.publicIpPrefix = publicIpPrefix;
			this.cloud = cloud;
		}

		@Override
		public boolean apply(HostAndPort input) {
			System.err.println("DeclarativeStubComputeServiceDependenciesModule.StubSocketOpen.apply() "
					+ input.getHostText() + " " + publicIpPrefix);
			if (input.getHostText().indexOf(publicIpPrefix) == -1) {
				System.err.println("FALSE !");
				return false;
			}
			// Not the best matching but it should be fine
			String id = input.getHostText().replace(publicIpPrefix, "");

			// NodeMetadata node = nodes.get(id);
			// return node != null && node.getStatus() == NodeMetadataStatus.RUNNING;
			try {
				DeclarativeNode node = cloud.getNode(id);
				// TODO node.getStatus()
				return node != null;
			} catch (Throwable t) {
				t.printStackTrace();
				return false;
			}

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
