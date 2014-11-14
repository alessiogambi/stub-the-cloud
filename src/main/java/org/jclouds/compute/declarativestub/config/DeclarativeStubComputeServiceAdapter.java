package org.jclouds.compute.declarativestub.config;

import static com.google.common.collect.Iterables.find;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.declarativestub.core.DeclarativeCloud;
import org.jclouds.compute.declarativestub.core.DeclarativeHardware;
import org.jclouds.compute.declarativestub.core.DeclarativeImage;
import org.jclouds.compute.declarativestub.core.DeclarativeLocation;
import org.jclouds.compute.declarativestub.core.DeclarativeNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadataStatus;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * This class defined the spec for a <strong>single-user<strong> cloud !
 * 
 * A multi-user implementation can be easily obtained by injecting a nodes-to-id object like in
 * {@StubComputeServiceAdapter}
 * 
 * @author alessiogambi
 *
 */
@Singleton
public class DeclarativeStubComputeServiceAdapter implements JCloudsNativeComputeServiceAdapter {

	// Spec of the Cloud implemented with Squander (MIT)
	// Possibly this implementation should be injected in the constructor !
	private final DeclarativeCloud cloud;

	// Cached nodes
	// private final Map<String, NodeMetadata> cachedNodes;
	// Keep the actual "real" instances
	private final Set<Image> images;
	private final Set<Hardware> hardwares;
	private final Set<Location> locations;

	private Map<String, TemplateOptions> cachedNodeIDuserMetadata;

	//
	private final String publicIpPrefix;
	private final String privateIpPrefix;

	@Inject
	public DeclarativeStubComputeServiceAdapter(
	/*
	 * 
	 */
	@Named("PUBLIC_IP_PREFIX") String publicIpPrefix, @Named("PRIVATE_IP_PREFIX") String privateIpPrefix,
	/* The original instances */
	Set<Image> images, Set<Hardware> hardwares, Set<Location> locations, //
			/* The cloud stub */
			@Named("DETACHED_CLOUD") DeclarativeCloud cloud) {
		this.cloud = cloud;
		//
		this.images = images;
		this.locations = locations;
		this.hardwares = hardwares;
		//
		this.publicIpPrefix = publicIpPrefix;
		this.privateIpPrefix = privateIpPrefix;
		//
		this.cachedNodeIDuserMetadata = new HashMap<String, TemplateOptions>();
	}

	@Override
	public NodeMetadata getNode(String id) {
		System.err.println("DeclarativeStubComputeServiceAdapter.getNode() " + id);
		System.err.println(" cloud.getAllNodes() " + cloud.getAllNodes());

		return toNodeMetadata(cloud.getNode(id));
	}

	private NodeMetadata toNodeMetadata(final DeclarativeNode node) {

		if (node == null) {
			// Defensive !
			return null;
		}

		Location location = ((Location) find(locations, new Predicate<Location>() {

			@Override
			public boolean apply(Location input) {
				return input.getId().equals(node.getLocation().getId());
			}
		}));

		Image image = ((Image) find(images, new Predicate<Image>() {

			@Override
			public boolean apply(Image input) {
				return input.getId().equals(node.getImage().getId());
			}
		}));

		Map<String, String> metadata = (cachedNodeIDuserMetadata.get(node.getId()) != null) ? cachedNodeIDuserMetadata
				.get(node.getId()).getUserMetadata() : new HashMap<String, String>();
		Set<String> tags = (cachedNodeIDuserMetadata.get(node.getId()) != null) ? cachedNodeIDuserMetadata.get(
				node.getId()).getTags() : new HashSet<String>();

		return new NodeMetadataBuilder()//
				.ids(node.getId())//
				.name(node.getName())//
				.group(node.getGroup())//
				.imageId(node.getImage().getId())//
				.status(node.getStatus())//
				// NOT SURE
				.hostname(node.getGroup())//
				// The address here must match the one in the expect !
				.privateAddresses(ImmutableSet.<String> builder().add(privateIpPrefix + node.getId()).build())//
				.publicAddresses(ImmutableSet.<String> builder().add(publicIpPrefix + node.getId()).build())//
				// The passwors must match the ones in the expect
				.credentials(LoginCredentials.builder().user("root").password("password" + node.getId()).build())//
				.location(location)//
				.operatingSystem(image.getOperatingSystem())//
				.userMetadata(metadata)//
				.tags(tags)//
				.build();
	}

	@Override
	public Iterable<NodeMetadata> listNodes() {
		ImmutableList.Builder<NodeMetadata> nodesBuilder = ImmutableList.builder();
		for (DeclarativeNode abstractNode : cloud.getAllNodes()) {
			nodesBuilder.add(toNodeMetadata(abstractNode));
		}
		return nodesBuilder.build();
	}

	@Override
	public Iterable<NodeMetadata> listNodesByIds(Iterable<String> ids) {
		Set<String> _ids = new HashSet<String>();
		for (String id : ids) {
			_ids.add(id);
		}
		ImmutableList.Builder<NodeMetadata> nodesBuilder = ImmutableList.builder();

		for (DeclarativeNode abstractNode : cloud.getNodes(_ids)) {
			nodesBuilder.add(toNodeMetadata(abstractNode));
		}
		return nodesBuilder.build();
	}

	@Override
	public void destroyNode(final String id) {
		System.err.println("DeclarativeStubComputeServiceAdapter.destroyNode() " + id);
		cloud.removeNode(id);
	}

	@Override
	public NodeWithInitialCredentials createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
		// Prepare the data !
		// Allocate a new ID
		String nodeId = DeclarativeCloud.FactoryId.allocateID();

		DeclarativeLocation location = cloud.getLocation(template.getLocation().getId());
		DeclarativeImage image = cloud.getImage(template.getImage().getId());
		DeclarativeHardware hardware = cloud.getHardware(template.getHardware().getId());

		// Create a new node given all the parameters !
		DeclarativeNode node = cloud.createNode(nodeId, name, group, location, hardware, image);

		cachedNodeIDuserMetadata.put(node.getId(), template.getOptions());
		// Prepare the output !

		return new NodeWithInitialCredentials(toNodeMetadata(node));

	}

	// TODO Require TIME/TIMED Specs or an executor implementatino to evolve the
	// state
	// somehow. Times can always be generated from the specs.
	@Override
	public void rebootNode(String id) {
		// TODO Do nothing for the moment, later this will become something like
		// running -> stop -> running
		cloud.startNode(id);
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
	 * Define the flavors of the instances TODO: generative approach would be better here !
	 * 
	 * We READ this BUT we DO NOT MODIFY this with the ComputeAPI !!
	 * 
	 * @return
	 */
	@Override
	public Iterable<Hardware> listHardwareProfiles() {
		return hardwares;
	}

	/**
	 * Define the available images TODO: generative approach would be better here ! We READ this BUT we DO NOT MODIFY
	 * this with the ComputeAPI !!
	 * 
	 * @return
	 */
	@Override
	public Iterable<Image> listImages() {
		return images;
	}

	@Override
	public Image getImage(final String id) {
		cloud.getImage(id);
		return ((Image) find(images, new Predicate<Image>() {

			@Override
			public boolean apply(Image input) {
				return (input.getId().equals(id));
			}

		}));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<Location> listLocations() {
		return locations;
	}
}
