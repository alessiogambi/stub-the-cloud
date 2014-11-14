package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.NodeMetadataStatus;

public class DeclarativeNode extends DeclarativeResource {

	public String toString() {
		return "Node = " + "ID:" + getId() + " -- " + this.hashCode() + "\n" + "Location: " + getLocation() + "\n" //
				+ "Image: " + getImage() + "\n" //
				+ "Hardware: " + getHardware() + "\n" //
		;
	}

	private String name;
	private String group;

	private DeclarativeImage image;
	private DeclarativeLocation location;
	private DeclarativeHardware hardware;

	// Are we polluting this with implementation details ?
	private NodeMetadataStatus status;

	public DeclarativeHardware getHardware() {
		return hardware;
	}

	public void setHardware(DeclarativeHardware hardware) {
		this.hardware = hardware;
	}

	public DeclarativeLocation getLocation() {
		return location;
	}

	public void setLocation(DeclarativeLocation location) {
		this.location = location;
	}

	public DeclarativeImage getImage() {
		return image;
	}

	public void setImage(DeclarativeImage image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public NodeMetadataStatus getStatus() {
		return this.status;
	}
}
