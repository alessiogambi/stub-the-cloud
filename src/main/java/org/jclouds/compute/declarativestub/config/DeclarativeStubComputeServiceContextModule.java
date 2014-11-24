package org.jclouds.compute.declarativestub.config;

import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.concurrent.SingleThreaded;

@SingleThreaded
public class DeclarativeStubComputeServiceContextModule extends JCloudsNativeComputeServiceAdapterContextModule {

	public DeclarativeStubComputeServiceContextModule() {
		super(DeclarativeStubComputeServiceAdapter.class);
	}

	@Override
	protected void configure() {
		install(new DeclarativeStubComputeServiceDependenciesModule());
		super.configure();
	}
}
