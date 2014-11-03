package org.jclouds.compute.declarativestub.core;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataStatus;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

@SpecField({
/**/
"id: one String",
/**/
"image : one org.jclouds.compute.domain.Image",
/**/
"status : one org.jclouds.compute.domain.NodeMetadataStatus" })
@Invariant({ "this.status != null", "this.id != null", "this.image != null" })
public class DeclarativeNode {

	public String toString() {
		return "Node = " + "ID:" + getId() + " -- " + this.hashCode() + "\n"
				+ "Image: " + getImage() + "\n" //
				+ "Status: " + getStatus() + "\n";
	}

	@Ensures({ "return = this.id" })
	public String getId() {
		return Squander.exe(this);
	}

	@Ensures({ "return = this.image" })
	public Image getImage() {
		return Squander.exe(this);
	}

	@Ensures({ "return = this.status" })
	public NodeMetadataStatus getStatus() {
		// return status;
		return Squander.exe(this);
	}
}
