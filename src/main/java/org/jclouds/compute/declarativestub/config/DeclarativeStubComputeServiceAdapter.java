package org.jclouds.compute.declarativestub.config;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;

import at.ac.tuwien.cloud.JcloudsStub;

/**
 * This class defined the spec for a <strong>single-user<strong> cloud !
 * 
 * A multi-user implementation can be easily obtained by injecting a nodes-to-id object like in
 * {@StubComputeServiceAdapter}
 * 
 * Note that the cloud must be a read-only objects for getter methods, the STUB itself represents the CLIENT side of the
 * cloud
 * 
 * In this implementation the cloud is modeled in a pure-abstract way and the Adapter mainly manages the Immutability of
 * the clouds on read and the actual object types. In the next versions we'll try to reason/model/specify directly in
 * terms of the actual objects that we want to mock.
 * 
 * 
 * @author alessiogambi
 *
 */
@Singleton
public class DeclarativeStubComputeServiceAdapter implements JCloudsNativeComputeServiceAdapter {

	private final JcloudsStub stub;

	// This is for the SSH mocking
	private final String publicIpPrefix;
	private final String privateIpPrefix;

	@Inject
	public DeclarativeStubComputeServiceAdapter(//
			@Named("PUBLIC_IP_PREFIX") String publicIpPrefix,//
			@Named("PRIVATE_IP_PREFIX") String privateIpPrefix,//
			JcloudsStub stub) {
		//
		this.publicIpPrefix = publicIpPrefix;
		this.privateIpPrefix = privateIpPrefix;
		//
		this.stub = stub;
	}

	@Override
	public NodeMetadata getNode(String id) {
		return stub.getNode(id);
	}

	@Override
	public Iterable<NodeMetadata> listNodes() {
		// ImmutableList.Builder<NodeMetadata> nodesBuilder = ImmutableList.builder();
		// for (DeclarativeNode abstractNode : cloud.getAllNodes()) {
		// nodesBuilder.add(toNodeMetadata(abstractNode));
		// }
		// return nodesBuilder.build();
		return stub.getAllNodes();
	}

	@Override
	public Iterable<NodeMetadata> listNodesByIds(Iterable<String> ids) {
		return stub.getAllNodesById(ids);
	}

	@Override
	public void destroyNode(final String id) {
		stub.destroyNode(id);
	}

	@Override
	public NodeWithInitialCredentials createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
		return new NodeWithInitialCredentials(stub.createNode(group, name, template));

	}

	@Override
	public void rebootNode(String id) {
		stub.rebootNode(id);
	}

	@Override
	public void resumeNode(String id) {
		stub.resumeNode(id);
	}

	@Override
	public void suspendNode(String id) {
		stub.suspendNode(id);
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
		return stub.getAllHardwares();
	}

	/**
	 * Define the available images TODO: generative approach would be better here ! We READ this BUT we DO NOT MODIFY
	 * this with the ComputeAPI !!
	 * 
	 * @return
	 */
	@Override
	public Iterable<Image> listImages() {
		return stub.getAllImages();
	}

	@Override
	public Image getImage(final String id) {
		return stub.getImage(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<Location> listLocations() {
		return stub.getAllLocations();
	}
}
