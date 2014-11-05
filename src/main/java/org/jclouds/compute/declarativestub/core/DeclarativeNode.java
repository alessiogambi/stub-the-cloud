package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataStatus;
import org.jclouds.domain.Location;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant({ "this.status != null", "this.id != null", "this.image != null",
		"this.location!= null" })
public class DeclarativeNode {

	// TODO Add name
	// TODO Add group

	// TODO Add Flavor
	
	public String toString() {
		return "Node = " + "ID:" + getId() + " -- " + this.hashCode() + "\n"
				+ "Location: " + getLocation() + "\n" //
				+ "Image: " + getImage() + "\n" //
				+ "Status: " + getStatus() + "\n";
	}

	private String id;
	private NodeMetadataStatus status;
	private Image image;
	private Location location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
