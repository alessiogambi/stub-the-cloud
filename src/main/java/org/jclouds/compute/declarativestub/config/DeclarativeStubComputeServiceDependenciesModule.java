package org.jclouds.compute.declarativestub.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.stub.extensions.StubSecurityGroupExtension;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.predicates.SocketOpen;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * This module should contains the declarative logic that implements the
 * specification of the cloud and that can be replaced at startup time !
 * 
 * @author alessiogambi
 *
 */
public class DeclarativeStubComputeServiceDependenciesModule extends
		AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<SecurityGroupExtension>() {
		}).to(StubSecurityGroupExtension.class);

	}

	// STUB STUFF STATIC SO MULTIPLE CONTEXTS CAN SEE IT
	protected static final LoadingCache<String, ConcurrentMap<String, NodeMetadata>> backing = CacheBuilder
			.newBuilder()
			.build(new CacheLoader<String, ConcurrentMap<String, NodeMetadata>>() {

				@Override
				public ConcurrentMap<String, NodeMetadata> load(String arg0)
						throws Exception {
					return new ConcurrentHashMap<String, NodeMetadata>();
				}

			});

	@Provides
	@Singleton
	/**
	 * This provides the nodes element to {@link DeclarativeStubComputeServiceAdapter}
	 */
	protected ConcurrentMap<String, NodeMetadata> provideNodesForIdentity(
	/**
	 * This is provided by who ? jclouds itself ?
	 */
	@Provider Supplier<Credentials> creds//
	) throws ExecutionException {
		return backing.get(creds.get().identity);
	}

	protected static final LoadingCache<String, ConcurrentMap<String, SecurityGroup>> groupBacking = CacheBuilder
			.newBuilder()
			.build(new CacheLoader<String, ConcurrentMap<String, SecurityGroup>>() {

				@Override
				public ConcurrentMap<String, SecurityGroup> load(String arg0)
						throws Exception {
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
			.newBuilder().build(
					new CacheLoader<String, Multimap<String, SecurityGroup>>() {

						@Override
						public Multimap<String, SecurityGroup> load(String arg0)
								throws Exception {
							return LinkedHashMultimap.create();
						}

					});

	@Provides
	@Singleton
	protected Multimap<String, SecurityGroup> provideGroupsForNode(
			@Provider Supplier<Credentials> creds) throws ExecutionException {
		return groupsForNodeBacking.get(creds.get().identity);
	}

	protected static final LoadingCache<String, AtomicInteger> nodeIds = CacheBuilder
			.newBuilder().build(new CacheLoader<String, AtomicInteger>() {

				@Override
				public AtomicInteger load(String arg0) throws Exception {
					return new AtomicInteger(0);
				}

			});

	@Provides
	@Named("NODE_ID")
	protected Integer provideNodeIdForIdentity(
			@Provider Supplier<Credentials> creds) throws ExecutionException {
		return nodeIds.get(creds.get().identity).incrementAndGet();
	}

	protected static final LoadingCache<String, AtomicInteger> groupIds = CacheBuilder
			.newBuilder().build(new CacheLoader<String, AtomicInteger>() {

				@Override
				public AtomicInteger load(String arg0) throws Exception {
					return new AtomicInteger(0);
				}

			});

	@Provides
	@Named("GROUP_ID")
	protected Integer provideGroupIdForIdentity(
			@Provider Supplier<Credentials> creds) throws ExecutionException {
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
			return node != null && node.getStatus() == Status.RUNNING;
		}

	}

//	static Hardware stub(String type, int cores, int ram, float disk) {
//		return new org.jclouds.compute.domain.HardwareBuilder()
//				.ids(type)
//				.name(type)
//				.processors(ImmutableList.of(new Processor(cores, 1.0)))
//				.ram(ram)
//				.volumes(
//						ImmutableList.<Volume> of(new VolumeImpl(disk, true,
//								false))).build();
//	}

}
