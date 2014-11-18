package org.jclouds.compute.declarativestub.config;

import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.concurrent.SingleThreaded;

@SingleThreaded
public class DeclarativeStubComputeServiceContextModule extends JCloudsNativeComputeServiceAdapterContextModule {

	public DeclarativeStubComputeServiceContextModule() {
		// This is basically the implementation of the CLIENT invoking the Cloud
		// and it can be out hook to inject the declarative logic into the loop
		super(DeclarativeStubComputeServiceAdapter.class);
	}

	// Basic module dependecies and anything needed to run Adapter Fine ?
	@Override
	protected void configure() {
		// // This is the additional module/logic injected to provide the
		// // predefined objects/methods
		// // TODO this would be nice to extend via a mechanism that reads the
		// // available information from a cloud installation
		install(new DeclarativeStubComputeServiceDependenciesModule());
		//
		super.configure();
	}

	// Define basic
	// FIXME: Optional extensions can be provided here !
}
