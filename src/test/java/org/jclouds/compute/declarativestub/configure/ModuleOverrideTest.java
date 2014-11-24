package org.jclouds.compute.declarativestub.configure;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class ModuleOverrideTest {

	ComputeServiceContext context;
	ComputeService service;

	@Test
	public void testDoNotOverride() {

		// FIXME I would like to specify a module and then override iff provided
		// Set<Module> modules = ImmutableSet.<Module> builder()
		// .add(new DeclarativeStubComputeServiceDefaultSetupModule()).build();
		context = ContextBuilder.newBuilder("declarative-stub").credentials("identity", "credential")// .modules(modules)
				.buildView(ComputeServiceContext.class);

		Assert.assertTrue(context.getComputeService().listImages().size() == 2);

	}

	@Test
	public void testOverride() {
		// Set<Module> modules = ImmutableSet.<Module> builder().add(new DefaultModuleOverride()).build();
		Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule(), new DefaultModuleOverride());

		context = ContextBuilder.newBuilder("declarative-stub").credentials("identity", "credential").modules(modules)
				.buildView(ComputeServiceContext.class);

		System.out.println("ModuleOverrideTest.testOverride() " + context.getComputeService().listImages());
		Assert.assertTrue(context.getComputeService().listImages().size() == 5);
	}

}
