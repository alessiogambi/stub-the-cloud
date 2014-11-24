package org.jclouds.compute.declarativestub.providers;

import java.util.Set;

import org.jclouds.compute.domain.Hardware;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ChainedHardwareSetProvider implements Provider<Set<Hardware>> {

	@Inject(optional = true)
	@Named("OVERRIDE_HARDWARE_SET")
	Set<Hardware> override;

	@Inject
	@Named("DEFAULT_HARDWARE_SET")
	Set<Hardware> defaultHardwares;

	@Override
	public Set<Hardware> get() {
		System.err.println("ChainedHardwareSetProvider.get()" + override + " - " + defaultHardwares);
		if (override != null) {
			return override;
		}
		return defaultHardwares;
	}

}
