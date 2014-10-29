package org.jclouds.compute.declarativestub.config;

import static com.google.common.collect.Iterables.find;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.declarativestub.core.DeclarativeCloud;
import org.jclouds.compute.declarativestub.core.DeclarativeNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.annotations.FreshObjects;

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

	@Inject
	public DeclarativeStubComputeServiceAdapter(/*
												 * TODO Inject the cloud
												 * specifications hereand Inject
												 * the initial state of the
												 * cloud
												 */) {
		cloud = new DeclarativeCloud();
	}

	@Override
	public NodeMetadata getNode(String id) {
		// Bridge the inner model/spec to the outer one
		DeclarativeNode abstractNode = cloud.getNode(id);

		NodeMetadataBuilder builder = new NodeMetadataBuilder();
		builder.ids("" + abstractNode.getId());
		NodeMetadata node = builder.build();

		return node;
	}

	@Override
	@FreshObjects(cls = Iterable.class, num = 1, typeParams = { NodeMetadata.class })
	@Ensures({ "return.elts == this.nodes.elts " })
	public Iterable<NodeMetadata> listNodes() {
		return Squander.exe(this);
	}

	@Override
	public Iterable<NodeMetadata> listNodesByIds(Iterable<String> ids) {
		return null;
	}

	@Override
	public void destroyNode(final String id) {
	}

	@Override
	public void rebootNode(String id) {
	}

	@Override
	public void resumeNode(String id) {
	}

	@Override
	public void suspendNode(String id) {
	}

	@Override
	public NodeWithInitialCredentials createNodeWithGroupEncodedIntoName(
			String group, String name, Template template) {
		// NodeMetadataBuilder builder = new NodeMetadataBuilder();
		// String id = idProvider.get() + "";
		// builder.ids(id);
		// builder.name(name);
		// // using a predictable name so tests will pass
		// builder.hostname(group);
		// builder.tags(template.getOptions().getTags());
		// builder.userMetadata(template.getOptions().getUserMetadata());
		// builder.group(group);
		// builder.location(location.get());
		// builder.imageId(template.getImage().getId());
		// builder.operatingSystem(template.getImage().getOperatingSystem());
		// builder.status(Status.PENDING);
		// builder.publicAddresses(ImmutableSet.<String> of(publicIpPrefix +
		// id));
		// builder.privateAddresses(ImmutableSet.<String> of(privateIpPrefix +
		// id));
		// builder.credentials(LoginCredentials.builder().user("root")
		// .password(passwordPrefix + id).build());
		// NodeMetadata node = builder.build();
		// nodes.put(node.getId(), node);
		//
		// if (template.getOptions().getGroups().size() > 0) {
		// final String groupId = Iterables.getFirst(template.getOptions()
		// .getGroups(), "0");
		//
		// // Optional<SecurityGroup> secGroup = Iterables.tryFind(
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
		//
		// setStateOnNodeAfterDelay(Status.RUNNING, node, 100);
		// return new NodeWithInitialCredentials(node);
		return null;
	}

	@Override
	public Iterable<Hardware> listHardwareProfiles() {

		// TODO Here is where PRECONDITIONS over DEPENDENT RESOURCES ARE
		// INJECTED !!!

		return ImmutableSet.<Hardware> of(
				DeclarativeStubComputeServiceDependenciesModule.stub("small",
						1, 1740, 160),
				DeclarativeStubComputeServiceDependenciesModule.stub("medium",
						4, 7680, 850),
				DeclarativeStubComputeServiceDependenciesModule.stub("large",
						8, 15360, 1690));
	}

	@Override
	public Iterable<Image> listImages() {
		// initializing as a List, as ImmutableSet does not allow you to put
		// duplicates
		Builder<Image> images = ImmutableList.builder();
		return images.build();
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
