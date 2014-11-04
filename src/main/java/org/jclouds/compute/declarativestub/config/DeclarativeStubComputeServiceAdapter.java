package org.jclouds.compute.declarativestub.config;

import static com.google.common.collect.Iterables.find;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.declarativestub.core.DeclarativeCloud;
import org.jclouds.compute.declarativestub.core.DeclarativeNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageStatus;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * This class defined the spec for a <strong>single-user<strong> cloud !
 * 
 * A multi-user implementation can be easily obtained by injecting a nodes-to-id
 * object like in {@StubComputeServiceAdapter}
 * 
 * @author alessiogambi
 *
 */
@Singleton
public class DeclarativeStubComputeServiceAdapter implements
		JCloudsNativeComputeServiceAdapter {

	// Spec of the Cloud
	DeclarativeCloud cloud;

	// Temporary Support for Test

	// Data Fixture? Different instances of possible OS and versions ?!
	private final Map<OsFamily, Map<String, String>> osToVersionMap;

	// TODO Location -> embed into cloud model !
	private final Supplier<Location> location;

	/*
	 * private final ConcurrentMap<String, NodeMetadata> nodes; private final
	 * Multimap<String, SecurityGroup> groupsForNodes; private final
	 * ListeningExecutorService ioExecutor; private final Provider<Integer>
	 * idProvider; private final Provider<Integer> groupIdProvider; private
	 * final String publicIpPrefix; private final String privateIpPrefix;
	 * private final String passwordPrefix; private final Supplier<Set<? extends
	 * Location>> locationSupplier; private final
	 * Optional<SecurityGroupExtension> securityGroupExtension;
	 */

	/*
	 * @Inject public StubComputeServiceAdapter(ConcurrentMap<String,
	 * NodeMetadata> nodes,
	 * 
	 * @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService
	 * ioExecutor,
	 * 
	 * @Named("NODE_ID") Provider<Integer> idProvider,
	 * 
	 * @Named("PUBLIC_IP_PREFIX") String publicIpPrefix,
	 * 
	 * @Named("PRIVATE_IP_PREFIX") String privateIpPrefix,
	 * 
	 * @Named("PASSWORD_PREFIX") String passwordPrefix, JustProvider
	 * locationSupplier, Multimap<String, SecurityGroup> groupsForNodes,
	 * 
	 * @Named("GROUP_ID") Provider<Integer> groupIdProvider,
	 * Optional<SecurityGroupExtension> securityGroupExtension) { this.nodes =
	 * nodes; this.ioExecutor = ioExecutor; this.location = location;
	 * this.idProvider = idProvider; this.publicIpPrefix = publicIpPrefix;
	 * this.privateIpPrefix = privateIpPrefix; this.passwordPrefix =
	 * passwordPrefix; this.locationSupplier = locationSupplier;
	 * this.osToVersionMap = osToVersionMap; this.groupsForNodes =
	 * groupsForNodes; this.groupIdProvider = groupIdProvider;
	 * this.securityGroupExtension = securityGroupExtension; }
	 */
	@Inject
	public DeclarativeStubComputeServiceAdapter(
	/*
	 * TODO Inject the cloud specifications here and Inject the initial state of
	 * the cloud
	 */
	// Temporary Support for the test
			/** Provided via TODO */
			Supplier<Location> location,
			/**
			 * Provided via
			 * {@link BaseComputeServiceContextModule#provideOsVersionMap}
			 */
			Map<OsFamily, Map<String, String>> osToVersionMap) {
		//
		cloud = new DeclarativeCloud();
		//
		this.location = location;
		this.osToVersionMap = osToVersionMap;
	}

	@Override
	public NodeMetadata getNode(String id) {
		DeclarativeNode n = new DeclarativeNode();
		// TODO Must be fixed from INTEGER to STRING !
		n.setId(id);
		DeclarativeNode abstractNode = cloud.getNode(n);

		NodeMetadataBuilder builder = new NodeMetadataBuilder();
		builder.ids("" + abstractNode.getId());
		NodeMetadata node = builder.build();

		return node;
	}

	@Override
	public Iterable<NodeMetadata> listNodes() {
		Builder<NodeMetadata> nodesBuilder = ImmutableList.builder();
		for (DeclarativeNode node : cloud.getAllNodes()) {
			NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
			nodeMetadataBuilder.ids("" + node.getId());

			nodesBuilder.add(nodeMetadataBuilder.build());
		}
		return nodesBuilder.build();
	}

	@Override
	public Iterable<NodeMetadata> listNodesByIds(Iterable<String> ids) {
		// TODO Remove this eventually
		Set<String> _ids = new HashSet<String>();
		for (String id : ids) {
			_ids.add(id);
		}
		Builder<NodeMetadata> nodesBuilder = ImmutableList.builder();
		for (DeclarativeNode node : cloud.getNodes(_ids)) {
			NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
			nodeMetadataBuilder.ids("" + node.getId());

			nodesBuilder.add(nodeMetadataBuilder.build());
		}
		return nodesBuilder.build();
	}

	@Override
	public void destroyNode(final String id) {
		cloud.removeNode(id);
	}

	@Override
	public NodeWithInitialCredentials createNodeWithGroupEncodedIntoName(
			String group, String name, Template template) {

		DeclarativeNode abstractNode = cloud.createNode();

		NodeMetadataBuilder builder = new NodeMetadataBuilder();
		builder.ids("" + abstractNode.getId());
		// TODO Shortcircuit then add name/group in the spec if needed
		builder.name(name);
		builder.group(group);
		// Not even sure what this is about !
		builder.credentials(LoginCredentials.builder().user("root")
				.password("id").build());
		NodeMetadata node = builder.build();

		// Not sure this is actually relevant or just an artifact for the
		// original stub implementation
		return new NodeWithInitialCredentials(node);

		// NodeMetadataBuilder builder = new NodeMetadataBuilder();
		// String id = idProvider.get() + "";
		// builder.ids(id);
		//
		// // using a predictable name so tests will pass
		// builder.hostname(group);
		// builder.tags(template.getOptions().getTags());
		// builder.userMetadata(template.getOptions().getUserMetadata());
		//
		// builder.location(location.get());
		// builder.imageId(template.getImage().getId());
		// builder.operatingSystem(template.getImage().getOperatingSystem());
		// builder.status(Status.PENDING);
		// builder.publicAddresses(ImmutableSet.<String> of(publicIpPrefix +
		// id));
		// builder.privateAddresses(ImmutableSet.<String> of(privateIpPrefix +
		// id));

		// NodeMetadata node = builder.build();
		// Update the STATE
		// nodes.put(node.getId(), node);
		//
		// if (template.getOptions().getGroups().size() > 0) {
		// final String groupId = Iterables.getFirst(template.getOptions()
		// .getGroups(), "0");
		//
		// Optional<SecurityGroup> secGroup = Iterables.tryFind(
		// // securityGroupExtension.get().listSecurityGroups(),
		// // new Predicate<SecurityGroup>() {
		// // @Override
		// // public boolean apply(SecurityGroup input) {
		// // return input.getId().equals(groupId);
		// // }
		// // });
		//
		// // if (secGroup.isPresent()) {
		// // groupsForNodes.put(node.getId(), secGroup.get());
		// // }
		// }
		// TODO Note the time/delay here ! Use an asynch "modification" we can
		// adopt something similar: given the state transitions and the expected
		// time ranges just execute the state machine
		// setStateOnNodeAfterDelay(Status.RUNNING, node, 100);
		// return new NodeWithInitialCredentials(node);
	}

	// TODO Require state definition. Not yet implemented
	@Override
	public void rebootNode(String id) {
		// TODO Do nothing for the moment, later this will become something like
		// running -> stop -> running
	}

	@Override
	public void resumeNode(String id) {
		cloud.startNode(id);
	}

	@Override
	public void suspendNode(String id) {
		cloud.suspendNode(id);
	}

	/**
	 * Define the flavors of the instances
	 * 
	 * @return
	 */
	@Override
	public Iterable<Hardware> listHardwareProfiles() {
		// This is similar to listImage

		Builder<Hardware> flavors = ImmutableList.builder();

		flavors.add(new HardwareBuilder()
				.ids("1")
				.name("small")
				.processors(ImmutableList.of(new Processor(1, 1.0)))
				.ram(1740)
				.volumes(
						ImmutableList.<Volume> of(new VolumeImpl((float) 160,
								true, false))).build());

		flavors.add(new HardwareBuilder()
				.ids("2")
				.name("medium")
				.processors(ImmutableList.of(new Processor(4, 1.0)))
				.ram(7680)
				.volumes(
						ImmutableList.<Volume> of(new VolumeImpl((float) 850,
								true, false))).build());

		flavors.add(new HardwareBuilder()
				.ids("3")
				.name("large")
				.processors(ImmutableList.of(new Processor(8, 1.0)))
				.ram(15360)
				.volumes(
						ImmutableList.<Volume> of(new VolumeImpl((float) 1690,
								true, false))).build());

		return flavors.build();
	}

	@Override
	public Iterable<Image> listImages() {
		return cloud.getAllImages();
	}

	@Override
	public Image getImage(String id) {
		return find(listImages(), ImagePredicates.idEquals(id), null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<Location> listLocations() {
		Builder<Location> locations = ImmutableList.builder();
		return locations.build();
	}
}
