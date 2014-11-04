package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataStatus;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant({ "this.status != null", "this.id != null", "this.image != null" })
public class DeclarativeNode {

	public String toString() {
		return "Node = " + "ID:" + getId() + " -- " + this.hashCode() + "\n"
				+ "Image: " + getImage() + "\n" //
				+ "Status: " + getStatus() + "\n";
	}

	private int id;
	private NodeMetadataStatus status;
	private Image image;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
