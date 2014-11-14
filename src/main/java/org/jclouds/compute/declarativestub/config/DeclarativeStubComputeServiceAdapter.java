package org.jclouds.compute.declarativestub.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
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
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import com.google.common.collect.ImmutableList;

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
	private final Map<String, NodeMetadata> cachedNodes;

	// private final Set<Image> cachedImages;
	// private final Set<Hardware> cachedHardwares;

	@Inject
	public DeclarativeStubComputeServiceAdapter(DeclarativeCloud cloud) {
		this.cloud = cloud;
		this.cachedNodes = new HashMap<String, NodeMetadata>();
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
		ImmutableList.Builder<NodeMetadata> nodesBuilder = ImmutableList.builder();
		for (DeclarativeNode node : cloud.getAllNodes()) {
			NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
			nodeMetadataBuilder.ids("" + node.getId());

			nodesBuilder.add(nodeMetadataBuilder.build());
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
			nodesBuilder.add(cachedNodes.remove(abstractNode.getId()));
		}
		return nodesBuilder.build();
	}

	@Override
	public void destroyNode(final String id) {
		cloud.removeNode(id);
		cachedNodes.remove(id);
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

		// Prepare the output !

		NodeMetadataBuilder builder = new NodeMetadataBuilder();
		builder.ids(node.getId());
		builder.name(node.getName());
		builder.group(node.getGroup());
		builder.imageId(node.getImage().getId());
		//
		builder.location(template.getLocation());
		builder.status(NodeMetadataStatus.RUNNING);
		builder.operatingSystem(template.getImage().getOperatingSystem());
		builder.credentials(LoginCredentials.builder().user("root").password("id").build());
		builder.hostname(group);
		builder.tags(template.getOptions().getTags());
		builder.userMetadata(template.getOptions().getUserMetadata());

		// This is what we cache !
		NodeMetadata nodeMetadata = builder.build();
		// Cached nodes
		cachedNodes.put(nodeMetadata.getId(), nodeMetadata);

		return new NodeWithInitialCredentials(nodeMetadata);

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
		// System.out.println("DeclarativeStubComputeServiceAdapter.listHardwareProfiles()");
		// return cloud.getAllHardwares();
		// return cachedHardwares;
		return null;
	}

	/**
	 * Define the available images TODO: generative approach would be better here ! We READ this BUT we DO NOT MODIFY
	 * this with the ComputeAPI !!
	 * 
	 * @return
	 */
	@Override
	public Iterable<Image> listImages() {
		// System.out.println("DeclarativeStubComputeServiceAdapter.listImages()");
		// return cloud.getAllImages();
		// return cachedImages;
		return null;
	}

	@Override
	public Image getImage(String id) {
		// return cloud.getImage(id);
		// return find(listImages(), ImagePredicates.idEquals(id), null);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<Location> listLocations() {
		// return cloud.getAllLocations();
		return null;
	}
}
