package org.jclouds.compute.declarativestub.core;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import edu.mit.csail.sdg.annotations.SpecField;

@SpecField({ "id : int | id == this.specID" })
public class DeclarativeNodeMetadata {// implements DeclarativeNode {

	private String specID;

}
