package org.jclouds.compute.declarativestub.core;

import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.annotations.FreshObjects;
import edu.mit.csail.sdg.squander.annotations.Options;

/**
 * This abstract class contains some of the specifications of a generic cloud
 * 
 * @author alessiogambi
 *
 */

@SpecField({ "vms : set DeclarativeNode" })
@Invariant({//
/* All the VM must have unique ID */
"all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
/* Null is not an option */
"null ! in this.vms" })
public class DeclarativeCloud {

	public DeclarativeCloud() {
		init();
	}

	@Ensures({ "no this.vms" })
	private void init() {
		Squander.exe(this);
	}

	@Ensures("return.id == id")
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Modifies("return.id")
	public DeclarativeNode getNode(String id) {
		return Squander.exe(this, id);
	}

	@Ensures("return.elts == this.vms")
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeNode> getAllNodes() {
		return Squander.exe(this);
	}

	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Ensures({ "this.vms = @old(this.vms) + return",//
			"return !in @old(this.vms)" })
	@Modifies({ "this.vms", "return.id" })
	@Options(ensureAllInts = true)
	public DeclarativeNode addNode() {
		return Squander.exe(this);
	}

}
