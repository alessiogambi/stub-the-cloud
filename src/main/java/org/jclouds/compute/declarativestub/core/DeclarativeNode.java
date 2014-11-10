package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.NodeMetadataStatus;

//@Invariant({ "this.status != null", "this.id != null", "this.image != null", "this.hardware != null",
//		"this.location!= null",
//		/*
//		 * Make sure Node location is the same as Hardware and Image location
//		 */
//		"this.location = this.image.location" //
//})
public class DeclarativeNode {

	public String toString() {
		return "Node = " + "ID:" + getId() + " -- " + this.hashCode() + "\n";
	}

	private String id;
	private NodeMetadataStatus status;

	// private Image image;
	// private Location location;
	// private Hardware hardware;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public NodeMetadataStatus getStatus() {
		return status;
	}

	public void setState(NodeMetadataStatus status) {
		this.status = status;
	}
}
