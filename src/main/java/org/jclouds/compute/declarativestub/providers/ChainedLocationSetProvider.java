package org.jclouds.compute.declarativestub.providers;

import java.util.Set;

import org.jclouds.domain.Location;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ChainedLocationSetProvider implements Provider<Set<Location>> {

	@Inject(optional = true)
	@Named("OVERRIDE_LOCATION_SET")
	Set<Location> override;

	@Inject
	@Named("DEFAULT_LOCATION_SET")
	Set<Location> defaultImages;

	@Override
	public Set<Location> get() {
		System.err.println("ChainedLocationSetProvider.get() " + override + " - " + defaultImages);
		if (override != null) {
			return override;
		}
		return defaultImages;
	}

}
