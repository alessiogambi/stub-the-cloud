package org.jclouds.compute.declarativestub;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.declarativestub.config.DeclarativeStubComputeServiceContextModule;
import org.jclouds.compute.stub.config.StubComputeServiceContextModule;

/**
 * Implementation of {@link ApiMetadata} for jclouds in-memory (Stub) API
 */
// Inspired by StubApiMetada with the addition of Squander specifications (if
// possible)
// Apparently this class is needed to give GUICE/Injection framework the info to
// instantiate the compute service and the other stuff
// Seems a bit awkward to use...
public class DeclarativeStubApiMetadata extends BaseApiMetadata {

	@Override
	public Builder toBuilder() {
		return new Builder().fromApiMetadata(this);
	}

	public DeclarativeStubApiMetadata() {
		super(new Builder());
	}

	protected DeclarativeStubApiMetadata(Builder builder) {
		super(builder);
	}

	public static class Builder extends BaseApiMetadata.Builder<Builder> {

		protected Builder() {
			id("declarative-stub")
					.name("declarative in-memory (Stub) API")
					.identityName("Unused")
					.defaultIdentity("declarative-stub")
					.defaultCredential("declarative-stub")
					.defaultEndpoint("local memory")
					.documentation(
							URI.create("http://www.infosys.tuwien.ac.at/staff/agambi/blog/"))
					.view(ComputeServiceContext.class)
					// Not sure about the meaning of this module, maybe it
					// provides implementations of ComputeServiceContext method.
					.defaultModule(
							DeclarativeStubComputeServiceContextModule.class);

		}

		@Override
		public DeclarativeStubApiMetadata build() {
			return new DeclarativeStubApiMetadata(this);
		}

		@Override
		protected Builder self() {
			return this;
		}
	}
}
