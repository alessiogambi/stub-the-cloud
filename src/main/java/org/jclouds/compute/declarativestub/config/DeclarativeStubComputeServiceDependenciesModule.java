package org.jclouds.compute.declarativestub.config;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.predicates.SocketOpen;

import at.ac.tuwien.cloud.JCloudsStubFactory;
import at.ac.tuwien.cloud.JcloudsStub;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * This module should contains the declarative logic that implements the specification of the cloud and that can be
 * replaced at startup time !
 * 
 * @author alessiogambi
 *
 */
public class DeclarativeStubComputeServiceDependenciesModule extends AbstractModule {

	public static final String DEFAULT_JCLOUD_STUB = "default";

	@Override
	protected void configure() {
		System.out.println("DeclarativeStubComputeServiceDependenciesModule.configure()");
	}

	@Singleton
	@Provides
	protected JcloudsStub providesDefaultJcloudsStub() {
		return JCloudsStubFactory.getStub(DEFAULT_JCLOUD_STUB);
	}

	// This is only needed for the way the SSH mock is defined
	@Singleton
	@Provides
	@Named("PUBLIC_IP_PREFIX")
	String publicIpPrefix() {
		return "144.175.1.";
	}

	// This is only needed for the way the SSH mock is defined
	@Singleton
	@Provides
	@Named("PRIVATE_IP_PREFIX")
	String privateIpPrefix() {
		return "10.1.1.";
	}

	// This is only needed for the way the SSH mock is defined
	@Singleton
	@Provides
	@Named("PASSWORD_PREFIX")
	String passwordPrefix() {
		return "password";
	}

	// This is only needed for the way the SSH mock is defined
	@Singleton
	@Provides
	SocketOpen socketOpen(StubSocketOpen in) {
		return in;
	}

	@Singleton
	public static class StubSocketOpen implements SocketOpen {
		// This is needed to "open" the ports only for the available nodes
		private final JcloudsStub cloudStub;

		@Inject
		public StubSocketOpen(JcloudsStub cloudStub) {
			System.err.println("DeclarativeStubComputeServiceDependenciesModule.StubSocketOpen.StubSocketOpen()");
			this.cloudStub = cloudStub;
		}

		@Override
		public boolean apply(HostAndPort input) {

			System.err.println("DeclarativeStubComputeServiceDependenciesModule.StubSocketOpen.apply() "
					+ input.getHostText());

			// If there is a RUNNING node that match the ip, then open then enables ssh
			for (NodeMetadata node : cloudStub.getAllNodes()) {
				if (node.getPrivateAddresses().contains(input.getHostText())
						|| node.getPublicAddresses().contains(input.getHostText())) {
					System.out
							.println("DeclarativeStubComputeServiceDependenciesModule.StubSocketOpen.apply() Found matching node");
					return true;
				}
			}
			System.err
					.println("DeclarativeStubComputeServiceDependenciesModule.StubSocketOpen.apply() Cannot find a matching node");
			return false;

		}
	}
}
