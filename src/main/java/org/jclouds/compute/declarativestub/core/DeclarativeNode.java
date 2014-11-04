package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.NodeMetadataStatus;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant({ "this.status != null", "this.id != null" })
public class DeclarativeNode {

	private String id;
	private NodeMetadataStatus status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String toString() {
		return "Node = " + getId() + " " + getStatus() + " -- "
				+ this.hashCode();
	}

	public NodeMetadataStatus getStatus() {
		return status;
	}

	public void setState(NodeMetadataStatus status) {
		this.status = status;
	}
}
