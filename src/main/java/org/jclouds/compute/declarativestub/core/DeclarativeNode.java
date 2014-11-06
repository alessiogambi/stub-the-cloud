package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataStatus;
import org.jclouds.domain.Location;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant({ "this.status != null", "this.id != null", "this.image != null",
		"this.hardware != null", "this.location!= null" })
public class DeclarativeNode {

	// TODO Add name
	// TODO Add group

	public String toString() {
		return "Node = " + "ID:" + getId() + " -- " + this.hashCode() + "\n"
				+ "Location: " + getLocation() + "\n" //
				+ "Image: " + getImage() + "\n" //
				+ "Hardware: " + getHardware() + "\n" //
				+ "Status: " + getStatus() + "\n";
	}

	private String id;
	private NodeMetadataStatus status;
	private Image image;
	private Location location;
	private Hardware hardware;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Hardware getHardware() {
		return hardware;
	}

	public void setHardware(Hardware hardware) {
		this.hardware = hardware;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public NodeMetadataStatus getStatus() {
		return status;
	}

	public void setState(NodeMetadataStatus status) {
		this.status = status;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
