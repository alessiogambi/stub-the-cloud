package org.jclouds.compute.declarativestub.providers;

import java.util.Set;

import org.jclouds.compute.domain.Image;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ChainedImageSetProvider implements Provider<Set<Image>> {

	
	
	
	@Inject(optional = true)
	@Named("OVERRIDE_IMAGE_SET")
	Set<Image> override;

	@Inject
	@Named("DEFAULT_IMAGE_SET")
	Set<Image> defaultImages;

	@Override
	public Set<Image> get() {
		System.err.println("ChainedImageSetProvider.get() " + override + " - " + defaultImages);
		if (override != null) {
			return override;
		}
		return defaultImages;
	}

}
