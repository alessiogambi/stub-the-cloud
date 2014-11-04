package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.NodeMetadataStatus;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant({ "this.status != null", "this.id != 0" })
public class DeclarativeNode {

	private int id;
	// TODO This should be the same enum used by jclouds or at least mapped to
	// it
	private NodeMetadataStatus status; // 0 = stop, 1 = running

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
