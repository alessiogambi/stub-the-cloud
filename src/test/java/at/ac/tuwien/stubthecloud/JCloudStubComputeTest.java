package at.ac.tuwien.stubthecloud;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.declarativestub.DeclarativeStubApiMetadata;
import org.jclouds.compute.declarativestub.config.DeclarativeStubComputeServiceContextModule;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class JCloudStubComputeTest {

	/*
	 * Not sure why it cannot find the ApiMetadata on the classpath!! //
	 * ComputeServiceContext context = ContextBuilder //
	 * .newBuilder("declarative-stub") // .modules(ImmutableSet.<Module> of(new
	 * SLF4JLoggingModule())) // .buildView(ComputeServiceContext.class);
	 */
	public static void main(String[] args) {
		// Here define the module to use as ComputeService

		ComputeServiceContext context = ContextBuilder
				.newBuilder(new DeclarativeStubApiMetadata())
				.credentials("alfa", "alfa")
				.modules(
						ImmutableSet
								.<Module> of(
										new DeclarativeStubComputeServiceContextModule(),
										new SLF4JLoggingModule()))
				.buildView(ComputeServiceContext.class);

		ComputeService computeService = context.getComputeService();

		System.out.println("Nodes Alfa : " + computeService.listNodes());

	}

	private static void destroyOneNode(ComputeService computeService) {
		for (ComputeMetadata node : computeService.listNodes()) {
			computeService.destroyNode(node.getId());
			break;
		}
	}

	private static void printDetail(ComputeService computeService) {
		for (ComputeMetadata node : computeService.listNodes()) {

			NodeMetadata metadata = computeService
					.getNodeMetadata(node.getId());

			System.out.println("Node Metadata");
			System.out.println(metadata.getId());
			// System.out.println(metadata.getProviderId());
			System.out.println(metadata.getLocation());
			System.out.println(metadata.getName());
			System.out.println(metadata.getGroup());
			System.out.println(metadata.getHardware());
			System.out.println(metadata.getImageId());
			System.out.println(metadata.getOperatingSystem());
			System.out.println(metadata.getStatus());
			System.out.println(metadata.getPrivateAddresses());
			System.out.println(metadata.getPublicAddresses());
			System.out.println(metadata.getCredentials());
			System.out.println("");
			System.out.println("Compute Metadata");
			System.out.println(node.getId());
			System.out.println(node.getProviderId());
			System.out.println(node.getName());
			System.out.println(node.getLocation());
			System.out.println(node.getTags());
			System.out.println(node.getType());
			System.out.println(node.getUserMetadata());
			System.out.println("");
			System.out.println("");
		}
	}
}
