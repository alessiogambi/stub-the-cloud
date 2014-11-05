package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataStatus;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant({ "this.status != null", "this.id != null", "this.image != null" })
public class DeclarativeNode {

	private String id;
	private NodeMetadataStatus status;
	private Image image;

	public String toString() {
		return "Node " + getId() + " -- " + this.hashCode() + "\n" //
				+ "Status " + getStatus() + "\n"//
				+ "Image " + getImage() + "\n";
	}

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

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
