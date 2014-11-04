package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.NodeMetadataStatus;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

@SpecField({
/* 1: Introduce status because otherwise we get a clsSpec=null */
"status : one org.jclouds.compute.domain.NodeMetadataStatus" })
@Invariant({ "this.status != null", "this.id != 0" })
public class DeclarativeNode {

	// Note that we are using actual implementations and not spec field !
	private int id;

	/*
	 * 1: Now the state is abstract so we do not need this variable. I guess
	 * especially if they are not linked via an abstract function.
	 */
	// private NodeMetadataStatus status;

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

	/*
	 * 1: Now the state is abstract we use post conditions on specField:status
	 */
	@Ensures("return = this.status")
	public NodeMetadataStatus getStatus() {
		// return status;
		return Squander.exe(this);
	}

	/*
	 * 1: Now the state is abstract we use post conditions on specField:status.
	 * Note that we cannot use clashin names !
	 */
	@Ensures("this.status=_status")
	public void setState(NodeMetadataStatus _status) {
		Squander.exe(this, _status);
	}
}
