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

	// Spec of the Cloud implemented with Squander (MIT)
	// Possibly this implementation should be injected in the constructor !
	private DeclarativeCloud cloud;

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
	 * // Implementation of the PER-CLIENT view of virtual machines. The Per
	 * client view is managed by guice
	 * 
	 * @Inject public StubComputeServiceAdapter(ConcurrentMap<String,
	 * NodeMetadata> nodes, // Execute State changes ?!
	 * 
	 * @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService
	 * ioExecutor, // Generate unique id ?!
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
	 * 
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
			/** Provided via TODO: Find Me ! */
			Supplier<Location> location,
			/**
			 * Provided via TODO Check me
			 * {@link BaseComputeServiceContextModule#provideOsVersionMap}
			 */
			Map<OsFamily, Map<String, String>> osToVersionMap) {
		//
		cloud = new DeclarativeCloud(provideImages());

		this.location = location;
		this.osToVersionMap = osToVersionMap;
	}

	// Cannot be called from dependency modules withouth firstly inject os and
	// location deps because it requires additional resources injected !
	protected Set<Image> provideImages() {
		ImmutableSet.Builder<Image> images = ImmutableSet.<Image> builder();
		int id = 1;

		for (boolean is64Bit : new boolean[] { true, false })
			for (Entry<OsFamily, Map<String, String>> osVersions : osToVersionMap
					.entrySet()) {

				for (String version : ImmutableSet.copyOf(osVersions.getValue()
						.values())) {

					String desc = String.format("declarative-stub %s %s",
							osVersions.getKey(), is64Bit);

					// THIS SHOULD BE CREATED USING THE SPEC OF THE CLOUD AND A
					// SMART
					// PRECONDITION
					Image image = new ImageBuilder()
							.ids(id++ + "")
							.name(osVersions.getKey().name())
							.location(location.get())
							.operatingSystem(
									new OperatingSystem(osVersions.getKey(),
											desc, version, null, desc, is64Bit))
							.description(desc).status(ImageStatus.AVAILABLE)
							.build();

					System.out.println(

					" \t DeclarativeStubComputeServiceAdapter.listImages() Creating IMAGE "
							+ image.getId() + " - " + image.getDescription());

					images.add(image);

				}
			}
		return images.build();
	}

	@Override
	public NodeMetadata getNode(String id) {
		DeclarativeNode abstractNode = cloud.getNode(id);
		NodeMetadataBuilder builder = new NodeMetadataBuilder();
		builder.ids("" + abstractNode.getId());
		NodeMetadata node = builder.build();

		return node;
	}

	@Override
	public Iterable<NodeMetadata> listNodes() {
		ImmutableList.Builder<NodeMetadata> nodesBuilder = ImmutableList
				.builder();
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
		ImmutableList.Builder<NodeMetadata> nodesBuilder = ImmutableList
				.builder();
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

	}

	// TODO Require TIME/TIMED Specs or an executor implementatino to evolve the
	// state
	// somehow. Times can always be generated from the specs.
	@Override
	public void rebootNode(String id) {
		// TODO Do nothing for the moment, later this will become something like
		// running -> stop -> running
		cloud.startNode("" + Integer.parseInt(id));
	}

	@Override
	public void resumeNode(String id) {
		// TODO Implement resume ?
		cloud.startNode(id);
	}

	@Override
	public void suspendNode(String id) {
		cloud.suspendNode(id);
	}

	/**
	 * Define the flavors of the instances TODO: generative approach would be
	 * better here !
	 * 
	 * We READ this BUT we DO NOT MODIFY this with the ComputeAPI !!
	 * 
	 * @return
	 */
	@Override
	public Iterable<Hardware> listHardwareProfiles() {
		// This is similar to listImage

		ImmutableList.Builder<Hardware> flavors = ImmutableList.builder();

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

	/**
	 * Define the available images TODO: generative approach would be better
	 * here ! We READ this BUT we DO NOT MODIFY this with the ComputeAPI !!
	 * 
	 * @return
	 */
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
		ImmutableList.Builder<Location> locations = ImmutableList.builder();
		return locations.build();
	}
}
